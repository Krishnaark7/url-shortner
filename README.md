# URL Shortener

A production-ready URL shortening service built with Java Spring Boot and MySQL. Paste a long URL and get a short one instantly. Click the short URL and get redirected to the original URL.

## Live Demo
🚀 [Coming Soon — Deploying on Render]

## GitHub
[github.com/Krishnaark7/url-shortner](https://github.com/Krishnaark7/url-shortner)

## Tech Stack

| Layer | Technology |
|---|---|
| Backend | Java 17, Spring Boot 3.1.5 |
| Database | MySQL 8.0, Spring Data JPA, Hibernate |
| Frontend | HTML5, CSS3, Vanilla JavaScript |
| Containerization | Docker, Docker Compose |
| Build Tool | Maven |
| Version Control | Git, GitHub |

## Features

- Shorten any long URL to a unique 6-character code
- Instant redirect when short URL is clicked (HTTP 302)
- Click count tracking for every short URL
- Duplicate URL detection — same URL always gets same short code
- URLs automatically expire after 30 days
- REST API with 3 endpoints
- Fully containerized with Docker and Docker Compose

## How It Works

1. User pastes a long URL and clicks Shorten
2. Backend takes current timestamp in milliseconds and converts it to Base62 encoding to generate a unique 6-character short code
3. Short code and original URL are saved to MySQL database
4. When someone clicks the short URL, backend looks up the short code in MySQL
5. Backend increments click count for analytics
6. Returns HTTP 302 redirect — browser automatically navigates to original URL

## Why Base62 Encoding?
- 62 characters (a-z, A-Z, 0-9) — all URL safe, no special characters
- 6 characters gives 62^6 = 56 billion possible combinations
- Never runs out of unique codes
- Cleaner than UUID (36 chars) or random numbers

## API Endpoints

| Method | Endpoint | Description | Response |
|---|---|---|---|
| POST | /api/shorten | Shorten a long URL | 200 + shortUrl |
| GET | /{shortCode} | Redirect to original URL | 302 Redirect |
| GET | /api/stats/{shortCode} | Get click stats | 200 + stats JSON |

## Project Structure