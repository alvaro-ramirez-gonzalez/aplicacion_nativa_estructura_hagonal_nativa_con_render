package com.example.gestionusuarios.session;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public final class JwtClaims {
    private JwtClaims() { }

    public static String roleFromToken(String jwt) {
        try {
            String[] parts = jwt.split("\\.");
            if (parts.length < 2) return null;
            String json = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
            JsonObject payload = JsonParser.parseString(json).getAsJsonObject();
            JsonElement role = payload.get("role");
            if (role != null && role.isJsonArray() && role.getAsJsonArray().size() > 0) {
                role = role.getAsJsonArray().get(0);
            }
            if (role == null || !role.isJsonPrimitive()) return null;
            String value = role.getAsString().trim().toUpperCase();
            if (value.startsWith("ROLE_")) value = value.substring(5);
            return value.isEmpty() ? null : value;
        } catch (RuntimeException e) {
            return null;
        }
    }
}
