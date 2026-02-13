package com.amaorcnsuaru.lesson.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.info.GitProperties;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class FooterAdvice {

    @Value("${app.version:unknown}")
    private String appVersion;

    private final GitProperties gitProperties;

    public FooterAdvice(@Nullable GitProperties gitProperties) {
        this.gitProperties = gitProperties;
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
}
