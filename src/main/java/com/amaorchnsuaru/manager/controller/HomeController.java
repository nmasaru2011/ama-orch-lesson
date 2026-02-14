package com.amaorchnsuaru.manager.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

	@GetMapping("/")
	public String index() {
		return "index";
	}

	@GetMapping("/license")
	public String license() {
		return "license";
	}

	@GetMapping("/release-notes")
	public String releaseNotes() {
		return "release-notes";
	}
}
