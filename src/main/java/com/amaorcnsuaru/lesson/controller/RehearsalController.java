package com.amaorcnsuaru.lesson.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.amaorcnsuaru.lesson.resource.RehearsalInstruction;
import com.amaorcnsuaru.lesson.service.RehearsalSrtService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/rehearsal")
public class RehearsalController {

	@Autowired
	private RehearsalSrtService rehearsalSrtService;

	@Autowired
	private ObjectMapper objectMapper;

	@GetMapping
	public String form() {
		return "rehearsal/form";
	}

	@PostMapping("/analyze")
	public String analyze(@RequestParam("file") MultipartFile file,
			@RequestParam(value = "youtubeUrl", required = false) String youtubeUrl, Model model,
			HttpSession session, RedirectAttributes redirectAttributes) {

		if (file.isEmpty()) {
			redirectAttributes.addFlashAttribute("error", "SRTファイルを選択してください");
			return "redirect:/rehearsal";
		}

		String filename = file.getOriginalFilename();
		if (filename == null || !filename.toLowerCase().endsWith(".srt")) {
			redirectAttributes.addFlashAttribute("error", "SRTファイル (.srt) を選択してください");
			return "redirect:/rehearsal";
		}

		try {
			List<RehearsalInstruction> instructions =
					rehearsalSrtService.analyze(file.getInputStream(), youtubeUrl);
			Map<String, Integer> instrumentSummary =
					rehearsalSrtService.countByInstrument(instructions);

			model.addAttribute("instructions", instructions);
			model.addAttribute("instrumentSummary", instrumentSummary);
			model.addAttribute("totalCount", instructions.size());
			model.addAttribute("youtubeUrl", youtubeUrl);

			// セッションに結果を保持（ダウンロード用）
			session.setAttribute("rehearsalInstructions", instructions);

			return "rehearsal/form";

		} catch (IOException e) {
			redirectAttributes.addFlashAttribute("error", "ファイルの読み込みに失敗しました: " + e.getMessage());
			return "redirect:/rehearsal";
		}
	}

	@GetMapping("/download/json")
	public ResponseEntity<byte[]> downloadJson(HttpSession session) throws Exception {
		@SuppressWarnings("unchecked")
		List<RehearsalInstruction> instructions =
				(List<RehearsalInstruction>) session.getAttribute("rehearsalInstructions");

		if (instructions == null) {
			return ResponseEntity.badRequest().build();
		}

		byte[] jsonBytes =
				objectMapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(instructions);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setContentDispositionFormData("attachment", "rehearsal_summary.json");

		return ResponseEntity.ok().headers(headers).body(jsonBytes);
	}

	@GetMapping("/download/csv")
	public ResponseEntity<byte[]> downloadCsv(HttpSession session) {
		@SuppressWarnings("unchecked")
		List<RehearsalInstruction> instructions =
				(List<RehearsalInstruction>) session.getAttribute("rehearsalInstructions");

		if (instructions == null) {
			return ResponseEntity.badRequest().build();
		}

		String csv = rehearsalSrtService.toCsv(instructions);
		byte[] csvBytes = csv.getBytes(java.nio.charset.StandardCharsets.UTF_8);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(
				new MediaType("text", "csv", java.nio.charset.StandardCharsets.UTF_8));
		headers.setContentDispositionFormData("attachment", "rehearsal_summary.csv");

		return ResponseEntity.ok().headers(headers).body(csvBytes);
	}

	@GetMapping("/download/excel")
	public ResponseEntity<byte[]> downloadExcel(HttpSession session) throws IOException {
		@SuppressWarnings("unchecked")
		List<RehearsalInstruction> instructions =
				(List<RehearsalInstruction>) session.getAttribute("rehearsalInstructions");

		if (instructions == null) {
			return ResponseEntity.badRequest().build();
		}

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		rehearsalSrtService.toExcel(instructions, baos);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.parseMediaType(
				"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
		headers.setContentDispositionFormData("attachment", "rehearsal_summary.xlsx");

		return ResponseEntity.ok().headers(headers).body(baos.toByteArray());
	}

	@GetMapping("/download/pdf")
	public ResponseEntity<byte[]> downloadPdf(HttpSession session) throws Exception {
		@SuppressWarnings("unchecked")
		List<RehearsalInstruction> instructions =
				(List<RehearsalInstruction>) session.getAttribute("rehearsalInstructions");

		if (instructions == null) {
			return ResponseEntity.badRequest().build();
		}

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		rehearsalSrtService.toPdf(instructions, baos);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_PDF);
		headers.setContentDispositionFormData("attachment", "rehearsal_summary.pdf");

		return ResponseEntity.ok().headers(headers).body(baos.toByteArray());
	}
}
