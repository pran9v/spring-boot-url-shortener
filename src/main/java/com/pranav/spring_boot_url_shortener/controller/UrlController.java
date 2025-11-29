package com.pranav.spring_boot_url_shortener.controller;

import com.pranav.spring_boot_url_shortener.model.UrlMapping;
import com.pranav.spring_boot_url_shortener.service.UrlShortenerService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Optional;

@Controller
public class UrlController {

    private final UrlShortenerService service;

    public UrlController(UrlShortenerService service) {
        this.service = service;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("shortUrl", null);
        model.addAttribute("error", null);
        return "index";
    }

    @PostMapping("/shorten")
    public String shorten(@RequestParam("url") String url, Model model) {
        try {
            UrlMapping mapping = service.createShortUrl(url);
            model.addAttribute("shortUrl", "/u/" + mapping.getShortCode());
            model.addAttribute("createdAt", mapping.getCreatedAt());
            model.addAttribute("clickCount", mapping.getClickCount());
            model.addAttribute("error", null);
        } catch (IllegalArgumentException err) {
            model.addAttribute("shortUrl", null);
            model.addAttribute("error", err.getMessage());
        }
        return "index";
    }

    @GetMapping("/u/{code}")
    public RedirectView redirect(@PathVariable String code) {
        Optional<UrlMapping> optional = service.getByShortCode(code);
        if (optional.isEmpty()) {
            RedirectView view = new RedirectView("/");
            view.setExposeModelAttributes(false);
            return view;
        }
        UrlMapping mapping = optional.get();
        service.incrementClick(mapping);
        RedirectView view = new RedirectView(mapping.getOriginalUrl());
        view.setExposeModelAttributes(false);
        return view;
    }

}
