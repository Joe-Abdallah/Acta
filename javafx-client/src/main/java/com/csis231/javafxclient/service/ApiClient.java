package com.csis231.javafxclient.service;

import com.csis231.javafxclient.model.*;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.List;

/**
 * API client for Acta backend.
 * Base URL: http://localhost:8080/api
 */
public class ApiClient {
    private static final String BASE_URL = "http://localhost:8080/api";
    private final HttpClient httpClient;
    private final Gson gson;

    public ApiClient() {
        this.httpClient = HttpClient.newHttpClient();
        this.gson = new GsonBuilder()
                .registerTypeAdapter(Instant.class, (JsonDeserializer<Instant>) (json, type, ctx) ->
                        Instant.parse(json.getAsJsonPrimitive().getAsString()))
                .registerTypeAdapter(Instant.class, (JsonSerializer<Instant>) (src, type, ctx) ->
                        ctx.serialize(src.toString()))
                .create();
    }

    public void setAuthToken(String authToken) {
        ApiSession.setToken(authToken);
    }

    public String getAuthToken() {
        return ApiSession.getToken();
    }

    // ----------------------------------------
    // Client endpoints
    // ----------------------------------------

    public String summarizePost(Long postId) throws IOException, InterruptedException {
        HttpRequest request = newRequestBuilder(BASE_URL + "/posts/" + postId + "/summarize")
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return response.body();
        }
        throw buildApiException("Summarization failed", response);
    }

    public AuthResponseDto registerClient(ClientDto clientDto) throws IOException, InterruptedException {
        String json = gson.toJson(clientDto);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/clients"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 201) {
            AuthResponseDto auth = gson.fromJson(response.body(), AuthResponseDto.class);
            if (auth != null && auth.getToken() != null && !auth.getToken().isBlank()) {
                ApiSession.setToken(auth.getToken());
            }
            return auth;
        }
        throw buildApiException("Failed to register client", response);
    }

    public AuthResponseDto authenticate(LoginDto loginDto) throws IOException, InterruptedException {
        String json = gson.toJson(loginDto);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/clients/authenticate"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            AuthResponseDto auth = gson.fromJson(response.body(), AuthResponseDto.class);
            if (auth != null && auth.getToken() != null && !auth.getToken().isBlank()) {
                ApiSession.setToken(auth.getToken());
            }
            return auth;
        }
        throw buildApiException("Failed to login", response);
    }

    private HttpRequest.Builder newRequestBuilder(String url) {
        HttpRequest.Builder builder = HttpRequest.newBuilder().uri(URI.create(url));
        String token = ApiSession.getToken();

        if (token == null || token.isBlank()) {
            throw new RuntimeException("Missing auth token. Please login first.");
        }

        builder.header("Authorization", "Bearer " + token);
        return builder;
    }

    public ClientDto getClientById(Long id) throws IOException, InterruptedException {
        HttpRequest request = newRequestBuilder(BASE_URL + "/clients/" + id)
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return gson.fromJson(response.body(), ClientDto.class);
        }
        throw buildApiException("Failed to get client", response);
    }

    public List<ClientDto> getAllClients() throws IOException, InterruptedException {
        HttpRequest request = newRequestBuilder(BASE_URL + "/clients")
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return gson.fromJson(response.body(), new TypeToken<List<ClientDto>>() {}.getType());
        }
        throw buildApiException("Failed to get clients", response);
    }

    public ClientDto updateClient(Long id, ClientDto clientDto) throws IOException, InterruptedException {
        String json = gson.toJson(clientDto);
        HttpRequest request = newRequestBuilder(BASE_URL + "/clients/" + id)
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return gson.fromJson(response.body(), ClientDto.class);
        }
        throw buildApiException("Failed to update client", response);
    }

    public void deleteClient(Long id) throws IOException, InterruptedException {
        HttpRequest request = newRequestBuilder(BASE_URL + "/clients/" + id)
                .DELETE()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 204 && response.statusCode() != 200) {
            throw buildApiException("Failed to delete client", response);
        }
    }

    public ClientDto findByUsername(String username) throws IOException, InterruptedException {
        HttpRequest request = newRequestBuilder(BASE_URL + "/clients/username/" + username)
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return gson.fromJson(response.body(), ClientDto.class);
        }
        throw buildApiException("Failed to find client by username", response);
    }

    public ClientDto findByEmail(String email) throws IOException, InterruptedException {
        HttpRequest request = newRequestBuilder(BASE_URL + "/clients/email/" + email)
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return gson.fromJson(response.body(), ClientDto.class);
        }
        throw buildApiException("Failed to find client by email", response);
    }

    // ----------------------------------------
    // Community endpoints
    // ----------------------------------------
    public CommunityDto createCommunity(CommunityDto communityDto) throws IOException, InterruptedException {
        String json = gson.toJson(communityDto);
        HttpRequest request = newRequestBuilder(BASE_URL + "/communities")
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 201) {
            return gson.fromJson(response.body(), CommunityDto.class);
        }
        throw buildApiException("Failed to create community", response);
    }

    public CommunityDto getCommunityById(Long id) throws IOException, InterruptedException {
        HttpRequest request = newRequestBuilder(BASE_URL + "/communities/" + id)
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return gson.fromJson(response.body(), CommunityDto.class);
        }
        throw buildApiException("Failed to get community", response);
    }

    public List<CommunityDto> getAllCommunities() throws IOException, InterruptedException {
        HttpRequest request = newRequestBuilder(BASE_URL + "/communities")
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return gson.fromJson(response.body(), new TypeToken<List<CommunityDto>>() {}.getType());
        }
        throw buildApiException("Failed to get communities", response);
    }

    public CommunityDto updateCommunityDescription(Long id, String description) throws IOException, InterruptedException {
        HttpRequest request = newRequestBuilder(BASE_URL + "/communities/" + id)
                .header("Content-Type", "text/plain")
                .method("PATCH", HttpRequest.BodyPublishers.ofString(description))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return gson.fromJson(response.body(), CommunityDto.class);
        }
        throw buildApiException("Failed to update community description", response);
    }

    public void deleteCommunity(Long id) throws IOException, InterruptedException {
        HttpRequest request = newRequestBuilder(BASE_URL + "/communities/" + id)
                .DELETE()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 204 && response.statusCode() != 200) {
            throw buildApiException("Failed to delete community", response);
        }
    }

    // ----------------------------------------
    // Post endpoints
    // ----------------------------------------
    public PostDto createPost(PostDto postDto) throws IOException, InterruptedException {
        String json = gson.toJson(postDto);
        HttpRequest request = newRequestBuilder(BASE_URL + "/posts")
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 201) {
            return gson.fromJson(response.body(), PostDto.class);
        }
        throw buildApiException("Failed to create post", response);
    }

    public PostDto getPostById(Long id) throws IOException, InterruptedException {
        HttpRequest request = newRequestBuilder(BASE_URL + "/posts/" + id)
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return gson.fromJson(response.body(), PostDto.class);
        }
        throw buildApiException("Failed to get post", response);
    }

    public List<PostDto> getAllPosts() throws IOException, InterruptedException {
        HttpRequest request = newRequestBuilder(BASE_URL + "/posts")
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return gson.fromJson(response.body(), new TypeToken<List<PostDto>>() {}.getType());
        }
        throw buildApiException("Failed to get posts", response);
    }

    public PostDto updatePost(Long id, PostDto postDto) throws IOException, InterruptedException {
        String json = gson.toJson(postDto);
        HttpRequest request = newRequestBuilder(BASE_URL + "/posts/" + id)
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return gson.fromJson(response.body(), PostDto.class);
        }
        throw buildApiException("Failed to update post", response);
    }

    public void deletePost(Long id) throws IOException, InterruptedException {
        HttpRequest request = newRequestBuilder(BASE_URL + "/posts/" + id)
                .DELETE()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 204 && response.statusCode() != 200) {
            throw buildApiException("Failed to delete post", response);
        }
    }

    // ----------------------------------------
    // Comment endpoints
    // ----------------------------------------
    public CommentDto createComment(CommentDto commentDto) throws IOException, InterruptedException {
        String json = gson.toJson(commentDto);
        HttpRequest request = newRequestBuilder(BASE_URL + "/comments")
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 201) {
            return gson.fromJson(response.body(), CommentDto.class);
        }
        throw buildApiException("Failed to create comment", response);
    }

    public CommentDto getCommentById(Long id) throws IOException, InterruptedException {
        HttpRequest request = newRequestBuilder(BASE_URL + "/comments/" + id)
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return gson.fromJson(response.body(), CommentDto.class);
        }
        throw buildApiException("Failed to get comment", response);
    }

    public List<CommentDto> getCommentsByPost(Long postId) throws IOException, InterruptedException {
        HttpRequest request = newRequestBuilder(BASE_URL + "/comments/post/" + postId)
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return gson.fromJson(response.body(), new TypeToken<List<CommentDto>>() {}.getType());
        }
        throw buildApiException("Failed to get comments", response);
    }

    public CommentDto updateComment(Long id, String newContent) throws IOException, InterruptedException {
        HttpRequest request = newRequestBuilder(BASE_URL + "/comments/" + id)
                .header("Content-Type", "text/plain")
                .PUT(HttpRequest.BodyPublishers.ofString(newContent))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return gson.fromJson(response.body(), CommentDto.class);
        }
        throw buildApiException("Failed to update comment", response);
    }

    public void deleteComment(Long id) throws IOException, InterruptedException {
        HttpRequest request = newRequestBuilder(BASE_URL + "/comments/" + id)
                .DELETE()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 204 && response.statusCode() != 200) {
            throw buildApiException("Failed to delete comment", response);
        }
    }

    // ----------------------------------------
    // Post Vote endpoints
    // ----------------------------------------
    public PostVoteDto castPostVote(Long postId, Long clientId, VoteType voteType) throws IOException, InterruptedException {
        String path = String.format("/post-votes?postId=%d&clientId=%d&voteType=%s", postId, clientId, voteType.name());
        HttpRequest request = newRequestBuilder(BASE_URL + path)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 201 || response.statusCode() == 200) {
            return gson.fromJson(response.body(), PostVoteDto.class);
        }
        throw buildApiException("Failed to cast post vote", response);
    }

    public PostVoteDto getPostVote(Long postId, Long clientId) throws IOException, InterruptedException {
        String path = String.format("/post-votes?postId=%d&clientId=%d", postId, clientId);
        HttpRequest request = newRequestBuilder(BASE_URL + path)
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return gson.fromJson(response.body(), PostVoteDto.class);
        } else if (response.statusCode() == 404) {
            return null;
        }
        throw buildApiException("Failed to get post vote", response);
    }

    public void deletePostVote(Long postId, Long clientId) throws IOException, InterruptedException {
        String path = String.format("/post-votes?postId=%d&clientId=%d", postId, clientId);
        HttpRequest request = newRequestBuilder(BASE_URL + path)
                .DELETE()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 204 && response.statusCode() != 200) {
            throw buildApiException("Failed to delete post vote", response);
        }
    }

    // ----------------------------------------
    // Comment Vote endpoints
    // ----------------------------------------
    public CommentVoteDto castCommentVote(Long commentId, Long clientId, VoteType voteType) throws IOException, InterruptedException {
        String path = String.format("/comment-votes?commentId=%d&clientId=%d&voteType=%s", commentId, clientId, voteType.name());
        HttpRequest request = newRequestBuilder(BASE_URL + path)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 201 || response.statusCode() == 200) {
            return gson.fromJson(response.body(), CommentVoteDto.class);
        }
        throw buildApiException("Failed to cast comment vote", response);
    }

    public CommentVoteDto getCommentVote(Long commentId, Long clientId) throws IOException, InterruptedException {
        String path = String.format("/comment-votes?commentId=%d&clientId=%d", commentId, clientId);
        HttpRequest request = newRequestBuilder(BASE_URL + path)
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return gson.fromJson(response.body(), CommentVoteDto.class);
        } else if (response.statusCode() == 404) {
            return null;
        }
        throw buildApiException("Failed to get comment vote", response);
    }

    public void deleteCommentVote(Long commentId, Long clientId) throws IOException, InterruptedException {
        String path = String.format("/comment-votes?commentId=%d&clientId=%d", commentId, clientId);
        HttpRequest request = newRequestBuilder(BASE_URL + path)
                .DELETE()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 204 && response.statusCode() != 200) {
            throw buildApiException("Failed to delete comment vote", response);
        }
    }

    // ----------------------------------------
    // Helper methods
    // ----------------------------------------
    private RuntimeException buildApiException(String defaultMessage, HttpResponse<String> response) {
        String message = extractErrorMessage(response.body());
        if (message != null && !message.isBlank()) {
            return new RuntimeException(message);
        }
        return new RuntimeException(defaultMessage + ": " + response.statusCode() + " - " + response.body());
    }

    private String extractErrorMessage(String responseBody) {
        if (responseBody == null || responseBody.isBlank()) {
            return null;
        }
        try {
            JsonObject jsonObject = JsonParser.parseString(responseBody).getAsJsonObject();
            if (jsonObject.has("message") && !jsonObject.get("message").isJsonNull()) {
                return jsonObject.get("message").getAsString();
            }
        } catch (Exception ignored) {
            return responseBody;
        }
        return responseBody;
    }
}