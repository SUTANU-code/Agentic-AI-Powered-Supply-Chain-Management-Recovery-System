package com.supplychain.ai.user;

public class AuthResponse {

    private final String token;
    private final String tokenType;
    private final long expiresInMs;
    private final UserResponse user;

    public AuthResponse(String token, String tokenType, long expiresInMs, UserResponse user) {
        this.token = token;
        this.tokenType = tokenType;
        this.expiresInMs = expiresInMs;
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public String getTokenType() {
        return tokenType;
    }

    public long getExpiresInMs() {
        return expiresInMs;
    }

    public UserResponse getUser() {
        return user;
    }
}
