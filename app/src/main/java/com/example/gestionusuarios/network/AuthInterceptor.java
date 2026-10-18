package com.example.gestionusuarios.network;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public final class AuthInterceptor implements Interceptor {

    public interface TokenProvider {
        String getToken();
    }

    private final TokenProvider provider;

    public AuthInterceptor(TokenProvider provider) {
        this.provider = provider;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        if (!request.url().encodedPath().endsWith("/api/auth/login")) {
            String token = provider.getToken();
            if (token != null) {
                request = request.newBuilder().header("Authorization", "Bearer " + token).build();
            }
        }
        return chain.proceed(request);
    }
}
