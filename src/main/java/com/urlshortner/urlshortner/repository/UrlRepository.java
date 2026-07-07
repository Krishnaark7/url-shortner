package com.urlshortner.urlshortner.repository;

import com.urlshortner.urlshortner.model.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UrlRepository extends JpaRepository<Url, Long> {
    Optional<Url> findByLongUrl(String longUrl);
    Optional<Url> findByShortCode(String shortCode);
    Optional<Url> findByLongUrlHash(String longUrlHash);
}