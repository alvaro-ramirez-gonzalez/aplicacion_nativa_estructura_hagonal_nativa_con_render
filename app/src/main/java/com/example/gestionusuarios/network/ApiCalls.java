package com.example.gestionusuarios.network;

import com.google.gson.JsonParseException;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public final class ApiCalls {
    private ApiCalls() { }

    public static <T> ApiResult<T> execute(Call<T> call, boolean bodyRequired) {
        try {
            return fromResponse(call.execute(), bodyRequired);
        } catch (IOException e) {
            return ApiResult.fail(ErrorMapper.fromIOException(e));
        } catch (JsonParseException e) {
            return ApiResult.fail(ErrorMapper.invalidResponse());
        }
    }

    public static <T> ApiResult<T> fromResponse(Response<T> response, boolean bodyRequired) {
        if (response.isSuccessful()) {
            T body = response.body();
            if (bodyRequired && body == null) {
                return ApiResult.fail(new ApiError(response.code(),
                        "El servidor respondió sin contenido (HTTP " + response.code() + ")."));
            }
            return ApiResult.ok(body);
        }
        return ApiResult.fail(ErrorMapper.fromHttp(response.code(), readErrorBody(response.errorBody())));
    }

    private static String readErrorBody(ResponseBody body) {
        if (body == null) return null;
        try {
            return body.string();
        } catch (IOException e) {
            return null;
        }
    }
}
