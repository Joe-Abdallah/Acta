package com.csis231.javafxclient.service;

/**
 * Simple shared in-memory session for the JavaFX app.
 * Keeps the JWT so all controllers/ApiClient instances can reuse it.
 */
public final class ApiSession {
    private static volatile String token;

    private ApiSession() {
    }

    public static String getToken() {
        return token;
    }

    public static void setToken(String tokenValue) {
        token = tokenValue;
    }

    public static void clear() {
        token = null;
    }
}
