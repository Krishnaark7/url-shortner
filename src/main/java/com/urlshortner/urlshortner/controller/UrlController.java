package com.urlshortner.urlshortner.controller;

import com.urlshortner.urlshortner.model.Url;
import com.urlshortner.urlshortner.service.UrlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "*")
public class UrlController {

    @Autowired
    private UrlService urlService;

    @GetMapping("/")
    public ResponseEntity<Void> home() {
        return ResponseEntity
                .status(HttpStatus.FOUND)
                .location(URI.create("/index.html"))
                .build();
    }

    @PostMapping("/api/shorten")
    public ResponseEntity<Map<String, String>> shortenUrl(@RequestBody Map<String, String> request) {
        String longUrl = request.get("longUrl");

        if (longUrl == null || longUrl.isEmpty()) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "URL cannot be empty");
            return ResponseEntity.badRequest().body(error);
        }

        Url url = urlService.shortenUrl(longUrl);

        Map<String, String> response = new HashMap<>();
        response.put("shortCode", url.getShortCode());
        response.put("shortUrl", "https://url-shortner-2qts.onrender.com/" + url.getShortCode());
        response.put("originalUrl", url.getLongUrl());
        response.put("expiresAt", url.getExpiresAt().toString());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{shortCode:(?!index\\.html).*}")
    public ResponseEntity<Void> redirect(@PathVariable String shortCode) {
        Optional<Url> urlOpt = urlService.getByShortCode(shortCode);

        if (!urlOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Url url = urlOpt.get();

        if (url.getExpiresAt().before(new Date())) {
            return ResponseEntity.status(410).build();
        }

        urlService.incrementClick(url);

        return ResponseEntity
                .status(HttpStatus.FOUND)
                .location(URI.create(url.getLongUrl()))
                .build();
    }

    @GetMapping("/api/stats/{shortCode}")
    public ResponseEntity<Map<String, Object>> getStats(@PathVariable String shortCode) {
        Optional<Url> urlOpt = urlService.getByShortCode(shortCode);

        if (!urlOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Url url = urlOpt.get();
        Map<String, Object> stats = new HashMap<>();
        stats.put("shortCode", url.getShortCode());
        stats.put("originalUrl", url.getLongUrl());
        stats.put("clickCount", url.getClickCount());
        stats.put("createdAt", url.getCreatedAt().toString());
        stats.put("expiresAt", url.getExpiresAt().toString());

        return ResponseEntity.ok(stats);
    }
}