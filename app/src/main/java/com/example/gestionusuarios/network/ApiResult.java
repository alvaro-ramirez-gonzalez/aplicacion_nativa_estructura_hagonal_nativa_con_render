package com.example.gestionusuarios.network;

public final class ApiResult<T> {
    public final T data;
    public final ApiError error;

    private ApiResult(T data, ApiError error) {
        this.data = data;
        this.error = error;
    }

    public static <T> ApiResult<T> ok(T data) {
        return new ApiResult<>(data, null);
    }

    public static <T> ApiResult<T> fail(ApiError error) {
        return new ApiResult<>(null, error);
    }

    public boolean isSuccess() {
        return error == null;
    }
}
