package com.amaorchnsuaru.manager.lesson.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class AudioTranscriptionService {

    private static final long MAX_CHUNK_BYTES = 24L * 1024 * 1024; // 24MB (Whisper上限は25MB)
    private static final String WHISPER_URL = "https://api.openai.com/v1/audio/transcriptions";

    @Value("${openai.api-key:}")
    private String openAiApiKey;

    @Autowired
    private ObjectMapper objectMapper;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * MultipartFile の音声ファイルを Whisper で文字起こしし、SRT 形式で返す。
     * 25MB 超のファイルは自動分割して処理する。
     */
    public String transcribeToSrt(MultipartFile file) throws IOException {
        validateApiKey();
        byte[] bytes = file.getBytes();
        String extension = resolveExtension(file.getOriginalFilename());
        return transcribeBytesToSrt(bytes, extension);
    }

    /**
     * バイト配列の音声チャンクを Whisper で文字起こしし、SRT 形式で返す（リアルタイム用）。
     */
    public String transcribeChunkToSrt(byte[] audioData, String extension) throws IOException {
        validateApiKey();
        return transcribeBytesToSrt(audioData, extension);
    }

    // -------- private --------

    private void validateApiKey() {
        if (openAiApiKey == null || openAiApiKey.isBlank()) {
            throw new IllegalStateException(
                    "OpenAI APIキーが設定されていません。application.yml の openai.api-key を確認してください。");
        }
    }

    private String transcribeBytesToSrt(byte[] bytes, String extension) throws IOException {
        if (bytes.length <= MAX_CHUNK_BYTES) {
            WhisperResponse resp = callWhisper(bytes, "audio." + extension);
            return segmentsToSrt(resp.segments, 1, 0.0);
        }
        return transcribeInChunks(bytes, extension);
    }

    private String transcribeInChunks(byte[] bytes, String extension) throws IOException {
        List<byte[]> chunks = "wav".equalsIgnoreCase(extension)
                ? splitWav(bytes)
                : splitGeneric(bytes);

        StringBuilder srt = new StringBuilder();
        int srtIndex = 1;
        double timeOffset = 0.0;

        for (byte[] chunk : chunks) {
            WhisperResponse resp = callWhisper(chunk, "chunk." + extension);
            srt.append(segmentsToSrt(resp.segments, srtIndex, timeOffset));
            srtIndex += resp.segments.size();
            timeOffset += resp.duration;
        }
        return srt.toString();
    }

    /** MP3 / M4A / WebM 等: バイト境界で単純分割（Whisper はフレーム欠けに対してロバスト） */
    private List<byte[]> splitGeneric(byte[] bytes) {
        List<byte[]> chunks = new ArrayList<>();
        for (long i = 0; i < bytes.length; i += MAX_CHUNK_BYTES) {
            int end = (int) Math.min(i + MAX_CHUNK_BYTES, bytes.length);
            chunks.add(Arrays.copyOfRange(bytes, (int) i, end));
        }
        return chunks;
    }

    /** WAV: 各チャンクに正しいヘッダーを付けて分割 */
    private List<byte[]> splitWav(byte[] wavBytes) {
        if (wavBytes.length < 44) {
            return splitGeneric(wavBytes);
        }
        byte[] header = Arrays.copyOfRange(wavBytes, 0, 44);
        byte[] data = Arrays.copyOfRange(wavBytes, 44, wavBytes.length);
        int chunkDataSize = (int) (MAX_CHUNK_BYTES - 44);

        List<byte[]> chunks = new ArrayList<>();
        for (int i = 0; i < data.length; i += chunkDataSize) {
            int end = Math.min(i + chunkDataSize, data.length);
            byte[] chunkData = Arrays.copyOfRange(data, i, end);
            chunks.add(buildWavChunk(header, chunkData));
        }
        return chunks;
    }

    private byte[] buildWavChunk(byte[] originalHeader, byte[] data) {
        byte[] header = Arrays.copyOf(originalHeader, 44);
        // RIFF チャンクサイズ (offset 4, LE)
        ByteBuffer.wrap(header, 4, 4).order(ByteOrder.LITTLE_ENDIAN).putInt(data.length + 36);
        // data サブチャンクサイズ (offset 40, LE)
        ByteBuffer.wrap(header, 40, 4).order(ByteOrder.LITTLE_ENDIAN).putInt(data.length);
        byte[] result = new byte[44 + data.length];
        System.arraycopy(header, 0, result, 0, 44);
        System.arraycopy(data, 0, result, 44, data.length);
        return result;
    }

    private WhisperResponse callWhisper(byte[] audioData, String filename) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.set("Authorization", "Bearer " + openAiApiKey);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("model", "whisper-1");
        body.add("language", "ja");
        body.add("response_format", "verbose_json");
        body.add("file", new NamedByteArrayResource(audioData, filename));

        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);
        String responseJson = restTemplate.postForObject(WHISPER_URL, request, String.class);
        return objectMapper.readValue(responseJson, WhisperResponse.class);
    }

    private String segmentsToSrt(List<WhisperSegment> segments, int indexStart, double offsetSec) {
        if (segments == null) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < segments.size(); i++) {
            WhisperSegment seg = segments.get(i);
            sb.append(indexStart + i).append("\n");
            sb.append(toSrtTimestamp(seg.start + offsetSec))
              .append(" --> ")
              .append(toSrtTimestamp(seg.end + offsetSec)).append("\n");
            sb.append(seg.text != null ? seg.text.trim() : "").append("\n\n");
        }
        return sb.toString();
    }

    private String toSrtTimestamp(double seconds) {
        long totalMs = (long) (seconds * 1000);
        long ms = totalMs % 1000;
        long totalSecs = totalMs / 1000;
        long secs = totalSecs % 60;
        long mins = (totalSecs / 60) % 60;
        long hours = totalSecs / 3600;
        return String.format("%02d:%02d:%02d,%03d", hours, mins, secs, ms);
    }

    private String resolveExtension(String filename) {
        if (filename == null) return "mp3";
        int dot = filename.lastIndexOf('.');
        return dot >= 0 ? filename.substring(dot + 1).toLowerCase() : "mp3";
    }

    // Whisper multipart 用: ファイル名を付与できる ByteArrayResource
    private static class NamedByteArrayResource extends ByteArrayResource {
        private final String filename;

        NamedByteArrayResource(byte[] data, String filename) {
            super(data);
            this.filename = filename;
        }

        @Override
        public String getFilename() {
            return filename;
        }
    }

    // -------- Whisper verbose_json DTOs --------

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class WhisperResponse {
        public double duration;
        public List<WhisperSegment> segments;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class WhisperSegment {
        public int id;
        public double start;
        public double end;
        public String text;
    }
}
