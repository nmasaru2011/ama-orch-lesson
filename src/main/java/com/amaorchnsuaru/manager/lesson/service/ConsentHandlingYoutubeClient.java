package com.amaorchnsuaru.manager.lesson.service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

import io.github.thoroldvix.api.TranscriptRetrievalException;
import io.github.thoroldvix.api.YoutubeClient;

/**
 * YouTube consent ページの302リダイレクトを回避するカスタムHTTPクライアント。
 * Renderなどクラウド環境ではYouTubeがconsent cookieを要求し302を返すため、
 * SOCS cookieとUser-Agentヘッダーを付与してリクエストする。
 */
public class ConsentHandlingYoutubeClient implements YoutubeClient {

    private static final String DEFAULT_ERROR_MESSAGE = "Request to YouTube failed.";
    private static final String USER_AGENT =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36";
    private static final String CONSENT_COOKIE = "SOCS=CAISNQgDEitib3FfaWRlbnRpdHlmcm9udGVuZHVpc2VydmVyXzIwMjMwODI5LjA3X3AxGgJlbiACGgYIgJnPpwY";

    private final HttpClient httpClient;

    public ConsentHandlingYoutubeClient() {
        this.httpClient = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
    }

    @Override
    public String get(String url, Map<String, String> headers) throws TranscriptRetrievalException {
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("User-Agent", USER_AGENT)
                .header("Accept-Language", "ja,en;q=0.9")
                .header("Cookie", CONSENT_COOKIE);

        for (Map.Entry<String, String> entry : headers.entrySet()) {
            requestBuilder.header(entry.getKey(), entry.getValue());
        }

        return send(requestBuilder.build());
    }

    @Override
    public String post(String url, String json) throws TranscriptRetrievalException {
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(URI.create(url))
                .header("User-Agent", USER_AGENT)
                .header("Accept-Language", "ja,en;q=0.9")
                .header("Cookie", CONSENT_COOKIE)
                .build();
        return send(request);
    }

    private String send(HttpRequest request) throws TranscriptRetrievalException {
        HttpResponse<String> response;
        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            throw new TranscriptRetrievalException(DEFAULT_ERROR_MESSAGE, e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new TranscriptRetrievalException(DEFAULT_ERROR_MESSAGE, e);
        }

        if (response.statusCode() != 200) {
            throw new TranscriptRetrievalException(
                    DEFAULT_ERROR_MESSAGE + " Status code: " + response.statusCode());
        }

        return response.body();
    }
}
