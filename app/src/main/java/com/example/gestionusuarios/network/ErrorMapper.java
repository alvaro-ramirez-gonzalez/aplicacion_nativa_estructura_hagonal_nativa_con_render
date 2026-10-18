package com.example.gestionusuarios.network;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ConnectException;
import java.net.UnknownHostException;

import javax.net.ssl.SSLException;

public final class ErrorMapper {
    private ErrorMapper() { }

    public static ApiError fromHttp(int code, String body) {
        String base;
        switch (code) {
            case 400: base = "Los datos enviados no son válidos."; break;
            case 401: base = "Sesión no válida o vencida. Inicia sesión de nuevo."; break;
            case 403: base = "No tienes permiso para realizar esta acción."; break;
            case 404: base = "No se encontró el recurso solicitado."; break;
            case 409: base = "Conflicto: el recurso ya existe o está en uso."; break;
            case 502: case 503: case 504:
                base = "El servidor no está disponible o se está iniciando. Espera un momento y reintenta."; break;
            default:
                base = code >= 500 ? "Error interno del servidor." : "Error inesperado.";
        }
        String detail = serverMessage(body);
        String msg = base + (detail != null ? " Detalle: " + detail : "") + " (HTTP " + code + ")";
        return new ApiError(code, msg);
    }

    public static ApiError fromIOException(IOException e) {
        if (e instanceof InterruptedIOException) {
            return new ApiError(ApiError.NETWORK,
                    "El servidor tardó demasiado en responder (Render puede estar iniciándose). Reintenta en unos segundos.");
        }
        if (e instanceof UnknownHostException || e instanceof ConnectException) {
            return new ApiError(ApiError.NETWORK,
                    "No se pudo conectar con el servidor. Revisa tu conexión a internet y la URL configurada.");
        }
        if (e instanceof SSLException) {
            return new ApiError(ApiError.NETWORK, "Falló la conexión segura (HTTPS) con el servidor.");
        }
        return new ApiError(ApiError.NETWORK, "Error de conexión con el servidor. Reintenta.");
    }

    public static ApiError invalidResponse() {
        return new ApiError(ApiError.INVALID_RESPONSE, "El servidor envió una respuesta que la app no puede interpretar.");
    }

    static String serverMessage(String body) {
        if (body == null || body.trim().isEmpty()) return null;
        try {
            JsonElement e = JsonParser.parseString(body);
            if (e.isJsonObject()) {
                JsonElement m = e.getAsJsonObject().get("message");
                if (m != null && m.isJsonPrimitive()) return m.getAsString();
            }
        } catch (JsonParseException | IllegalStateException ignored) {
            // cuerpo no JSON: se usa solo el mensaje base
        }
        return null;
    }
}
