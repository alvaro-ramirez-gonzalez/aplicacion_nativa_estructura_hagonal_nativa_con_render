package com.example.gestionusuarios.data;

import com.example.gestionusuarios.model.LoginRequest;
import com.example.gestionusuarios.model.LoginResponse;
import com.example.gestionusuarios.network.ApiCalls;
import com.example.gestionusuarios.network.ApiError;
import com.example.gestionusuarios.network.ApiResult;
import com.example.gestionusuarios.network.ApiService;
import com.example.gestionusuarios.session.JwtClaims;
import com.example.gestionusuarios.session.SessionManager;

import java.security.GeneralSecurityException;
import java.util.concurrent.Executor;

public class AuthRepository {
    private final ApiService api;
    private final SessionManager session;
    private final Executor executor;

    public AuthRepository(ApiService api, SessionManager session, Executor executor) {
        this.api = api;
        this.session = session;
        this.executor = executor;
    }

    public void login(String email, String password, Callback<Void> cb) {
        executor.execute(() -> {
            ApiResult<LoginResponse> r = ApiCalls.execute(api.login(new LoginRequest(email, password)), true);
            if (!r.isSuccess()) {
                if (r.error.httpCode == 401) {
                    cb.onResult(ApiResult.fail(new ApiError(401, "Correo o contraseña incorrectos (HTTP 401).")));
                } else {
                    cb.onResult(ApiResult.fail(r.error));
                }
                return;
            }
            LoginResponse body = r.data;
            if (body.accessToken == null || body.accessToken.isEmpty() || body.expiresIn <= 0) {
                cb.onResult(ApiResult.fail(new ApiError(ApiError.INVALID_RESPONSE,
                        "La respuesta de inicio de sesión está incompleta.")));
                return;
            }
            try {
                session.saveSession(body.accessToken, body.expiresIn, JwtClaims.roleFromToken(body.accessToken));
                cb.onResult(ApiResult.ok(null));
            } catch (GeneralSecurityException | RuntimeException e) {
                cb.onResult(ApiResult.fail(new ApiError(ApiError.NETWORK,
                        "No se pudo guardar la sesión de forma segura en este dispositivo.")));
            }
        });
    }
}
