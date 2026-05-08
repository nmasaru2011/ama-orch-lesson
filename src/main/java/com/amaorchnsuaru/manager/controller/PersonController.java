package com.amaorchnsuaru.manager.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.amaorchnsuaru.manager.entity.Person;
import com.amaorchnsuaru.manager.repository.PersonRepository;

@Controller
@RequestMapping("/person")
public class PersonController {

    private static final int PAGE_SIZE = 30;

    private final PersonRepository repo;

    public PersonController(PersonRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public String list(
            @RequestParam(defaultValue = "") String kw,
            @RequestParam(defaultValue = "0") int page,
            Model model) {

        Pageable pageable = PageRequest.of(page, PAGE_SIZE);
        Page<Person> personPage = kw.isBlank()
                ? repo.findAllByOrderByLastNameKanaEstimateAscFirstNameKanaEstimateAsc(pageable)
                : repo.search(kw, pageable);

        model.addAttribute("personPage", personPage);
        model.addAttribute("kw", kw);
        return "person/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("person", new Person());
        model.addAttribute("isNew", true);
        return "person/form";
    }

    @PostMapping("/new")
    public String create(@ModelAttribute Person person, RedirectAttributes ra) {
        if (repo.existsById(person.getPersonId())) {
            ra.addFlashAttribute("errorMsg", "ID " + person.getPersonId() + " は既に存在します。");
            return "redirect:/person/new";
        }
        repo.save(person);
        ra.addFlashAttribute("successMsg", person.getFullName() + " を登録しました。");
        return "redirect:/person";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Person person = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("不正なID: " + id));
        model.addAttribute("person", person);
        model.addAttribute("isNew", false);
        return "person/form";
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id, @ModelAttribute Person person, RedirectAttributes ra) {
        person.setPersonId(id);
        repo.save(person);
        ra.addFlashAttribute("successMsg", person.getFullName() + " を更新しました。");
        return "redirect:/person";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        repo.findById(id).ifPresent(p -> {
            repo.delete(p);
            ra.addFlashAttribute("successMsg", p.getFullName() + " を削除しました。");
        });
        return "redirect:/person";
    }
}
