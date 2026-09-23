package com.supplychain.ai.common;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Uniform error body returned by every failed API call, including the
 * ones Spring Security itself rejects (see config.RestAuthEntryPoint /
 * config.RestAccessDeniedHandler).
 */
public class ApiError {

    private final int status;
    private final String message;
    private final LocalDateTime timestamp;
    private final Map<String, String> fieldErrors;

    private ApiError(int status, String message, LocalDateTime timestamp, Map<String, String> fieldErrors) {
        this.status = status;
        this.message = message;
        this.timestamp = timestamp;
        this.fieldErrors = fieldErrors;
    }

    public static ApiError of(int status, String message) {
        return new ApiError(status, message, LocalDateTime.now(), null);
    }

    public static ApiError of(int status, String message, Map<String, String> fieldErrors) {
        return new ApiError(status, message, LocalDateTime.now(), fieldErrors);
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public Map<String, String> getFieldErrors() {
        return fieldErrors;
    }
}
