# URL Shortener

A URL shortening service built with Java Spring Boot and MySQL. Paste a long URL and get a short one instantly. Click the short URL and get redirected to the original.

## Tech Stack

- **Backend:** Java 17, Spring Boot 3.1.5
- **Database:** MySQL 8.0 with Spring Data JPA
- **Frontend:** HTML, CSS, Vanilla JavaScript
- **Build Tool:** Maven

## Features

- Shorten any long URL to a 6-character code
- Instant redirect when short URL is clicked
- Click count tracking for every short URL
- Duplicate URL detection - same URL always gets same short code
- URLs expire after 30 days
- REST API with 3 endpoints

## How It Works

1. User pastes a long URL and clicks Shorten
2. Backend generates a unique 6-character code using Base62 encoding of current timestamp
3. Short code and original URL are saved to MySQL database
4. When short URL is clicked, backend looks up the code in database and returns a 302 redirect
5. Browser follows redirect and lands on original URL

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /api/shorten | Shorten a long URL |
| GET | /{shortCode} | Redirect to original URL |
| GET | /api/stats/{shortCode} | Get click stats for a URL |

## How to Run Locally

### Prerequisites
- Java 17
- MySQL 8.0
- Maven

### Steps

1. Clone the repository