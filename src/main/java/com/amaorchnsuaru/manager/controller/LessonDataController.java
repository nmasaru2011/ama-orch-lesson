package com.amaorchnsuaru.manager.controller;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
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

    @Value("${app.concert-filter-months:4}")
    private int concertFilterMonths;

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
        model.addAttribute("mainConcertList",       concertRepo.findMainConcertsFrom(fromDateStr()));
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
        model.addAttribute("mainConcertList", concertRepo.findMainConcertsFrom(fromDateStr()));
        model.addAttribute("orchList",        orchRepo.findAllByOrderByOrchIdAsc());
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
        ra.addAttribute("concertMainId", lesson.getConcertMainId());
        return "redirect:/lesson";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        LessonData lesson = lessonRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("不正なID: " + id));

        List<ConcertData> concertList = new ArrayList<>(concertRepo.findMainConcertsFrom(fromDateStr()));
        // 編集中レッスンの演奏会が期間外でも選択肢に残す
        String currentId = lesson.getConcertMainId();
        if (currentId != null && concertList.stream().noneMatch(c -> c.getConcertMainId().equals(currentId))) {
            concertRepo.findFirstByConcertMainIdOrderByConcertDateDesc(currentId)
                    .ifPresent(c -> concertList.add(0, c));
        }

        model.addAttribute("lesson",          lesson);
        model.addAttribute("mainConcertList", concertList);
        model.addAttribute("orchList",        orchRepo.findAllByOrderByOrchIdAsc());
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
        ra.addAttribute("concertMainId", lesson.getConcertMainId());
        return "redirect:/lesson";
    }

    @GetMapping("/maxBranch")
    @ResponseBody
    public Map<String, Object> maxBranch(@RequestParam String concertMainId) {
        Integer max = lessonRepo.findMaxBranchNo(concertMainId);
        return Map.of("max", max != null ? max : 0);
    }

    @GetMapping("/export/text")
    @ResponseBody
    public ResponseEntity<String> exportText(
            @RequestParam(required = false) String concertMainId,
            @RequestParam(required = false) String orchId,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) String future) {

        String concertFilter = nullIfBlank(concertMainId);
        String orchFilter    = nullIfBlank(orchId);
        String futureFilter  = nullIfBlank(future);

        List<LessonData> lessons = lessonRepo.findAllByFilters(concertFilter, orchFilter, year, futureFilter);

        DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("M/d");
        StringBuilder sb = new StringBuilder();

        if (concertFilter != null) {
            String concertName = concertRepo.findFirstByConcertMainIdOrderByConcertDateDesc(concertFilter)
                    .map(c -> c.getConcertName() != null ? c.getConcertName() : concertFilter)
                    .orElse(concertFilter);
            sb.append("【").append(concertName).append("】練習予定\n\n");
            for (LessonData lesson : lessons) {
                appendLessonLine(sb, lesson, dateFmt);
            }
        } else {
            // 演奏会ごとにグループ化して出力
            sb.append("練習予定一覧\n\n");
            String currentId = null;
            for (LessonData lesson : lessons) {
                if (!lesson.getConcertMainId().equals(currentId)) {
                    currentId = lesson.getConcertMainId();
                    final String capturedId = currentId;
                    String name = concertRepo.findFirstByConcertMainIdOrderByConcertDateDesc(capturedId)
                            .map(c -> c.getConcertName() != null ? c.getConcertName() : capturedId)
                            .orElse(capturedId);
                    if (sb.length() > "練習予定一覧\n\n".length()) sb.append("\n");
                    sb.append("■ ").append(name).append("\n");
                }
                appendLessonLine(sb, lesson, dateFmt);
            }
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "text/plain; charset=UTF-8")
                .body(sb.toString().trim());
    }

    private void appendLessonLine(StringBuilder sb, LessonData lesson, DateTimeFormatter dateFmt) {
        String dow  = japaneseDay(lesson.getLessonDate().getDayOfWeek());
        String time = buildTimeStr(lesson);
        sb.append("#").append(lesson.getBranchNo()).append("  ")
          .append(lesson.getLessonDate().format(dateFmt))
          .append("(").append(dow).append(")");
        if (!time.isEmpty()) sb.append(" ").append(time);
        if (lesson.getPlaceName() != null && !lesson.getPlaceName().isBlank()) {
            sb.append("  ").append(lesson.getPlaceName());
        }
        sb.append("\n");
        if (lesson.getContain() != null && !lesson.getContain().isBlank()) {
            sb.append("  ").append(lesson.getContain()).append("\n");
        }
    }

    private String japaneseDay(java.time.DayOfWeek dow) {
        return switch (dow) {
            case MONDAY    -> "月";
            case TUESDAY   -> "火";
            case WEDNESDAY -> "水";
            case THURSDAY  -> "木";
            case FRIDAY    -> "金";
            case SATURDAY  -> "土";
            case SUNDAY    -> "日";
        };
    }

    private String buildTimeStr(LessonData lesson) {
        String start = lesson.getLessonStartTimeStr();
        String end   = lesson.getLessonEndTimeStr();
        if (start != null && !start.isBlank() && end != null && !end.isBlank()) {
            return start + "〜" + end;
        } else if (start != null && !start.isBlank()) {
            return start + "〜";
        }
        return "";
    }

    @GetMapping("/export/ics")
    public ResponseEntity<byte[]> exportIcs(@RequestParam(required = false) String concertMainId) {
        if (concertMainId == null || concertMainId.isBlank()) {
            byte[] msg = "演奏会を選択してからエクスポートしてください。".getBytes(StandardCharsets.UTF_8);
            return ResponseEntity.badRequest()
                    .header(HttpHeaders.CONTENT_TYPE, "text/plain; charset=UTF-8")
                    .body(msg);
        }

        List<LessonData> lessons = lessonRepo.findByConcertMainIdOrderByBranchNoAsc(concertMainId);
        String concertName = concertRepo.findFirstByConcertMainIdOrderByConcertDateDesc(concertMainId)
                .map(c -> c.getConcertName() != null ? c.getConcertName() : concertMainId)
                .orElse(concertMainId);

        DateTimeFormatter icsDate     = DateTimeFormatter.ofPattern("yyyyMMdd");
        DateTimeFormatter icsDateTime = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss");
        String dtstamp = LocalDateTime.now().format(icsDateTime);

        StringBuilder sb = new StringBuilder();
        sb.append("BEGIN:VCALENDAR\r\n");
        sb.append("VERSION:2.0\r\n");
        sb.append("PRODID:-//AMAOrchLesson//JP\r\n");
        sb.append("CALSCALE:GREGORIAN\r\n");
        sb.append("METHOD:PUBLISH\r\n");
        sb.append("X-WR-CALNAME:").append(icsEscape(concertName + " 練習予定")).append("\r\n");

        for (LessonData lesson : lessons) {
            sb.append("BEGIN:VEVENT\r\n");
            sb.append("UID:lesson-").append(lesson.getId()).append("@amaorchnsuaru\r\n");
            sb.append("DTSTAMP:").append(dtstamp).append("\r\n");

            if (lesson.getLessonStartTime() != null) {
                sb.append("DTSTART:").append(lesson.getLessonStartTime().format(icsDateTime)).append("\r\n");
                LocalDateTime end = lesson.getLessonEndTime() != null
                        ? lesson.getLessonEndTime()
                        : lesson.getLessonStartTime().plusHours(2);
                sb.append("DTEND:").append(end.format(icsDateTime)).append("\r\n");
            } else {
                sb.append("DTSTART;VALUE=DATE:").append(lesson.getLessonDate().format(icsDate)).append("\r\n");
                sb.append("DTEND;VALUE=DATE:").append(lesson.getLessonDate().plusDays(1).format(icsDate)).append("\r\n");
            }

            sb.append("SUMMARY:").append(icsEscape(concertName + " 練習 #" + lesson.getBranchNo())).append("\r\n");
            if (lesson.getPlaceName() != null && !lesson.getPlaceName().isBlank()) {
                sb.append("LOCATION:").append(icsEscape(lesson.getPlaceName())).append("\r\n");
            }
            if (lesson.getContain() != null && !lesson.getContain().isBlank()) {
                sb.append("DESCRIPTION:").append(icsEscape(lesson.getContain())).append("\r\n");
            }
            sb.append("END:VEVENT\r\n");
        }

        sb.append("END:VCALENDAR\r\n");

        byte[] icsBytes = sb.toString().getBytes(StandardCharsets.UTF_8);
        String safeId = concertMainId.replaceAll("[^A-Za-z0-9_-]", "_");

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "text/calendar; charset=UTF-8")
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"lesson_" + safeId + ".ics\"")
                .body(icsBytes);
    }

    private String icsEscape(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\")
                    .replace(";", "\\;")
                    .replace(",", "\\,")
                    .replace("\r\n", "\\n")
                    .replace("\n", "\\n")
                    .replace("\r", "\\n");
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        final String[] holder = {null};
        lessonRepo.findById(id).ifPresent(l -> {
            holder[0] = l.getConcertMainId();
            lessonRepo.delete(l);
            ra.addFlashAttribute("successMsg",
                    l.getLessonDate() + " (" + l.getConcertMainId() + " #" + l.getBranchNo() + ") を削除しました。");
        });
        if (holder[0] != null) {
            ra.addAttribute("concertMainId", holder[0]);
        }
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

    private String fromDateStr() {
        return LocalDate.now().minusMonths(concertFilterMonths)
                .format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
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
