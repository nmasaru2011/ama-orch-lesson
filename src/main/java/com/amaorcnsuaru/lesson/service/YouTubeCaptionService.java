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
	private static final int MAX_RETRIES = 3;
	private static final long RETRY_DELAY_MS = 1000;

	static {
		// グローバルなHTTPリダイレクト設定
		System.setProperty("http.maxRedirects", "5");
		System.setProperty("https.maxRedirects", "5");
		System.setProperty("sun.net.http.allowRestrictedHeaders", "true");
		System.setProperty("java.net.useSystemProxies", "true");
	}

	public YouTubeCaptionService() {
		this.api = TranscriptApiFactory.createDefault();
		this.srtFormatter = TranscriptFormatters.srtFormatter();
	}

	/**
	 * YouTube動画から字幕をSRT形式で取得する 日本語字幕を優先し、見つからない場合は他の言語にフォールバック
	 *
	 * @param youtubeUrl YouTube動画のURL
	 * @return SRT形式の字幕コンテンツ
	 * @throws YouTubeCaptionException 取得に失敗した場合
	 */
	public String fetchCaptionAsSrt(String youtubeUrl) throws YouTubeCaptionException {
		String videoId = extractVideoId(youtubeUrl);
		if (videoId == null) {
			throw new YouTubeCaptionException("有効なYouTube URLを入力してください");
		}

		return fetchCaptionWithRetry(videoId, 0);
	}

	/**
	 * リトライロジック付きで字幕を取得
	 */
	private String fetchCaptionWithRetry(String videoId, int retryCount)
			throws YouTubeCaptionException {
		try {
			return doFetchCaption(videoId);
		} catch (Exception e) {
			if (retryCount < MAX_RETRIES) {
				log.warn("YouTube字幕の取得に失敗しました (試行 {}/{}): videoId={}, error={}", retryCount + 1,
						MAX_RETRIES, videoId, e.getMessage());

				try {
					// リトライ前に待機（指数バックオフ）
					long delayMs = RETRY_DELAY_MS * (long) Math.pow(2, retryCount);
					Thread.sleep(delayMs);
				} catch (InterruptedException ie) {
					Thread.currentThread().interrupt();
				}

				return fetchCaptionWithRetry(videoId, retryCount + 1);
			} else {
				log.error("YouTube字幕の取得に複数回失敗しました (最大試行回数に達しました): videoId={}", videoId, e);
				throw new YouTubeCaptionException(
						"YouTube字幕の取得に失敗しました (Render環境での最大リトライ回数超過): " + e.getMessage(), e);
			}
		}
	}

	/**
	 * 実際の取得処理
	 */
	private String doFetchCaption(String videoId) throws Exception {
		TranscriptList transcriptList = api.listTranscripts(videoId);

		// 日本語を優先、なければ英語にフォールバック
		TranscriptContent content = transcriptList.findTranscript("ja", "en").fetch();

		String srtContent = srtFormatter.format(content);
		if (srtContent == null || srtContent.isBlank()) {
			throw new Exception("字幕データが空でした");
		}

		return srtContent;
	}

	/**
	 * YouTubeのURLからビデオIDを抽出
	 */
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
