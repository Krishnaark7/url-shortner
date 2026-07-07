package com.urlshortner.urlshortner.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "urls", indexes = {
        @Index(name = "idx_long_url_hash", columnList = "longUrlHash")
})
public class Url {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 2000)
    private String longUrl;

    @Column(name = "long_url_hash", length = 64)
    private String longUrlHash;

    @Column(unique = true, length = 50)
    private String shortCode;

    private int clickCount;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    private Date expiresAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getLongUrl() { return longUrl; }
    public void setLongUrl(String longUrl) { this.longUrl = longUrl; }

    public String getLongUrlHash() { return longUrlHash; }
    public void setLongUrlHash(String longUrlHash) { this.longUrlHash = longUrlHash; }

    public String getShortCode() { return shortCode; }
    public void setShortCode(String shortCode) { this.shortCode = shortCode; }

    public int getClickCount() { return clickCount; }
    public void setClickCount(int clickCount) { this.clickCount = clickCount; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getExpiresAt() { return expiresAt; }
    public void setExpiresAt(Date expiresAt) { this.expiresAt = expiresAt; }
}