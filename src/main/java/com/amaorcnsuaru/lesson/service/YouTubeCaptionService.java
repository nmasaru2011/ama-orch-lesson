package com.amaorcnsuaru.lesson.service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import io.github.thoroldvix.api.TranscriptApiFactory;
import io.github.thoroldvix.api.TranscriptContent;
import io.github.thoroldvix.api.TranscriptFormatter;
import io.github.thoroldvix.api.TranscriptFormatters;
import io.github.thoroldvix.api.TranscriptList;
import io.github.thoroldvix.api.YoutubeTranscriptApi;

@Service
public class YouTubeCaptionService {

	private static final Logger log = LoggerFactory.getLogger(YouTubeCaptionService.class);

	private final YoutubeTranscriptApi api;
	private final TranscriptFormatter srtFormatter;

	public YouTubeCaptionService() {
		this.api = TranscriptApiFactory.createDefault();
		this.srtFormatter = TranscriptFormatters.srtFormatter();
	}

	/**
	 * YouTube動画から字幕をSRT形式で取得する
	 * 日本語字幕を優先し、見つからない場合は他の言語にフォールバック
	 */
	public String fetchCaptionAsSrt(String youtubeUrl) throws YouTubeCaptionException {
		String videoId = extractVideoId(youtubeUrl);
		if (videoId == null) {
			throw new YouTubeCaptionException("有効なYouTube URLを入力してください");
		}

		try {
			TranscriptList transcriptList = api.listTranscripts(videoId);

			// 日本語を優先、なければ英語にフォールバック
			TranscriptContent content = transcriptList.findTranscript("ja", "en").fetch();

			String srtContent = srtFormatter.format(content);
			if (srtContent == null || srtContent.isBlank()) {
				throw new YouTubeCaptionException("字幕データが空でした");
			}

			return srtContent;
		} catch (YouTubeCaptionException e) {
			throw e;
		} catch (Exception e) {
			log.error("YouTube字幕の取得に失敗しました: videoId={}", videoId, e);
			throw new YouTubeCaptionException(
					"YouTube字幕の取得に失敗しました: " + e.getMessage(), e);
		}
	}

	private String extractVideoId(String url) {
		if (url == null || url.isBlank()) {
			return null;
		}

		// https://www.youtube.com/watch?v=XXXXX
		Pattern p1 = Pattern.compile("[?&]v=([a-zA-Z0-9_-]{11})");
		Matcher m1 = p1.matcher(url);
		if (m1.find()) {
			return m1.group(1);
		}

		// https://youtu.be/XXXXX
		Pattern p2 = Pattern.compile("youtu\\.be/([a-zA-Z0-9_-]{11})");
		Matcher m2 = p2.matcher(url);
		if (m2.find()) {
			return m2.group(1);
		}

		return null;
	}
}
