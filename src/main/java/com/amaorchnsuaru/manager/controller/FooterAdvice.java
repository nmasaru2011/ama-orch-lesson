package com.amaorchnsuaru.manager.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.info.GitProperties;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.amaorchnsuaru.manager.repository.AppUserRepository;

@ControllerAdvice
public class FooterAdvice {

    @Value("${app.version:unknown}")
    private String appVersion;

    private final GitProperties gitProperties;
    private final AppUserRepository appUserRepository;

    public FooterAdvice(@Nullable GitProperties gitProperties, AppUserRepository appUserRepository) {
        this.gitProperties = gitProperties;
        this.appUserRepository = appUserRepository;
    }

    @ModelAttribute("footerVersion")
    public String footerVersion() {
        return appVersion;
    }

    @ModelAttribute("footerGitHash")
    public String footerGitHash() {
        if (gitProperties == null) {
            return "dev";
        }
        return gitProperties.getShortCommitId();
    }

    @ModelAttribute("footerGitCommitTime")
    public String footerGitCommitTime() {
        if (gitProperties == null) {
            return "-";
        }
        return gitProperties.getCommitTime().toString();
    }

    @ModelAttribute("displayName")
    public String displayName() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return null;
        }
        String username = auth.getName();
        return appUserRepository.findByUsername(username)
                .map(u -> u.getDisplayName() != null && !u.getDisplayName().isBlank()
                        ? u.getDisplayName()
                        : username)
                .orElse(username);
    }
}
