package com.urlshortner.urlshortner.service;

import com.urlshortner.urlshortner.model.Url;
import com.urlshortner.urlshortner.repository.UrlRepository;
import com.urlshortner.urlshortner.util.UrlValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.util.Date;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class UrlService {

    @Autowired
    private UrlRepository urlRepository;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String BASE62 = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final String HASH_PREFIX = "url:hash:";
    private static final String CODE_PREFIX = "url:code:";
    private static final long THIRTY_DAYS_SECONDS = 30L * 24 * 60 * 60;

    private static final Set<String> RESERVED_WORDS = Set.of(
            "api", "admin", "shorten", "index.html", "stats", "static", "assets", "favicon.ico"
    );

    // ---------- Random (auto-generated) shorten ----------
    public Url shortenUrl(String longUrl) {
        if (!UrlValidator.isValid(longUrl)) {
            throw new IllegalArgumentException("Please enter a valid http:// or https:// URL.");
        }
        String trimmedUrl = longUrl.trim();
        String hash = sha256(trimmedUrl);

        String cachedCode = redisTemplate.opsForValue().get(HASH_PREFIX + hash);
        if (cachedCode != null) {
            Optional<Url> cachedUrl = urlRepository.findByShortCode(cachedCode);
            if (cachedUrl.isPresent()) {
                return cachedUrl.get();
            }
        }

        Optional<Url> existing = urlRepository.findByLongUrlHash(hash);
        if (existing.isPresent()) {
            cacheUrl(existing.get());
            return existing.get();
        }

        Url url = new Url();
        url.setLongUrl(trimmedUrl);
        url.setLongUrlHash(hash);
        url.setClickCount(0);
        url.setCreatedAt(new Date());
        url.setExpiresAt(new Date(System.currentTimeMillis() + THIRTY_DAYS_SECONDS * 1000));
        url = urlRepository.save(url);

        String shortCode = encodeBase62(url.getId());
        url.setShortCode(shortCode);
        url = urlRepository.save(url);

        cacheUrl(url);
        return url;
    }

    // ---------- Custom alias shorten ----------
    public Url shortenUrlCustom(String longUrl, String alias) {
        if (!UrlValidator.isValid(longUrl)) {
            throw new IllegalArgumentException("Please enter a valid http:// or https:// URL.");
        }
        if (!isValidAlias(alias)) {
            throw new IllegalArgumentException("Custom URL must be 3-30 characters: letters, numbers, hyphens, underscores only.");
        }
        if (RESERVED_WORDS.contains(alias.toLowerCase())) {
            throw new IllegalArgumentException("This custom URL is reserved. Please choose another.");
        }
        if (urlRepository.findByShortCode(alias).isPresent()) {
            throw new IllegalArgumentException("This custom URL is already taken. Please choose another.");
        }

        String trimmedUrl = longUrl.trim();
        String hash = sha256(trimmedUrl);

        Url url = new Url();
        url.setLongUrl(trimmedUrl);
        url.setLongUrlHash(hash);
        url.setShortCode(alias);
        url.setClickCount(0);
        url.setCreatedAt(new Date());
        url.setExpiresAt(new Date(System.currentTimeMillis() + THIRTY_DAYS_SECONDS * 1000));
        url = urlRepository.save(url);

        cacheUrl(url);
        return url;
    }

    // ---------- Redirect lookup ----------
    public Optional<Url> getByShortCode(String shortCode) {
        return urlRepository.findByShortCode(shortCode);
    }

    public void incrementClick(Url url) {
        url.setClickCount(url.getClickCount() + 1);
        urlRepository.save(url);
    }

    // ---------- Helpers ----------
    private void cacheUrl(Url url) {
        redisTemplate.opsForValue().set(HASH_PREFIX + url.getLongUrlHash(), url.getShortCode(), THIRTY_DAYS_SECONDS, TimeUnit.SECONDS);
        redisTemplate.opsForValue().set(CODE_PREFIX + url.getShortCode(), url.getLongUrl(), THIRTY_DAYS_SECONDS, TimeUnit.SECONDS);
    }

    private boolean isValidAlias(String alias) {
        if (alias == null) return false;
        return alias.matches("^[a-zA-Z0-9_-]{3,30}$");
    }

    private String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(input.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String encodeBase62(Long id) {
        if (id == 0) return String.valueOf(BASE62.charAt(0));
        StringBuilder sb = new StringBuilder();
        long number = id;
        while (number > 0) {
            sb.append(BASE62.charAt((int) (number % 62)));
            number /= 62;
        }
        return sb.reverse().toString();
    }
}