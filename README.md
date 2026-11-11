# Acta

Acta is a Reddit-style community platform built as a full-stack Java project. Users can register, create communities, write posts, comment in nested threads, and upvote or downvote posts and comments. The project is split into a REST API and a desktop client.

## Repository structure

| Folder | Description |
|---|---|
| [`api-springboot/`](./api-springboot) | Spring Boot REST API with PostgreSQL, JWT authentication, and AI post summaries |
| [`javafx-client/`](./javafx-client) | JavaFX desktop client that consumes the API |

## Tech stack

**Backend**
- Java 17, Spring Boot 3.3
- Spring Data JPA / Hibernate, PostgreSQL
- Spring Security with JWT authentication
- Google Gemini API for post summarization
- Maven, Lombok

**Frontend**
- JavaFX 21 with FXML views

## Features

- User registration and login (JWT-secured)
- Communities with members
- Posts and nested comment threads
- Upvotes and downvotes on posts and comments
- AI-generated post summaries
- Global error handling with consistent JSON error responses

## Quick start

### Prerequisites
- JDK 17+
- Maven 3.6+
- PostgreSQL 12+

### 1. Run the API

1. Create a PostgreSQL database named `acta`.
2. Set the environment variables:
3. Start the server:
   ```bash
   cd api-springboot
   ./mvnw spring-boot:run
   ```
   The API runs on `http://localhost:8080`. Tables are created automatically on the first run.

### 2. Run the JavaFX client

With the API running:
```bash
cd javafx-client
mvn clean javafx:run
```

See the README in each folder for more details.

## Known issues

The JavaFX client is a work in progress: some views have bugs and not every feature of the API is available in the UI. The REST API is the more complete part of the project.

## Author

Joe Abdallah
