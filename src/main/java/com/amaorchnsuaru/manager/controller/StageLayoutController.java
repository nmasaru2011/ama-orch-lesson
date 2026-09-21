package com.amaorchnsuaru.manager.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.amaorchnsuaru.manager.entity.ConcertData;
import com.amaorchnsuaru.manager.entity.ConcertProgram;
import com.amaorchnsuaru.manager.entity.StageLayout;
import com.amaorchnsuaru.manager.entity.StageLayoutSeat;
import com.amaorchnsuaru.manager.repository.ConcertDataRepository;
import com.amaorchnsuaru.manager.repository.ConcertProgramRepository;
import com.amaorchnsuaru.manager.repository.PersonRepository;
import com.amaorchnsuaru.manager.repository.StageLayoutRepository;
import com.amaorchnsuaru.manager.repository.StageLayoutSeatRepository;
import com.amaorchnsuaru.manager.service.StageLayout3dExportService;
import com.amaorchnsuaru.manager.service.StageLayoutExportService;
import com.amaorchnsuaru.manager.service.StageLayoutService;

/**
 * 舞台配置（ステージ上の人・パートの並び）の一覧と編集画面。
 * 編集画面はドラッグで座席を動かし、まとめて JSON で保存する。
 */
@Controller
@RequestMapping("/layout")
public class StageLayoutController {

    private final StageLayoutRepository     layoutRepo;
    private final StageLayoutSeatRepository seatRepo;
    private final ConcertProgramRepository  programRepo;
    private final ConcertDataRepository     concertRepo;
    private final PersonRepository          personRepo;
    private final StageLayoutService        layoutService;
    private final StageLayoutExportService  exportService;
    private final StageLayout3dExportService export3dService;

    public StageLayoutController(StageLayoutRepository layoutRepo,
                                 StageLayoutSeatRepository seatRepo,
                                 ConcertProgramRepository programRepo,
                                 ConcertDataRepository concertRepo,
                                 PersonRepository personRepo,
                                 StageLayoutService layoutService,
                                 StageLayoutExportService exportService,
                                 StageLayout3dExportService export3dService) {
        this.layoutRepo    = layoutRepo;
        this.seatRepo      = seatRepo;
        this.programRepo   = programRepo;
        this.concertRepo   = concertRepo;
        this.personRepo    = personRepo;
        this.layoutService = layoutService;
        this.exportService = exportService;
        this.export3dService = export3dService;
    }

    /** 編集画面の人物プルダウン用（氏名とかなだけ持つ軽量データ） */
    public record PersonOption(Long id, String name, String kana, String inst) {}

    /** 一覧に出す「この配置を使っている曲」 */
    public record LayoutUsage(String concertId, Integer programNo,
                              String concertName, String musicTitle) {}

    @GetMapping
    public String list(Model model) {
        List<StageLayout> layouts = layoutRepo.findAllByOrderByLayoutNameAsc();

        Map<Long, Integer> seatCounts = new HashMap<>();
        Map<Long, List<LayoutUsage>> usages = new HashMap<>();
        for (StageLayout l : layouts) {
            seatCounts.put(l.getLayoutId(),
                    seatRepo.findByLayoutIdOrderBySeatNoAsc(l.getLayoutId()).size());
            usages.put(l.getLayoutId(), buildUsages(l.getLayoutId()));
        }
        model.addAttribute("layouts", layouts);
        model.addAttribute("seatCounts", seatCounts);
        model.addAttribute("usages", usages);
        return "layout/list";
    }

    @PostMapping("/new")
    public String create(@RequestParam(required = false) String layoutName,
                         @RequestParam(defaultValue = "true") boolean withTemplate,
                         RedirectAttributes ra) {
        StageLayout layout = layoutService.create(layoutName, withTemplate);
        ra.addFlashAttribute("successMsg", layout.getLayoutName() + " を作成しました。");
        return "redirect:/layout/" + layout.getLayoutId() + "/edit";
    }

    /** 閲覧のみ（編集不可） */
    @GetMapping("/{layoutId}")
    public String view(@PathVariable Long layoutId, Model model) {
        prepareEditor(layoutId, false, model);
        return "layout/edit";
    }

    @GetMapping("/{layoutId}/edit")
    public String edit(@PathVariable Long layoutId, Model model) {
        prepareEditor(layoutId, true, model);
        return "layout/edit";
    }

    /** 編集画面からの一括保存。座席の追加・更新・削除をまとめて反映する */
    @PostMapping("/{layoutId}/save")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> save(@PathVariable Long layoutId,
                                                    @RequestBody StageLayoutService.LayoutForm form) {
        StageLayout layout = layoutService.save(layoutId, form);
        List<StageLayoutSeat> seats = seatRepo.findByLayoutIdOrderBySeatNoAsc(layoutId);
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("layoutId", layout.getLayoutId());
        body.put("layoutName", layout.getLayoutName());
        body.put("seatCount", seats.size());
        body.put("seats", seats);
        return ResponseEntity.ok(body);
    }

    @PostMapping("/{layoutId}/template")
    public String applyTemplate(@PathVariable Long layoutId, RedirectAttributes ra) {
        layoutService.resetToStandard(layoutId);
        ra.addFlashAttribute("successMsg", "標準配置で置き換えました。");
        return "redirect:/layout/" + layoutId + "/edit";
    }

    @PostMapping("/{layoutId}/duplicate")
    public String duplicate(@PathVariable Long layoutId, RedirectAttributes ra) {
        StageLayout copy = layoutService.duplicate(layoutId);
        ra.addFlashAttribute("successMsg", copy.getLayoutName() + " を作成しました。");
        return "redirect:/layout/" + copy.getLayoutId() + "/edit";
    }

    @PostMapping("/{layoutId}/delete")
    public String delete(@PathVariable Long layoutId, RedirectAttributes ra) {
        layoutRepo.findById(layoutId).ifPresent(l -> {
            layoutService.delete(layoutId);
            ra.addFlashAttribute("successMsg", l.getLayoutName() + " を削除しました。");
        });
        return "redirect:/layout";
    }

    // =========================================================
    // 書き出し
    // =========================================================

    /** PowerPoint (.pptx)。画像ではなく編集可能な図形として出力する */
    @GetMapping("/{layoutId}/export.pptx")
    public ResponseEntity<byte[]> exportPptx(@PathVariable Long layoutId) throws IOException {
        StageLayout layout = require(layoutId);
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        exportService.writePptx(layout, seatRepo.findByLayoutIdOrderBySeatNoAsc(layoutId), buf);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.presentationml.presentation"))
                .header(HttpHeaders.CONTENT_DISPOSITION, attachment(layout, layoutId, "pptx"))
                .body(buf.toByteArray());
    }

    /** SVG。Google スライド・Keynote・Illustrator などに読ませる用 */
    @GetMapping("/{layoutId}/export.svg")
    public ResponseEntity<byte[]> exportSvg(@PathVariable Long layoutId) {
        StageLayout layout = require(layoutId);
        byte[] svg = exportService.toSvg(layout, seatRepo.findByLayoutIdOrderBySeatNoAsc(layoutId))
                .getBytes(StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("image/svg+xml; charset=UTF-8"))
                .header(HttpHeaders.CONTENT_DISPOSITION, attachment(layout, layoutId, "svg"))
                .body(svg);
    }

    /** 3MF。ペイント3D・3Dビューアーで開ける（奏者は座った簡易モデル） */
    @GetMapping("/{layoutId}/export.3mf")
    public ResponseEntity<byte[]> export3mf(@PathVariable Long layoutId) throws IOException {
        StageLayout layout = require(layoutId);
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        export3dService.write3mf(layout, seatRepo.findByLayoutIdOrderBySeatNoAsc(layoutId), buf);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("model/3mf"))
                .header(HttpHeaders.CONTENT_DISPOSITION, attachment(layout, layoutId, "3mf"))
                .body(buf.toByteArray());
    }

    /**
     * 配置名をファイル名にする。日本語が使えないブラウザ向けに ASCII の代替名も並記する
     * （RFC 6266 / RFC 5987）。
     */
    private static String attachment(StageLayout layout, Long layoutId, String ext) {
        String name = layout.getLayoutName() == null ? "" : layout.getLayoutName();
        String safe = name.replaceAll("[\\/:*?\"<>|\r\n]", "_").trim();
        if (safe.isEmpty()) {
            safe = "stage-layout-" + layoutId;
        }
        String encoded = URLEncoder.encode(safe + "." + ext, StandardCharsets.UTF_8)
                .replace("+", "%20");
        return "attachment; filename=\"stage-layout-" + layoutId + "." + ext + "\"; "
                + "filename*=UTF-8''" + encoded;
    }

    private StageLayout require(Long layoutId) {
        return layoutRepo.findById(layoutId)
                .orElseThrow(() -> new IllegalArgumentException("不正な配置ID: " + layoutId));
    }

    private void prepareEditor(Long layoutId, boolean editable, Model model) {
        StageLayout layout = require(layoutId);
        model.addAttribute("layout", layout);
        model.addAttribute("seats", seatRepo.findByLayoutIdOrderBySeatNoAsc(layoutId));
        model.addAttribute("editable", editable);
        model.addAttribute("usages", buildUsages(layoutId));
        model.addAttribute("personOptions", buildPersonOptions());
    }

    private List<LayoutUsage> buildUsages(Long layoutId) {
        return programRepo.findByLayoutId(layoutId).stream()
                .sorted((a, b) -> {
                    int c = a.getConcertId().compareTo(b.getConcertId());
                    return c != 0 ? c : Integer.compare(a.getProgramNo(), b.getProgramNo());
                })
                .map(this::toUsage)
                .toList();
    }

    private LayoutUsage toUsage(ConcertProgram prog) {
        String concertName = concertRepo.findById(prog.getConcertId())
                .map(ConcertData::getConcertName)
                .orElse(prog.getConcertId());
        return new LayoutUsage(prog.getConcertId(), prog.getProgramNo(),
                concertName, prog.getMusicTitleFormalJp());
    }

    private List<PersonOption> buildPersonOptions() {
        return personRepo.findAll().stream()
                .map(p -> new PersonOption(
                        p.getPersonId(),
                        nullToEmpty(p.getLastName()) + nullToEmpty(p.getFirstName()),
                        nullToEmpty(p.getLastNameKanaEstimate()) + nullToEmpty(p.getFirstNameKanaEstimate()),
                        nullToEmpty(p.getMainActiveInstrument())))
                .sorted((a, b) -> a.kana().compareTo(b.kana()))
                .toList();
    }

    private static String nullToEmpty(String s) {
        return s == null ? "" : s;
    }
}
