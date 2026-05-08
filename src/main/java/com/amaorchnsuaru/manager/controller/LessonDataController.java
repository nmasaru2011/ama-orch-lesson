package com.amaorchnsuaru.manager.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.amaorchnsuaru.manager.entity.ConcertData;
import com.amaorchnsuaru.manager.entity.LessonData;
import com.amaorchnsuaru.manager.repository.ConcertDataRepository;
import com.amaorchnsuaru.manager.repository.LessonDataRepository;
import com.amaorchnsuaru.manager.repository.OrchDataRepository;

@Controller
@RequestMapping("/lesson")
public class LessonDataController {

    private static final int PAGE_SIZE = 30;
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

    private final LessonDataRepository lessonRepo;
    private final ConcertDataRepository concertRepo;
    private final OrchDataRepository orchRepo;

    public LessonDataController(LessonDataRepository lessonRepo,
                                ConcertDataRepository concertRepo,
                                OrchDataRepository orchRepo) {
        this.lessonRepo  = lessonRepo;
        this.concertRepo = concertRepo;
        this.orchRepo    = orchRepo;
    }

    @GetMapping
    public String list(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(required = false) String concertMainId,
                       @RequestParam(required = false) String orchId,
                       @RequestParam(required = false) Integer year,
                       @RequestParam(required = false) String future,
                       Model model) {

        String concertFilter = nullIfBlank(concertMainId);
        String orchFilter    = nullIfBlank(orchId);
        String futureFilter  = nullIfBlank(future);

        Page<LessonData> lessonPage = lessonRepo.findByFilters(
                concertFilter, orchFilter, year, futureFilter,
                PageRequest.of(page, PAGE_SIZE));

        // concert_main_id → 演奏会名マップ（表示用）
        Map<String, String> concertNameMap = lessonPage.getContent().stream()
                .map(LessonData::getConcertMainId)
                .distinct()
                .collect(Collectors.toMap(
                        id -> id,
                        id -> concertRepo.findFirstByConcertMainIdOrderByConcertDateDesc(id)
                                .map(c -> c.getConcertName() != null ? c.getConcertName() : id)
                                .orElse(id),
                        (a, b) -> a,
                        LinkedHashMap::new));

        // concert_main_id → orch_id マップ（表示用）
        Map<String, String> concertOrchMap = lessonPage.getContent().stream()
                .map(LessonData::getConcertMainId)
                .distinct()
                .collect(Collectors.toMap(
                        id -> id,
                        id -> concertRepo.findFirstByConcertMainIdOrderByConcertDateDesc(id)
                                .map(ConcertData::getOrchId)
                                .orElse(""),
                        (a, b) -> a,
                        LinkedHashMap::new));

        model.addAttribute("lessonPage",           lessonPage);
        model.addAttribute("concertNameMap",        concertNameMap);
        model.addAttribute("concertOrchMap",        concertOrchMap);
        model.addAttribute("orchNameMap",           buildOrchNameMap());
        model.addAttribute("orchList",              orchRepo.findAllByOrderByOrchIdAsc());
        model.addAttribute("mainConcertList",       concertRepo.findMainConcerts());
        model.addAttribute("selectedConcertMainId", concertFilter);
        model.addAttribute("selectedOrchId",        orchFilter);
        model.addAttribute("selectedYear",          year);
        model.addAttribute("selectedFuture",        futureFilter);
        return "lesson/list";
    }

    @GetMapping("/new")
    public String newForm(@RequestParam(required = false) String concertMainId, Model model) {
        LessonData lesson = new LessonData();
        if (concertMainId != null && !concertMainId.isBlank()) {
            lesson.setConcertMainId(concertMainId);
            Integer maxBranch = lessonRepo.findMaxBranchNo(concertMainId);
            lesson.setBranchNo(maxBranch != null ? maxBranch + 1 : 1);
        }
        model.addAttribute("lesson",          lesson);
        model.addAttribute("mainConcertList", concertRepo.findMainConcerts());
        model.addAttribute("isNew",           true);
        return "lesson/form";
    }

    @PostMapping("/new")
    public String create(@ModelAttribute LessonData lesson, RedirectAttributes ra) {
        fillTimestamps(lesson);
        sanitize(lesson);
        lessonRepo.save(lesson);
        ra.addFlashAttribute("successMsg",
                lesson.getLessonDate() + " (" + lesson.getConcertMainId() + " #" + lesson.getBranchNo() + ") を登録しました。");
        return "redirect:/lesson";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        LessonData lesson = lessonRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("不正なID: " + id));
        model.addAttribute("lesson",          lesson);
        model.addAttribute("mainConcertList", concertRepo.findMainConcerts());
        model.addAttribute("isNew",           false);
        return "lesson/form";
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id,
                         @ModelAttribute LessonData lesson,
                         RedirectAttributes ra) {
        lesson.setId(id);
        fillTimestamps(lesson);
        sanitize(lesson);
        lessonRepo.save(lesson);
        ra.addFlashAttribute("successMsg",
                lesson.getLessonDate() + " (" + lesson.getConcertMainId() + " #" + lesson.getBranchNo() + ") を更新しました。");
        return "redirect:/lesson";
    }

    @GetMapping("/maxBranch")
    @ResponseBody
    public Map<String, Object> maxBranch(@RequestParam String concertMainId) {
        Integer max = lessonRepo.findMaxBranchNo(concertMainId);
        return Map.of("max", max != null ? max : 0);
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        lessonRepo.findById(id).ifPresent(l -> {
            lessonRepo.delete(l);
            ra.addFlashAttribute("successMsg",
                    l.getLessonDate() + " (" + l.getConcertMainId() + " #" + l.getBranchNo() + ") を削除しました。");
        });
        return "redirect:/lesson";
    }

    private void fillTimestamps(LessonData lesson) {
        if (lesson.getLessonDate() == null) return;
        lesson.setLessonStartTime(toDateTime(lesson.getLessonDate(), lesson.getLessonStartTimeStr()));
        lesson.setLessonEndTime(toDateTime(lesson.getLessonDate(), lesson.getLessonEndTimeStr()));
    }

    private LocalDateTime toDateTime(LocalDate date, String timeStr) {
        if (timeStr != null && !timeStr.isBlank()) {
            try {
                return LocalDateTime.of(date, LocalTime.parse(timeStr.trim(), TIME_FMT));
            } catch (Exception ignored) {}
        }
        return LocalDateTime.of(date, LocalTime.MIDNIGHT);
    }

    private void sanitize(LessonData lesson) {
        if (lesson.getPlaceName() == null) lesson.setPlaceName("");
        if (lesson.getLessonStartTimeStr() == null) lesson.setLessonStartTimeStr("");
        if (lesson.getLessonEndTimeStr()   == null) lesson.setLessonEndTimeStr("");
    }

    private String nullIfBlank(String s) {
        return (s != null && !s.isBlank()) ? s : null;
    }

    private Map<String, String> buildOrchNameMap() {
        return orchRepo.findAllByOrderByOrchIdAsc().stream()
                .collect(Collectors.toMap(
                        o -> o.getOrchId(),
                        o -> o.getOrchName(),
                        (a, b) -> a,
                        LinkedHashMap::new));
    }
}
