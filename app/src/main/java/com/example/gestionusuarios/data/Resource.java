package com.example.gestionusuarios.data;

import com.example.gestionusuarios.network.ApiError;
import com.example.gestionusuarios.network.ApiResult;

public final class Resource<T> {
    public enum Status { LOADING, SUCCESS, ERROR }

    public final Status status;
    public final T data;
    public final ApiError error;

    private Resource(Status status, T data, ApiError error) {
        this.status = status;
        this.data = data;
        this.error = error;
    }

    public static <T> Resource<T> loading() { return new Resource<>(Status.LOADING, null, null); }
    public static <T> Resource<T> success(T data) { return new Resource<>(Status.SUCCESS, data, null); }
    public static <T> Resource<T> error(ApiError e) { return new Resource<>(Status.ERROR, null, e); }

    public static <T> Resource<T> from(ApiResult<T> r) {
        return r.isSuccess() ? success(r.data) : error(r.error);
    }
}
