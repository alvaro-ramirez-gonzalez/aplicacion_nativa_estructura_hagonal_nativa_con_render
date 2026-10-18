package com.example.gestionusuarios.network;

public final class ApiError {
    public static final int NETWORK = 0;
    public static final int INVALID_RESPONSE = -1;

    public final int httpCode;
    public final String message;

    public ApiError(int httpCode, String message) {
        this.httpCode = httpCode;
        this.message = message;
    }

    public boolean isUnauthorized() {
        return httpCode == 401;
    }

    public boolean isNetwork() {
        return httpCode == NETWORK;
    }
}
