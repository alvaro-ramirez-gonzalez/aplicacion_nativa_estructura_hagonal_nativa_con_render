package com.example.gestionusuarios.session;

import java.security.GeneralSecurityException;

public final class SessionManager {
    static final String K_TOKEN = "token_enc";
    static final String K_EXPIRES_AT = "expires_at";
    static final String K_ROLE_HINT = "role_hint";

    private final KeyValueStore store;
    private final TokenCipher cipher;
    private final TimeSource time;

    public SessionManager(KeyValueStore store, TokenCipher cipher, TimeSource time) {
        this.store = store;
        this.cipher = cipher;
        this.time = time;
    }

    /** @param roleHint rol leído del JWT solo como ayuda de presentación (puede ser null). */
    public void saveSession(String token, long expiresInSeconds, String roleHint) throws GeneralSecurityException {
        if (token == null || token.isEmpty() || expiresInSeconds <= 0) {
            clear();
            throw new IllegalArgumentException("Datos de sesión incompletos");
        }
        String encrypted;
        try {
            encrypted = cipher.encrypt(token);
        } catch (GeneralSecurityException | RuntimeException e) {
            clear();
            throw e instanceof GeneralSecurityException ? (GeneralSecurityException) e
                    : new GeneralSecurityException("No se pudo cifrar el token", e);
        }
        store.putLong(K_EXPIRES_AT, time.nowMillis() + expiresInSeconds * 1000L);
        if (roleHint != null) store.putString(K_ROLE_HINT, roleHint); else store.remove(K_ROLE_HINT);
        store.putString(K_TOKEN, encrypted);
    }

    public String getToken() {
        String encrypted = store.getString(K_TOKEN);
        if (encrypted == null) return null;
        long expiresAt = store.getLong(K_EXPIRES_AT, 0L);
        if (expiresAt <= 0L || time.nowMillis() >= expiresAt) {
            clear();
            return null;
        }
        try {
            return cipher.decrypt(encrypted);
        } catch (GeneralSecurityException | RuntimeException e) {
            clear();
            return null;
        }
    }

    public boolean isLoggedIn() {
        return getToken() != null;
    }

    public String getRoleHint() {
        return getToken() == null ? null : store.getString(K_ROLE_HINT);
    }

    public void clear() {
        store.clear();
    }
}
