package com.amaorchnsuaru.manager.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

	@GetMapping("/web/login")
	public String loginPage() {
		return "login";
	}
}
