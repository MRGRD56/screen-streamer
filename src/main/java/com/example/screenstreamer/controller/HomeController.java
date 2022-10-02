package com.example.screenstreamer.controller;

import com.example.screenstreamer.model.config.SecuritySettings;
import com.example.screenstreamer.service.SecurityService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/")
public class HomeController {
    private final SecurityService securityService;

    public HomeController(SecurityService securityService) {
        this.securityService = securityService;
    }

    @GetMapping
    public String index(
            @RequestParam(required = false) String password,
            Model model) {
        securityService.checkPassword(password, SecuritySettings::isSecureView, HttpStatus.FORBIDDEN);

        model.addAttribute("password", password);
        return "index";
    }
}
