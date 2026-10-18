package com.example.gestionusuarios.util;

public final class AppConfig {
    private AppConfig() { }

    /**
     * Semántica de contraseña en PUT /api/users/{id} NO confirmada (¿se ignora si falta? ¿se exige? ¿se borra?).
     * Mientras sea false, la edición no muestra ni envía contraseña. Cambiar a true SOLO tras confirmar
     * con el backend; el campo enviará la contraseña únicamente si la persona escribe una.
     */
    public static final boolean PASSWORD_UPDATE_ENABLED = true;

    public static boolean isBaseUrlPlaceholder(String url) {
        return url == null || url.contains("REEMPLAZAR");
    }
}
