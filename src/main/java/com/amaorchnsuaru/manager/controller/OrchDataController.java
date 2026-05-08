package com.amaorchnsuaru.manager.controller;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.amaorchnsuaru.manager.entity.OrchData;
import com.amaorchnsuaru.manager.repository.OrchDataRepository;

@Controller
@RequestMapping("/orch")
public class OrchDataController {

    private static final Map<String, String> ORCH_TYPE_LABELS = new LinkedHashMap<>();
    static {
        ORCH_TYPE_LABELS.put("AREA",     "地域オーケストラ");
        ORCH_TYPE_LABELS.put("STUDENTS", "学生オーケストラ");
        ORCH_TYPE_LABELS.put("COMPANY",  "企業・団体");
        ORCH_TYPE_LABELS.put("EVENT",    "イベント系");
        ORCH_TYPE_LABELS.put("PERSON",   "個人");
        ORCH_TYPE_LABELS.put("PREFER",   "お気に入り");
    }

    private final OrchDataRepository repo;

    public OrchDataController(OrchDataRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("orchList", repo.findAllByOrderByOrchIdAsc());
        model.addAttribute("orchTypeLabels", ORCH_TYPE_LABELS);
        return "orch/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("orch", new OrchData());
        model.addAttribute("orchTypeLabels", ORCH_TYPE_LABELS);
        model.addAttribute("isNew", true);
        return "orch/form";
    }

    @PostMapping("/new")
    public String create(@ModelAttribute OrchData orch, RedirectAttributes ra) {
        if (repo.existsById(orch.getOrchId())) {
            ra.addFlashAttribute("errorMsg", "ID " + orch.getOrchId() + " は既に存在します。");
            return "redirect:/orch/new";
        }
        repo.save(orch);
        ra.addFlashAttribute("successMsg", orch.getOrchName() + " を登録しました。");
        return "redirect:/orch";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable String id, Model model) {
        OrchData orch = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("不正なID: " + id));
        model.addAttribute("orch", orch);
        model.addAttribute("orchTypeLabels", ORCH_TYPE_LABELS);
        model.addAttribute("isNew", false);
        return "orch/form";
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable String id, @ModelAttribute OrchData orch, RedirectAttributes ra) {
        orch.setOrchId(id);
        repo.save(orch);
        ra.addFlashAttribute("successMsg", orch.getOrchName() + " を更新しました。");
        return "redirect:/orch";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable String id, RedirectAttributes ra) {
        repo.findById(id).ifPresent(o -> {
            repo.delete(o);
            ra.addFlashAttribute("successMsg", o.getOrchName() + " を削除しました。");
        });
        return "redirect:/orch";
    }
}
