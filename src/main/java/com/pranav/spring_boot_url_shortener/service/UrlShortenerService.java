package com.pranav.spring_boot_url_shortener.service;

import com.pranav.spring_boot_url_shortener.model.UrlMapping;
import com.pranav.spring_boot_url_shortener.repository.UrlMappingRepository;
import org.springframework.stereotype.Service;
import java.security.SecureRandom;
import java.util.Optional;

@Service
public class UrlShortenerService {

    private final UrlMappingRepository repository;
    private final SecureRandom random = new SecureRandom();
    private static final int LENGTH = 7;
    private static final String ALLOWED_ALPHABETS =
            "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    public UrlShortenerService(UrlMappingRepository repository) {
        this.repository = repository;
    }

    public UrlMapping createShortUrl(String originalUrl) {
        if (originalUrl == null || originalUrl.trim().isEmpty()) {
            throw new IllegalArgumentException("URL cannot be empty");
        }
        String normalized = normalizeUrl(originalUrl.trim());
        Optional<UrlMapping> existing = repository.findByOriginalUrl(normalized);
        if (existing.isPresent()) {
            return existing.get();
        }
        String code = generateUniqueCode();
        UrlMapping mapping = new UrlMapping(normalized, code);
        return repository.save(mapping);
    }


    public Optional<UrlMapping> getByShortCode(String code) {
        return repository.findByShortCode(code);
    }

    public void incrementClick(UrlMapping mapping) {
        mapping.setClickCount(mapping.getClickCount() + 1);
        repository.save(mapping);
    }

    private String generateUniqueCode() {
        String code = randomCode();
        while (repository.findByShortCode(code).isPresent()) {
            code = randomCode();
        }
        return code;
    }

    private String randomCode() {
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<LENGTH; i++) {
            int index = random.nextInt(ALLOWED_ALPHABETS.length());
            sb.append(ALLOWED_ALPHABETS.charAt(index));
        }
        return sb.toString();
    }

    private String normalizeUrl(String url) {
        url = url.trim();
        if (url.startsWith("http://") || url.startsWith("https://")) {
            return url;
        }
        return "https://" + url;
    }
}
