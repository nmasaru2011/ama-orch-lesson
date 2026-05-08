package com.amaorchnsuaru.manager.controller;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.amaorchnsuaru.manager.entity.ConcertData;
import com.amaorchnsuaru.manager.entity.OrchData;
import com.amaorchnsuaru.manager.repository.ConcertDataRepository;
import com.amaorchnsuaru.manager.repository.OrchDataRepository;
import com.amaorchnsuaru.manager.repository.PersonRepository;

@Controller
@RequestMapping("/concert")
public class ConcertDataController {

    private static final int PAGE_SIZE = 30;

    private final ConcertDataRepository concertRepo;
    private final OrchDataRepository    orchRepo;
    private final PersonRepository      personRepo;

    public ConcertDataController(ConcertDataRepository concertRepo,
                                 OrchDataRepository orchRepo,
                                 PersonRepository personRepo) {
        this.concertRepo = concertRepo;
        this.orchRepo    = orchRepo;
        this.personRepo  = personRepo;
    }

    @GetMapping
    public String list(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(required = false) String orchId,
                       @RequestParam(required = false) String year,
                       Model model) {
        String orchIdFilter = (orchId != null && !orchId.isBlank()) ? orchId : null;
        String yearFilter   = (year   != null && !year.isBlank())   ? year   : null;
        Page<ConcertData> concertPage = concertRepo
                .findByFilters(orchIdFilter, yearFilter, PageRequest.of(page, PAGE_SIZE));
        model.addAttribute("concertPage", concertPage);
        model.addAttribute("orchNameMap", buildOrchNameMap());
        model.addAttribute("orchList", orchRepo.findAllByOrderByOrchIdAsc());
        model.addAttribute("selectedOrchId", orchIdFilter);
        model.addAttribute("selectedYear", yearFilter);
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
