package com.amaorchnsuaru.manager.controller;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.amaorchnsuaru.manager.entity.ConcertData;
import com.amaorchnsuaru.manager.entity.ConcertProgram;
import com.amaorchnsuaru.manager.entity.ConcertProgramId;
import com.amaorchnsuaru.manager.entity.Music;
import com.amaorchnsuaru.manager.entity.OrchData;
import com.amaorchnsuaru.manager.entity.StageLayout;
import com.amaorchnsuaru.manager.repository.ConcertDataRepository;
import com.amaorchnsuaru.manager.repository.ConcertProgramRepository;
import com.amaorchnsuaru.manager.repository.MusicRepository;
import com.amaorchnsuaru.manager.repository.OrchDataRepository;
import com.amaorchnsuaru.manager.repository.PersonRepository;
import com.amaorchnsuaru.manager.repository.StageLayoutRepository;
import com.amaorchnsuaru.manager.service.StageLayoutService;

@Controller
@RequestMapping("/concert")
public class ConcertDataController {

    private static final int PAGE_SIZE = 30;

    private final ConcertDataRepository    concertRepo;
    private final OrchDataRepository       orchRepo;
    private final PersonRepository         personRepo;
    private final ConcertProgramRepository programRepo;
    private final MusicRepository          musicRepo;
    private final StageLayoutRepository    layoutRepo;
    private final StageLayoutService       layoutService;

    public ConcertDataController(ConcertDataRepository concertRepo,
                                 OrchDataRepository orchRepo,
                                 PersonRepository personRepo,
                                 ConcertProgramRepository programRepo,
                                 MusicRepository musicRepo,
                                 StageLayoutRepository layoutRepo,
                                 StageLayoutService layoutService) {
        this.concertRepo   = concertRepo;
        this.orchRepo      = orchRepo;
        this.personRepo    = personRepo;
        this.programRepo   = programRepo;
        this.musicRepo     = musicRepo;
        this.layoutRepo    = layoutRepo;
        this.layoutService = layoutService;
    }

    @GetMapping
    public String list(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(required = false) String orchId,
                       @RequestParam(required = false) String year,
                       @RequestParam(required = false) String musicId,
                       Model model) {
        String orchIdFilter  = (orchId != null && !orchId.isBlank())   ? orchId  : null;
        String yearFilter    = (year   != null && !year.isBlank())     ? year    : null;
        String musicIdFilter = (musicId != null && !musicId.isBlank()) ? musicId : null;
        Page<ConcertData> concertPage = concertRepo
                .findByFilters(orchIdFilter, yearFilter, musicIdFilter, PageRequest.of(page, PAGE_SIZE));
        model.addAttribute("concertPage", concertPage);
        model.addAttribute("orchNameMap", buildOrchNameMap());
        model.addAttribute("orchList", orchRepo.findAllByOrderByOrchIdAsc());
        model.addAttribute("musicList", musicRepo.findAllByOrderByComposerNameAscMusicTitleFormalJpAsc());
        model.addAttribute("selectedOrchId", orchIdFilter);
        model.addAttribute("selectedYear", yearFilter);
        model.addAttribute("selectedMusicId", musicIdFilter);
        return "concert/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("concert", new ConcertData());
        addFormOptions(model);
        model.addAttribute("isNew", true);
        return "concert/form";
    }

    @PostMapping("/new")
    public String create(@ModelAttribute ConcertData concert, RedirectAttributes ra) {
        if (concertRepo.existsById(concert.getConcertId())) {
            ra.addFlashAttribute("errorMsg", "ID " + concert.getConcertId() + " は既に存在します。");
            return "redirect:/concert/new";
        }
        concertRepo.save(concert);
        ra.addFlashAttribute("successMsg", concert.getConcertName() + " を登録しました。");
        return "redirect:/concert";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable String id, Model model) {
        ConcertData concert = concertRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("不正なID: " + id));
        model.addAttribute("concert", concert);
        addFormOptions(model);
        model.addAttribute("isNew", false);
        model.addAttribute("programs", programRepo.findByConcertIdOrderByProgramNoAsc(id));
        model.addAttribute("musicList", musicRepo.findAllByOrderByComposerNameAscMusicTitleFormalJpAsc());
        model.addAttribute("layoutList", layoutRepo.findAllByOrderByLayoutNameAsc());
        model.addAttribute("layoutNameMap", buildLayoutNameMap());
        return "concert/form";
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable String id, @ModelAttribute ConcertData concert,
                         RedirectAttributes ra) {
        concert.setConcertId(id);
        concertRepo.save(concert);
        ra.addFlashAttribute("successMsg", concert.getConcertName() + " を更新しました。");
        return "redirect:/concert";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable String id, RedirectAttributes ra) {
        concertRepo.findById(id).ifPresent(c -> {
            concertRepo.delete(c);
            ra.addFlashAttribute("successMsg", c.getConcertName() + " を削除しました。");
        });
        return "redirect:/concert";
    }

    @PostMapping("/{id}/program/add")
    @Transactional
    public String addProgram(@PathVariable String id,
                             @RequestParam(required = false) String musicId,
                             @RequestParam String musicTitleFormalJp,
                             @RequestParam(required = false) String fromPart,
                             @RequestParam(required = false) String memo,
                             RedirectAttributes ra) {
        if (musicTitleFormalJp == null || musicTitleFormalJp.isBlank()) {
            ra.addFlashAttribute("programError", "曲名は必須です。");
            return "redirect:/concert/" + id + "/edit";
        }
        int nextNo = programRepo.findMaxProgramNo(id) + 1;

        ConcertProgram prog = new ConcertProgram();
        prog.setConcertId(id);
        prog.setProgramNo(nextNo);
        prog.setMusicId(musicId != null && !musicId.isBlank() ? musicId : null);
        prog.setMusicTitleFormalJp(musicTitleFormalJp);
        prog.setFromPart(fromPart != null && !fromPart.isBlank() ? fromPart : null);
        prog.setMemo(memo != null && !memo.isBlank() ? memo : null);
        programRepo.save(prog);
        return "redirect:/concert/" + id + "/edit";
    }

    @PostMapping("/{id}/program/{programNo}/delete")
    @Transactional
    public String deleteProgram(@PathVariable String id,
                                @PathVariable Integer programNo,
                                RedirectAttributes ra) {
        programRepo.deleteByConcertIdAndProgramNo(id, programNo);
        return "redirect:/concert/" + id + "/edit";
    }

    @PostMapping("/{id}/program/{programNo}/edit")
    @Transactional
    public String editProgram(@PathVariable String id,
                              @PathVariable Integer programNo,
                              @RequestParam(required = false) String musicId,
                              @RequestParam String musicTitleFormalJp,
                              @RequestParam(required = false) String fromPart,
                              @RequestParam(required = false) String memo,
                              RedirectAttributes ra) {
        programRepo.findById(new ConcertProgramId(id, programNo))
                .ifPresent(prog -> {
                    prog.setMusicId(musicId != null && !musicId.isBlank() ? musicId : null);
                    prog.setMusicTitleFormalJp(musicTitleFormalJp);
                    prog.setFromPart(fromPart != null && !fromPart.isBlank() ? fromPart : null);
                    prog.setMemo(memo != null && !memo.isBlank() ? memo : null);
                    programRepo.save(prog);
                });
        return "redirect:/concert/" + id + "/edit";
    }

    /** 曲目に既存の舞台配置を紐付ける（layoutId 未指定なら紐付けを解除） */
    @PostMapping("/{id}/program/{programNo}/layout")
    @Transactional
    public String assignLayout(@PathVariable String id,
                               @PathVariable Integer programNo,
                               @RequestParam(required = false) Long layoutId,
                               RedirectAttributes ra) {
        programRepo.findById(new ConcertProgramId(id, programNo)).ifPresent(prog -> {
            prog.setLayoutId(layoutId != null && layoutRepo.existsById(layoutId) ? layoutId : null);
            programRepo.save(prog);
        });
        return "redirect:/concert/" + id + "/edit";
    }

    /** 曲目用の舞台配置を標準配置から新規作成し、そのまま編集画面へ遷移する */
    @PostMapping("/{id}/program/{programNo}/layout/new")
    @Transactional
    public String createLayoutForProgram(@PathVariable String id,
                                         @PathVariable Integer programNo,
                                         RedirectAttributes ra) {
        ConcertProgram prog = programRepo.findById(new ConcertProgramId(id, programNo)).orElse(null);
        if (prog == null) {
            ra.addFlashAttribute("programError", "曲目が見つかりません。");
            return "redirect:/concert/" + id + "/edit";
        }
        StageLayout layout = layoutService.create(prog.getMusicTitleFormalJp() + " の配置", true);
        prog.setLayoutId(layout.getLayoutId());
        programRepo.save(prog);
        return "redirect:/layout/" + layout.getLayoutId() + "/edit";
    }

    private Map<Long, String> buildLayoutNameMap() {
        return layoutRepo.findAllByOrderByLayoutNameAsc().stream()
                .collect(Collectors.toMap(StageLayout::getLayoutId, StageLayout::getLayoutName,
                        (a, b) -> a, LinkedHashMap::new));
    }

    private void addFormOptions(Model model) {
        model.addAttribute("orchList", orchRepo.findAllByOrderByOrchIdAsc());
        model.addAttribute("conductorList",
                personRepo.findByMainActiveInstrumentOrderByLastNameKanaEstimateAscFirstNameKanaEstimateAsc("cond"));
    }

    private Map<String, String> buildOrchNameMap() {
        return orchRepo.findAllByOrderByOrchIdAsc().stream()
                .collect(Collectors.toMap(OrchData::getOrchId, OrchData::getOrchName,
                        (a, b) -> a, LinkedHashMap::new));
    }
}
