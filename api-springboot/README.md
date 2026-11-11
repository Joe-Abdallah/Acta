# Acta BACKEND

Project: Spring Boot REST API for a community platform (posts, comments, votes, communities)
Author: Joe Abdallah
Tech stack: Java 17, Spring Boot 3.3.3, PostgreSQL, Maven, JPA/Hibernate, Lombok


## PREREQUISITES

- Java 17 or higher (JDK 17+)
- Maven 3.6 or higher
- PostgreSQL 12+ (or any relational DB, but PostgreSQL is used by default)
- Git (optional, for cloning)

## DATABASE SETUP

1. Create a PostgreSQL database ("acta")
2. Update `src/main/resources/application.yml` with your database credentials:
   spring.datasource.url=jdbc:postgresql://localhost:5432/springpostgrescrud
   spring.datasource.username=your_username
   spring.datasource.password=your_password
3. The application will automatically create tables (ddl-auto=update is configured)


## BUILD AND RUN

1. Open a terminal in the project root directory (where pom.xml is located)
2. Build the project:
   ```bash
   mvn clean install
   ```
3. Run the application:
   ```bash
   mvn spring-boot:run
   ```
   OR run the main class: com.csis231.springpostgrescrud.SpringPostgresCrudApplication
4. The server starts on http://localhost:8080

## API ENDPOINTS OVERVIEW

All endpoints return JSON. Error responses follow a standard format with status, message, timestamp.

1. CLIENT (user registration & management)
   - `POST   /api/clients` – register new client
   - `GET    /api/clients/{id}` – get client by ID
   - `GET    /api/clients` – get all clients
   - `PUT    /api/clients/{id}` – update client (username, email, password)
   - `DELETE /api/clients/{id}` – delete client (removes from communities automatically)
   - `POST   /api/clients/authenticate` – login (body: LoginDto with username/email + password)
   - `GET    /api/clients/username/{username}` – find by username
   - `GET    /api/clients/email/{email}` – find by email

2. COMMUNITY
   - `POST   /api/communities             `– create new community (creator becomes first member)
   - `GET    /api/communities/{id}        `– get community by ID (includes memberCount)
   - `GET    /api/communities             `– get all communities
   - `PATCH  /api/communities/{id}        `– update description (send plain text body)
   - `DELETE /api/communities/{id}        `– delete community (cascades to posts/comments)

3. POST
   - `POST   /api/posts`– create a new post (needs authorId, communityId)
   - `GET    /api/posts/{id}`– get post by ID
   - `GET    /api/posts`– get all posts
   - `PUT    /api/posts/{id}`– update title/content
   - `DELETE /api/posts/{id}`– delete post (cascades to comments/votes)

4. COMMENT
   - `POST   /api/comments`– create a comment (provide content, authorId, postId, optional parentCommentId)
   - `GET    /api/comments/{id}`– get comment by ID
   - `GET    /api/comments/post/{postId}`– get all top‑level comments for a post (replies nested inside)
   - `PUT    /api/comments/{id}`– update content (send plain text body)
   - `DELETE /api/comments/{id}`– delete comment (cascades to replies, updates post.commentCount)

5. POST VOTE
   - `POST   /api/post-votes?postId=X&clientId=Y&voteType=UPVOTE/DOWNVOTE` – cast/change vote
   - `GET    /api/post-votes?postId=X&clientId=Y` – get a vote for a client on a post
   - `DELETE /api/post-votes?postId=X&clientId=Y` – remove vote

6. COMMENT VOTE
   - `POST   /api/comment-votes?commentId=X&clientId=Y&voteType=UPVOTE/DOWNVOTE` – cast/change vote
   - `GET    /api/comment-votes?commentId=X&clientId=Y` – get vote
   - `DELETE /api/comment-votes?commentId=X&clientId=Y` – remove vote

## PROJECT STRUCTURE
```
src/main/java/com/csis231/springpostgrescrud/
├── entity/         – JPA entities (Client, Post, Comment, Community, PostVote, CommentVote)
├── dto/            – Data Transfer Objects (ClientDto, PostDto, ...)
├── repository/     – Spring Data JPA repositories
├── service/        – Business logic (interfaces + implementations)
├── controller/     – REST controllers
├── exception/      – Custom exceptions and global exception handler
├── mapper/         – Utility classes for entity ↔ DTO conversion
└── SpringPostgresCrudApplication.java – Main class
```