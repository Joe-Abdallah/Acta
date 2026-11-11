package com.csis231.javafxclient.model;

public class AuthResponseDto {
    private String token;
    private ClientDto client;

    public AuthResponseDto() {
    }

    public AuthResponseDto(String token, ClientDto client) {
        this.token = token;
        this.client = client;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public ClientDto getClient() {
        return client;
    }

    public void setClient(ClientDto client) {
        this.client = client;
    }
}
