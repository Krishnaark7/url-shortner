package com.urlshortner.urlshortner.service;

import com.urlshortner.urlshortner.model.Url;
import com.urlshortner.urlshortner.repository.UrlRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UrlService {

    @Autowired
    private UrlRepository urlRepository;

    private static final String BASE62 = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    public Url shortenUrl(String longUrl) {
        Optional<Url> existing = urlRepository.findByLongUrl(longUrl);
        if (existing.isPresent()) {
            return existing.get();
        }

        String shortCode = generateShortCode();

        Url url = new Url();
        url.setLongUrl(longUrl);
        url.setShortCode(shortCode);
        url.setClickCount(0);
        url.setCreatedAt(LocalDateTime.now());
        url.setExpiresAt(LocalDateTime.now().plusDays(30));

        return urlRepository.save(url);
    }

    public Optional<Url> getByShortCode(String shortCode) {
        return urlRepository.findByShortCode(shortCode);
    }

    public void incrementClick(Url url) {
        url.setClickCount(url.getClickCount() + 1);
        urlRepository.save(url);
    }

    private String generateShortCode() {
        long number = System.currentTimeMillis();
        StringBuilder shortCode = new StringBuilder();
        while (number > 0) {
            shortCode.append(BASE62.charAt((int)(number % 62)));
            number = number / 62;
        }
        return shortCode.reverse().toString().substring(0, 6);
    }
}