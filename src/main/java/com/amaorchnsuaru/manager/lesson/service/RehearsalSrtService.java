package com.amaorchnsuaru.manager.lesson.service;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import com.lowagie.text.Anchor;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import com.amaorchnsuaru.manager.entity.Correction;
import com.amaorchnsuaru.manager.entity.ExcludePattern;
import com.amaorchnsuaru.manager.entity.IncludeKeyword;
import com.amaorchnsuaru.manager.entity.InstrumentKeyword;
import com.amaorchnsuaru.manager.entity.MeasurePattern;
import com.amaorchnsuaru.manager.entity.RehearsalMarkPattern;
import com.amaorchnsuaru.manager.repository.CorrectionRepository;
import com.amaorchnsuaru.manager.repository.ExcludePatternRepository;
import com.amaorchnsuaru.manager.repository.IncludeKeywordRepository;
import com.amaorchnsuaru.manager.repository.InstrumentKeywordRepository;
import com.amaorchnsuaru.manager.repository.MeasurePatternRepository;
import com.amaorchnsuaru.manager.repository.RehearsalMarkPatternRepository;
import com.amaorchnsuaru.manager.lesson.resource.RehearsalInstruction;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@Service
public class RehearsalSrtService {

	// SRTエントリの内部表現
	private static class SrtEntry {
		int num;
		long startSeconds;
		String startStr;
		String text;
	}

	private final CorrectionRepository correctionRepository;
	private final InstrumentKeywordRepository instrumentKeywordRepository;
	private final IncludeKeywordRepository includeKeywordRepository;
	private final ExcludePatternRepository excludePatternRepository;
	private final MeasurePatternRepository measurePatternRepository;
	private final RehearsalMarkPatternRepository rehearsalMarkPatternRepository;

	// DBから読み込んだデータのキャッシュ
	private List<String[]> corrections;
	private List<String[]> instrumentKeywords;
	private String[] includeKeywords;
	private Pattern[] excludePatterns;
	private Pattern[] measurePatterns;
	private Pattern rehearsalMarkPattern;

	public RehearsalSrtService(CorrectionRepository correctionRepository,
			InstrumentKeywordRepository instrumentKeywordRepository,
			IncludeKeywordRepository includeKeywordRepository,
			ExcludePatternRepository excludePatternRepository,
			MeasurePatternRepository measurePatternRepository,
			RehearsalMarkPatternRepository rehearsalMarkPatternRepository) {
		this.correctionRepository = correctionRepository;
		this.instrumentKeywordRepository = instrumentKeywordRepository;
		this.includeKeywordRepository = includeKeywordRepository;
		this.excludePatternRepository = excludePatternRepository;
		this.measurePatternRepository = measurePatternRepository;
		this.rehearsalMarkPatternRepository = rehearsalMarkPatternRepository;
	}

	@EventListener(ApplicationReadyEvent.class)
	public void loadDataFromDb() {
		// CORRECTIONS: sort_order順に読み込み
		List<Correction> correctionEntities = correctionRepository.findAllByOrderBySortOrderAsc();
		this.corrections = correctionEntities.stream()
				.map(c -> new String[] {c.getWrongText(), c.getCorrectText()})
				.collect(Collectors.toList());

		// INSTRUMENT_KEYWORDS: canonical_nameでグルーピング（順序保持）
		List<InstrumentKeyword> ikEntities = instrumentKeywordRepository.findAll();
		Map<String, List<String>> groupedKeywords = new LinkedHashMap<>();
		for (InstrumentKeyword ik : ikEntities) {
			groupedKeywords.computeIfAbsent(ik.getCanonicalName(), k -> new ArrayList<>())
					.add(ik.getKeyword());
		}
		this.instrumentKeywords = new ArrayList<>();
		for (Map.Entry<String, List<String>> entry : groupedKeywords.entrySet()) {
			List<String> row = new ArrayList<>();
			row.add(entry.getKey());
			row.addAll(entry.getValue());
			this.instrumentKeywords.add(row.toArray(new String[0]));
		}

		// INCLUDE_KEYWORDS
		List<IncludeKeyword> includeEntities = includeKeywordRepository.findAll();
		this.includeKeywords = includeEntities.stream()
				.map(IncludeKeyword::getKeyword)
				.toArray(String[]::new);

		// EXCLUDE_PATTERNS
		List<ExcludePattern> excludeEntities = excludePatternRepository.findAll();
		this.excludePatterns = excludeEntities.stream()
				.map(e -> Pattern.compile(e.getPattern()))
				.toArray(Pattern[]::new);

		// MEASURE_PATTERNS
		List<MeasurePattern> measureEntities = measurePatternRepository.findAll();
		this.measurePatterns = measureEntities.stream()
				.map(m -> Pattern.compile(m.getPattern()))
				.toArray(Pattern[]::new);

		// REHEARSAL_MARK_PATTERN
		List<RehearsalMarkPattern> rehearsalEntities = rehearsalMarkPatternRepository.findAll();
		if (!rehearsalEntities.isEmpty()) {
			this.rehearsalMarkPattern = Pattern.compile(rehearsalEntities.get(0).getPattern());
		}
	}

	/**
	 * SRTファイルを処理して練習指摘リストを返す
	 */
	public List<RehearsalInstruction> analyze(InputStream srtInputStream, String youtubeUrl)
			throws IOException {
		List<SrtEntry> entries = parseSrtFile(srtInputStream);
		String videoId = extractVideoId(youtubeUrl);

		List<RehearsalInstruction> instructions = new ArrayList<>();

		for (SrtEntry entry : entries) {
			String correctedText = correctMusicTerms(entry.text);

			if (isMeaningfulInstruction(correctedText)) {
				List<String> measures = extractMeasureNumbers(correctedText);
				List<String> instruments = extractInstruments(correctedText);
				String timeStr = formatTime(entry.startSeconds);
				String youtubeLink = buildYoutubeLink(videoId, entry.startSeconds);

				instructions.add(new RehearsalInstruction(timeStr, entry.startSeconds, measures,
						instruments, correctedText, youtubeLink));
			}
		}

		return instructions;
	}

	/**
	 * SRTタイムスタンプをパースして秒数を返す
	 */
	private long parseSrtTime(String timeStr) {
		// 形式: HH:MM:SS,mmm または HH:MM:SS.mmm
		String[] parts = timeStr.split(":");
		int h = Integer.parseInt(parts[0]);
		int m = Integer.parseInt(parts[1]);
		String[] secParts = parts[2].split("[,.]");
		int s = Integer.parseInt(secParts[0]);
		return h * 3600L + m * 60L + s;
	}

	/**
	 * 秒数を HH:MM:SS 形式にフォーマット
	 */
	private String formatTime(long totalSeconds) {
		long hours = totalSeconds / 3600;
		long minutes = (totalSeconds % 3600) / 60;
		long seconds = totalSeconds % 60;
		return String.format("%02d:%02d:%02d", hours, minutes, seconds);
	}

	/**
	 * SRTファイルをパース
	 */
	private List<SrtEntry> parseSrtFile(InputStream inputStream) throws IOException {
		List<SrtEntry> entries = new ArrayList<>();
		List<String> lines = new ArrayList<>();

		try (BufferedReader reader =
				new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
			String line;
			while ((line = reader.readLine()) != null) {
				lines.add(line.trim());
			}
		}

		int i = 0;
		while (i < lines.size()) {
			String currentLine = lines.get(i).trim();
			if (currentLine.matches("\\d+")) {
				int entryNum = Integer.parseInt(currentLine);
				i++;

				if (i < lines.size() && lines.get(i).contains(" --> ")) {
					String timestamps = lines.get(i).trim();
					String[] timeParts = timestamps.split(" --> ");
					long startSeconds = parseSrtTime(timeParts[0].trim());
					i++;

					List<String> textLines = new ArrayList<>();
					while (i < lines.size() && !lines.get(i).trim().isEmpty()) {
						textLines.add(lines.get(i));
						i++;
					}

					if (!textLines.isEmpty()) {
						SrtEntry entry = new SrtEntry();
						entry.num = entryNum;
						entry.startSeconds = startSeconds;
						entry.startStr = timeParts[0].trim();
						entry.text = textLines.get(0);
						entries.add(entry);
					}
				}
			}
			i++;
		}

		return entries;
	}

	/**
	 * 音楽用語の誤字を修正
	 */
	private String correctMusicTerms(String text) {
		for (String[] correction : corrections) {
			text = text.replace(correction[0], correction[1]);
		}
		return text;
	}

	/**
	 * 小節番号を抽出
	 */
	private List<String> extractMeasureNumbers(String text) {
		Set<String> measures = new HashSet<>();

		for (Pattern pattern : measurePatterns) {
			Matcher matcher = pattern.matcher(text);
			while (matcher.find()) {
				measures.add(matcher.group(1));
			}
		}

		if (rehearsalMarkPattern != null) {
			Matcher rehearsalMatcher = rehearsalMarkPattern.matcher(text);
			while (rehearsalMatcher.find()) {
				String mark = rehearsalMatcher.group(1);
				if (mark.length() == 1 && Character.isUpperCase(mark.charAt(0))) {
					measures.add(mark);
				}
			}
		}

		return new ArrayList<>(measures);
	}

	/**
	 * 楽器を抽出
	 */
	private List<String> extractInstruments(String text) {
		Set<String> instruments = new LinkedHashSet<>();

		for (String[] entry : instrumentKeywords) {
			String instrumentName = entry[0];
			for (int i = 1; i < entry.length; i++) {
				if (text.contains(entry[i])) {
					instruments.add(instrumentName);
					break;
				}
			}
		}

		return new ArrayList<>(instruments);
	}

	/**
	 * 意味のある指摘かどうかを判定
	 */
	private boolean isMeaningfulInstruction(String text) {
		// 除外パターンチェック
		for (Pattern pattern : excludePatterns) {
			Matcher matcher = pattern.matcher(text);
			if (pattern.pattern().startsWith("^")) {
				if (matcher.matches()) {
					return false;
				}
			} else {
				if (matcher.find()) {
					return false;
				}
			}
		}

		// 最低文字数
		if (text.length() < 8) {
			return false;
		}

		// 含むべきキーワード
		for (String keyword : includeKeywords) {
			if (text.contains(keyword)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * YouTube URLから video ID を抽出
	 */
	private String extractVideoId(String youtubeUrl) {
		if (youtubeUrl == null || youtubeUrl.isBlank()) {
			return null;
		}

		// https://www.youtube.com/watch?v=XXXXX
		Pattern p1 = Pattern.compile("[?&]v=([a-zA-Z0-9_-]{11})");
		Matcher m1 = p1.matcher(youtubeUrl);
		if (m1.find()) {
			return m1.group(1);
		}

		// https://youtu.be/XXXXX
		Pattern p2 = Pattern.compile("youtu\\.be/([a-zA-Z0-9_-]{11})");
		Matcher m2 = p2.matcher(youtubeUrl);
		if (m2.find()) {
			return m2.group(1);
		}

		return null;
	}

	/**
	 * YouTubeタイムスタンプ付きリンクを生成
	 */
	private String buildYoutubeLink(String videoId, long totalSeconds) {
		if (videoId == null) {
			return null;
		}
		return "https://www.youtube.com/watch?v=" + videoId + "&t=" + totalSeconds + "s";
	}

	/**
	 * 結果をCSV文字列として出力
	 */
	public String toCsv(List<RehearsalInstruction> instructions) {
		StringBuilder sb = new StringBuilder();
		// BOM for Excel
		sb.append('\uFEFF');
		sb.append("時刻,小節,楽器,指摘内容,YouTubeリンク\n");
		for (RehearsalInstruction inst : instructions) {
			sb.append(escapeCsv(inst.getTimeStr())).append(",");
			sb.append(escapeCsv(inst.getMeasuresJoined())).append(",");
			sb.append(escapeCsv(inst.getInstrumentsJoined())).append(",");
			sb.append(escapeCsv(inst.getText())).append(",");
			sb.append(escapeCsv(inst.getYoutubeLink() != null ? inst.getYoutubeLink() : ""))
					.append("\n");
		}
		return sb.toString();
	}

	private String escapeCsv(String value) {
		if (value == null) {
			return "";
		}
		if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
			return "\"" + value.replace("\"", "\"\"") + "\"";
		}
		return value;
	}

	/**
	 * 楽器別サマリーを集計
	 */
	public Map<String, Integer> countByInstrument(List<RehearsalInstruction> instructions) {
		Map<String, Integer> counts = new LinkedHashMap<>();
		for (RehearsalInstruction inst : instructions) {
			if (inst.getInstruments() != null) {
				for (String instrument : inst.getInstruments()) {
					counts.merge(instrument, 1, Integer::sum);
				}
			}
		}
		return counts;
	}

	/**
	 * Excel形式で出力
	 */
	public void toExcel(List<RehearsalInstruction> instructions, OutputStream outputStream)
			throws IOException {
		try (Workbook workbook = new XSSFWorkbook()) {
			Sheet sheet = workbook.createSheet("練習まとめ");

			// ヘッダースタイル
			CellStyle headerStyle = workbook.createCellStyle();
			Font headerFont = workbook.createFont();
			headerFont.setBold(true);
			headerFont.setColor(IndexedColors.WHITE.getIndex());
			headerFont.setFontHeightInPoints((short) 11);
			headerStyle.setFont(headerFont);
			headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
			headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			headerStyle.setAlignment(HorizontalAlignment.CENTER);
			headerStyle.setBorderBottom(BorderStyle.THIN);
			headerStyle.setBorderTop(BorderStyle.THIN);
			headerStyle.setBorderLeft(BorderStyle.THIN);
			headerStyle.setBorderRight(BorderStyle.THIN);

			// データセルスタイル
			CellStyle dataStyle = workbook.createCellStyle();
			dataStyle.setBorderBottom(BorderStyle.THIN);
			dataStyle.setBorderTop(BorderStyle.THIN);
			dataStyle.setBorderLeft(BorderStyle.THIN);
			dataStyle.setBorderRight(BorderStyle.THIN);
			dataStyle.setWrapText(true);

			// ヘッダー行
			String[] headers = {"時刻", "小節", "楽器", "指摘内容", "YouTubeリンク"};
			Row headerRow = sheet.createRow(0);
			for (int i = 0; i < headers.length; i++) {
				Cell cell = headerRow.createCell(i);
				cell.setCellValue(headers[i]);
				cell.setCellStyle(headerStyle);
			}

			// データ行
			int rowNum = 1;
			for (RehearsalInstruction inst : instructions) {
				Row row = sheet.createRow(rowNum++);

				Cell c0 = row.createCell(0);
				c0.setCellValue(inst.getTimeStr());
				c0.setCellStyle(dataStyle);

				Cell c1 = row.createCell(1);
				c1.setCellValue(inst.getMeasuresJoined());
				c1.setCellStyle(dataStyle);

				Cell c2 = row.createCell(2);
				c2.setCellValue(inst.getInstrumentsJoined());
				c2.setCellStyle(dataStyle);

				Cell c3 = row.createCell(3);
				c3.setCellValue(inst.getText());
				c3.setCellStyle(dataStyle);

				Cell c4 = row.createCell(4);
				c4.setCellValue(inst.getYoutubeLink() != null ? inst.getYoutubeLink() : "");
				c4.setCellStyle(dataStyle);
			}

			// 列幅調整
			sheet.setColumnWidth(0, 3500); // 時刻
			sheet.setColumnWidth(1, 3000); // 小節
			sheet.setColumnWidth(2, 5000); // 楽器
			sheet.setColumnWidth(3, 15000); // 指摘内容
			sheet.setColumnWidth(4, 12000); // YouTubeリンク

			workbook.write(outputStream);
		}
	}

	/**
	 * PDF形式で出力
	 */
	public void toPdf(List<RehearsalInstruction> instructions, OutputStream outputStream)
			throws Exception {
		Document document = new Document(PageSize.A4.rotate(), 20, 20, 20, 20);
		PdfWriter.getInstance(document, outputStream);
		document.open();

		// 日本語フォント設定
		BaseFont baseFont =
				BaseFont.createFont("HeiseiKakuGo-W5", "UniJIS-UCS2-H", BaseFont.NOT_EMBEDDED);
		com.lowagie.text.Font titleFont =
				new com.lowagie.text.Font(baseFont, 16, com.lowagie.text.Font.BOLD);
		com.lowagie.text.Font headerFont =
				new com.lowagie.text.Font(baseFont, 9, com.lowagie.text.Font.BOLD, Color.WHITE);
		com.lowagie.text.Font cellFont = new com.lowagie.text.Font(baseFont, 8);

		// タイトル
		Paragraph title = new Paragraph("練習まとめ (" + instructions.size() + "件)", titleFont);
		title.setAlignment(Element.ALIGN_CENTER);
		title.setSpacingAfter(15);
		document.add(title);

		// テーブル（時刻列にリンクを埋め込むのでYouTubeリンク列は不要）
		PdfPTable table = new PdfPTable(new float[] {10f, 8f, 12f, 70f});
		table.setWidthPercentage(100);

		// リンク用フォント（青色・下線）
		com.lowagie.text.Font linkFont = new com.lowagie.text.Font(baseFont, 8,
				com.lowagie.text.Font.UNDERLINE, new Color(30, 80, 220));

		// ヘッダー
		Color headerBg = new Color(102, 126, 234);
		String[] pdfHeaders = {"時刻", "小節", "楽器", "指摘内容"};
		for (String header : pdfHeaders) {
			PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
			cell.setBackgroundColor(headerBg);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setPadding(5);
			table.addCell(cell);
		}

		// データ行
		boolean alternate = false;
		Color altBg = new Color(245, 245, 255);
		for (RehearsalInstruction inst : instructions) {
			Color rowBg = alternate ? altBg : Color.WHITE;

			// 時刻列: YouTubeリンクがあればクリッカブルリンクにする
			if (inst.getYoutubeLink() != null) {
				Anchor anchor = new Anchor(inst.getTimeStr(), linkFont);
				anchor.setReference(inst.getYoutubeLink());
				PdfPCell timeCell = new PdfPCell(anchor);
				timeCell.setBackgroundColor(rowBg);
				timeCell.setPadding(4);
				table.addCell(timeCell);
			} else {
				addPdfCell(table, inst.getTimeStr(), cellFont, rowBg);
			}

			addPdfCell(table, inst.getMeasuresJoined(), cellFont, rowBg);
			addPdfCell(table, inst.getInstrumentsJoined(), cellFont, rowBg);
			addPdfCell(table, inst.getText(), cellFont, rowBg);

			alternate = !alternate;
		}

		document.add(table);
		document.close();
	}

	private void addPdfCell(PdfPTable table, String text, com.lowagie.text.Font font,
			Color bgColor) {
		PdfPCell cell = new PdfPCell(new Phrase(text, font));
		cell.setBackgroundColor(bgColor);
		cell.setPadding(4);
		table.addCell(cell);
	}
}
