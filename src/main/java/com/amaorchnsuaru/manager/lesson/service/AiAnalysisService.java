package com.amaorchnsuaru.manager.lesson.service;

import com.amaorchnsuaru.manager.lesson.resource.RehearsalInstruction;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * SRT テキストを AI（OpenAI GPT または Anthropic Claude）で解析し、
 * 練習指摘リストを返す。
 *
 * 注意: 音声の文字起こし（Whisper）は AudioTranscriptionService が担う。
 * Anthropic Claude は音声ファイルを直接処理できないため、
 * 本サービスはテキスト解析のみを担当する。
 */
@Service
public class AiAnalysisService {

    private static final String OPENAI_CHAT_URL = "https://api.openai.com/v1/chat/completions";
    private static final String ANTHROPIC_URL = "https://api.anthropic.com/v1/messages";
    private static final String ANTHROPIC_VERSION = "2023-06-01";
    private static final String ANTHROPIC_MODEL = "claude-sonnet-4-6";
    private static final String OPENAI_MODEL = "gpt-4o-mini";

    @Value("${openai.api-key:}")
    private String openAiApiKey;

    @Value("${anthropic.api-key:}")
    private String anthropicApiKey;

    @Autowired
    private ObjectMapper objectMapper;

    private final RestTemplate restTemplate = new RestTemplate();

    public enum AiProvider {
        OPENAI, ANTHROPIC
    }

    /**
     * SRT テキストを AI で解析し、練習指摘リストを返す。
     */
    public List<RehearsalInstruction> analyzeWithAi(String srtText, AiProvider provider)
            throws Exception {
        String prompt = buildPrompt(srtText);
        String rawJson = provider == AiProvider.ANTHROPIC
                ? callAnthropic(prompt)
                : callOpenAiChat(prompt);
        return parseInstructions(rawJson);
    }

    // -------- prompt --------

    private String buildPrompt(String srtText) {
        return """
                以下は音楽練習（オーケストラ・吹奏楽等）の録音をテキスト化したものです（SRT形式）。
                指揮者・講師が楽団員・奏者に対して行った指摘・指示・注意事項を抽出してください。

                【抽出対象】
                - 音量・テンポ・音程・アーティキュレーション・タイミング・ピッチ・バランス等の音楽的指摘
                - 演奏技術・表現に関する具体的な指示

                【除外対象】
                - 挨拶・雑談・休憩の案内・曲目紹介

                【出力形式】
                以下の JSON 配列のみを出力してください（コードブロックや説明文は一切不要）：
                [
                  {
                    "timeStr": "HH:MM:SS形式の時刻文字列",
                    "totalSeconds": 秒数（整数）,
                    "measures": ["小節番号または記号（ない場合は空配列）"],
                    "instruments": ["楽器名（ない場合は空配列）"],
                    "text": "指摘内容のテキスト"
                  }
                ]

                SRT 文字起こし:
                """
                + srtText;
    }

    // -------- OpenAI --------

    private String callOpenAiChat(String prompt) {
        if (openAiApiKey == null || openAiApiKey.isBlank()) {
            throw new IllegalStateException(
                    "OpenAI APIキーが設定されていません。application.yml の openai.api-key を確認してください。");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + openAiApiKey);

        Map<String, Object> message = new LinkedHashMap<>();
        message.put("role", "user");
        message.put("content", prompt);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("model", OPENAI_MODEL);
        body.put("messages", List.of(message));
        body.put("temperature", 0.1);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        Map<?, ?> response = restTemplate.postForObject(OPENAI_CHAT_URL, request, Map.class);

        List<?> choices = (List<?>) response.get("choices");
        Map<?, ?> choice = (Map<?, ?>) choices.get(0);
        Map<?, ?> msgObj = (Map<?, ?>) choice.get("message");
        return (String) msgObj.get("content");
    }

    // -------- Anthropic --------

    private String callAnthropic(String prompt) {
        if (anthropicApiKey == null || anthropicApiKey.isBlank()) {
            throw new IllegalStateException(
                    "Anthropic APIキーが設定されていません。application.yml の anthropic.api-key を確認してください。");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-api-key", anthropicApiKey);
        headers.set("anthropic-version", ANTHROPIC_VERSION);

        Map<String, Object> message = new LinkedHashMap<>();
        message.put("role", "user");
        message.put("content", prompt);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("model", ANTHROPIC_MODEL);
        body.put("max_tokens", 4096);
        body.put("messages", List.of(message));

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        Map<?, ?> response = restTemplate.postForObject(ANTHROPIC_URL, request, Map.class);

        List<?> content = (List<?>) response.get("content");
        Map<?, ?> contentItem = (Map<?, ?>) content.get(0);
        return (String) contentItem.get("text");
    }

    // -------- JSON parsing --------

    private List<RehearsalInstruction> parseInstructions(String jsonText) throws Exception {
        String cleaned = jsonText.trim();
        // AI がコードブロックで返した場合も対応
        if (cleaned.startsWith("```")) {
            cleaned = cleaned.replaceAll("^```[a-zA-Z]*\\n?", "").replaceAll("\\n?```$", "").trim();
        }
        // JSON 配列の開始位置まで読み飛ばし（前置き文章が入った場合の保険）
        int arrayStart = cleaned.indexOf('[');
        int arrayEnd = cleaned.lastIndexOf(']');
        if (arrayStart >= 0 && arrayEnd > arrayStart) {
            cleaned = cleaned.substring(arrayStart, arrayEnd + 1);
        }

        List<Map<String, Object>> rawList = objectMapper.readValue(
                cleaned, new TypeReference<List<Map<String, Object>>>() {});

        List<RehearsalInstruction> instructions = new ArrayList<>();
        for (Map<String, Object> item : rawList) {
            String timeStr = (String) item.getOrDefault("timeStr", "00:00:00");
            long totalSeconds = ((Number) item.getOrDefault("totalSeconds", 0)).longValue();

            @SuppressWarnings("unchecked")
            List<String> measures = (List<String>) item.getOrDefault("measures", List.of());
            @SuppressWarnings("unchecked")
            List<String> instruments = (List<String>) item.getOrDefault("instruments", List.of());
            String text = (String) item.getOrDefault("text", "");

            if (text != null && !text.isBlank()) {
                instructions.add(new RehearsalInstruction(
                        timeStr, totalSeconds, measures, instruments, text, null));
            }
        }
        return instructions;
    }
}
