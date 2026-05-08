package com.amaorchnsuaru.manager.lesson.controller;

import com.amaorchnsuaru.manager.lesson.resource.RehearsalInstruction;
import com.amaorchnsuaru.manager.lesson.service.AiAnalysisService;
import com.amaorchnsuaru.manager.lesson.service.AiAnalysisService.AiProvider;
import com.amaorchnsuaru.manager.lesson.service.AudioTranscriptionService;
import com.amaorchnsuaru.manager.lesson.service.RehearsalSrtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/rehearsal/audio")
public class AudioController {

    /** ダウンロード用セッションキー（RehearsalController と共有） */
    private static final String SESSION_DL = "rehearsalInstructions";
    /** リアルタイム蓄積用セッションキー */
    private static final String SESSION_RT = "realtimeInstructions";

    @Autowired
    private AudioTranscriptionService transcriptionService;

    @Autowired
    private AiAnalysisService aiAnalysisService;

    @Autowired
    private RehearsalSrtService rehearsalSrtService;

    // ----------------------------------------------------------------
    // 音声ファイルアップロード
    // ----------------------------------------------------------------

    /**
     * POST /rehearsal/audio/analyze
     * 音声ファイルを受け取り、Whisper で文字起こし後に解析する。
     */
    @PostMapping("/analyze")
    public String analyzeAudio(
            @RequestParam MultipartFile audioFile,
            @RequestParam(defaultValue = "standard") String analysisMode,
            @RequestParam(defaultValue = "openai") String aiProvider,
            Model model,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (audioFile == null || audioFile.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "音声ファイルを選択してください。");
            redirectAttributes.addFlashAttribute("activeTab", "audio");
            return "redirect:/rehearsal";
        }

        try {
            String srtText = transcriptionService.transcribeToSrt(audioFile);
            List<RehearsalInstruction> instructions = doAnalyze(srtText, analysisMode, aiProvider);

            Map<String, Integer> instrumentSummary = rehearsalSrtService.countByInstrument(instructions);
            model.addAttribute("instructions", instructions);
            model.addAttribute("instrumentSummary", instrumentSummary);
            model.addAttribute("totalCount", instructions.size());
            model.addAttribute("activeTab", "audio");

            session.setAttribute(SESSION_DL, instructions);
            return "rehearsal/form";

        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            redirectAttributes.addFlashAttribute("activeTab", "audio");
            return "redirect:/rehearsal";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "音声解析に失敗しました: " + e.getMessage());
            redirectAttributes.addFlashAttribute("activeTab", "audio");
            return "redirect:/rehearsal";
        }
    }

    // ----------------------------------------------------------------
    // リアルタイム録音
    // ----------------------------------------------------------------

    /**
     * POST /rehearsal/audio/realtime-chunk
     * ブラウザから送られてくる録音チャンクを受け取り、文字起こし・解析して
     * セッションに蓄積する。レスポンスは JSON。
     */
    @PostMapping("/realtime-chunk")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> receiveChunk(
            @RequestParam MultipartFile chunk,
            @RequestParam(defaultValue = "standard") String analysisMode,
            @RequestParam(defaultValue = "openai") String aiProvider,
            HttpSession session) {

        Map<String, Object> response = new HashMap<>();
        try {
            String extension = mimeToExtension(chunk.getContentType());
            String srtText = transcriptionService.transcribeChunkToSrt(chunk.getBytes(), extension);
            List<RehearsalInstruction> newItems = doAnalyze(srtText, analysisMode, aiProvider);

            List<RehearsalInstruction> accumulated = getRealtimeList(session);
            accumulated.addAll(newItems);
            session.setAttribute(SESSION_RT, accumulated);

            response.put("success", true);
            response.put("newCount", newItems.size());
            response.put("totalCount", accumulated.size());

        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
        }
        return ResponseEntity.ok(response);
    }

    /**
     * GET /rehearsal/audio/realtime-status
     * 蓄積中の結果を JSON で返す（ポーリング用）。
     */
    @GetMapping("/realtime-status")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> realtimeStatus(HttpSession session) {
        List<RehearsalInstruction> instructions = getRealtimeList(session);
        Map<String, Object> response = new HashMap<>();
        response.put("instructions", instructions);
        response.put("totalCount", instructions.size());
        return ResponseEntity.ok(response);
    }

    /**
     * POST /rehearsal/audio/realtime-reset
     * リアルタイム蓄積データをクリアする。
     */
    @PostMapping("/realtime-reset")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> realtimeReset(HttpSession session) {
        session.removeAttribute(SESSION_RT);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        return ResponseEntity.ok(response);
    }

    /**
     * POST /rehearsal/audio/realtime-finalize
     * 蓄積結果を確定してダウンロード可能にし、結果ページを表示する。
     */
    @PostMapping("/realtime-finalize")
    public String realtimeFinalize(HttpSession session, Model model) {
        List<RehearsalInstruction> instructions = getRealtimeList(session);
        session.setAttribute(SESSION_DL, instructions);

        Map<String, Integer> instrumentSummary = rehearsalSrtService.countByInstrument(instructions);
        model.addAttribute("instructions", instructions);
        model.addAttribute("instrumentSummary", instrumentSummary);
        model.addAttribute("totalCount", instructions.size());
        model.addAttribute("activeTab", "realtime");

        return "rehearsal/form";
    }

    // ----------------------------------------------------------------
    // private helpers
    // ----------------------------------------------------------------

    private List<RehearsalInstruction> doAnalyze(
            String srtText, String analysisMode, String aiProvider) throws Exception {
        if ("ai".equals(analysisMode)) {
            AiProvider provider = "anthropic".equalsIgnoreCase(aiProvider)
                    ? AiProvider.ANTHROPIC : AiProvider.OPENAI;
            return aiAnalysisService.analyzeWithAi(srtText, provider);
        }
        // 標準: 既存の regex / キーワードマッチング
        ByteArrayInputStream srtStream =
                new ByteArrayInputStream(srtText.getBytes(StandardCharsets.UTF_8));
        return rehearsalSrtService.analyze(srtStream, null);
    }

    @SuppressWarnings("unchecked")
    private List<RehearsalInstruction> getRealtimeList(HttpSession session) {
        List<RehearsalInstruction> list =
                (List<RehearsalInstruction>) session.getAttribute(SESSION_RT);
        return list != null ? list : new ArrayList<>();
    }

    /**
     * ブラウザの録音 MIME タイプを Whisper が受け付けるファイル拡張子にマッピング。
     * Chrome: audio/webm, Safari: audio/mp4, Firefox: audio/ogg (Whisper 非対応のため webm 推奨)
     */
    private String mimeToExtension(String mimeType) {
        if (mimeType == null) return "webm";
        if (mimeType.contains("webm")) return "webm";
        if (mimeType.contains("mp4") || mimeType.contains("m4a")) return "mp4";
        if (mimeType.contains("wav")) return "wav";
        if (mimeType.contains("mpeg") || mimeType.contains("mp3")) return "mp3";
        if (mimeType.contains("ogg")) return "ogg"; // Whisper 未サポートだが一応渡す
        return "webm";
    }
}
