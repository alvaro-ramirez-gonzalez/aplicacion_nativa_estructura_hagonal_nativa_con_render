package com.example.gestionusuarios;

import static org.junit.Assert.*;

import com.example.gestionusuarios.network.ApiCalls;
import com.example.gestionusuarios.network.ApiError;
import com.example.gestionusuarios.network.ApiResult;
import com.example.gestionusuarios.network.ErrorMapper;

import org.junit.Test;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.List;

import retrofit2.Response;

public class ErrorMapperTest {

    @Test
    public void includesServerMessageAndStatusCode() {
        ApiError e = ErrorMapper.fromHttp(409, "{\"status\":409,\"message\":\"email duplicado\"}");
        assertEquals(409, e.httpCode);
        assertTrue(e.message.contains("email duplicado"));
        assertTrue(e.message.contains("HTTP 409"));
    }

    @Test
    public void handlesEmptyBody() {
        ApiError e = ErrorMapper.fromHttp(404, "");
        assertTrue(e.message.contains("HTTP 404"));
        assertFalse(e.message.contains("Detalle"));
        assertNotNull(ErrorMapper.fromHttp(500, null).message);
    }

    @Test
    public void handlesInvalidJsonBody() {
        ApiError e = ErrorMapper.fromHttp(500, "<html>boom</html>");
        assertTrue(e.message.contains("HTTP 500"));
        assertFalse(e.message.contains("boom"));
    }

    @Test
    public void marksUnauthorizedAndForbidden() {
        assertTrue(ErrorMapper.fromHttp(401, null).isUnauthorized());
        ApiError forbidden = ErrorMapper.fromHttp(403, null);
        assertFalse(forbidden.isUnauthorized());
        assertTrue(forbidden.message.contains("permiso"));
    }

    @Test
    public void mapsConnectionProblems() {
        ApiError timeout = ErrorMapper.fromIOException(new SocketTimeoutException());
        assertTrue(timeout.isNetwork());
        assertTrue(timeout.message.contains("tardó"));
        assertTrue(ErrorMapper.fromIOException(new UnknownHostException()).message.contains("conectar"));
        assertTrue(ErrorMapper.fromIOException(new IOException("x")).isNetwork());
    }

    @Test
    public void emptySuccessBodyIsErrorWhenBodyRequired() {
        ApiResult<List<String>> r = ApiCalls.fromResponse(Response.<List<String>>success(null), true);
        assertFalse(r.isSuccess());
    }

    @Test
    public void emptySuccessBodyIsOkWhenNotRequired() {
        assertTrue(ApiCalls.fromResponse(Response.<Void>success(null), false).isSuccess());
    }
}
