package com.example.gestionusuarios;

import static org.junit.Assert.*;

import com.example.gestionusuarios.model.CreateUserRequest;
import com.example.gestionusuarios.model.LoginRequest;
import com.example.gestionusuarios.model.LoginResponse;
import com.example.gestionusuarios.model.UpdateUserRequest;
import com.example.gestionusuarios.model.User;
import com.example.gestionusuarios.network.ApiCalls;
import com.example.gestionusuarios.network.ApiClient;
import com.example.gestionusuarios.network.ApiResult;
import com.example.gestionusuarios.network.ApiService;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import okhttp3.mockwebserver.SocketPolicy;

public class ApiClientTest {
    private static final String FAKE_TOKEN = "token-de-prueba-no-real";
    private MockWebServer server;
    private ApiService api;

    @Before
    public void setUp() throws Exception {
        server = new MockWebServer();
        server.start();
        api = ApiClient.create(server.url("/").toString(), () -> FAKE_TOKEN);
    }

    @After
    public void tearDown() throws Exception {
        server.shutdown();
    }

    @Test
    public void login_postsJson_withoutAuthorizationHeader() throws Exception {
        server.enqueue(new MockResponse().setBody("{\"accessToken\":\"abc\",\"tokenType\":\"Bearer\",\"expiresIn\":900}"));
        ApiResult<LoginResponse> r = ApiCalls.execute(api.login(new LoginRequest("u@example.com", "pw-de-prueba")), true);
        assertTrue(r.isSuccess());
        assertEquals(900L, r.data.expiresIn);
        RecordedRequest req = server.takeRequest();
        assertEquals("POST", req.getMethod());
        assertEquals("/api/auth/login", req.getPath());
        assertNull(req.getHeader("Authorization"));
        assertTrue(req.getBody().readUtf8().contains("\"email\":\"u@example.com\""));
    }

    @Test
    public void listUsers_sendsBearerToken() throws Exception {
        server.enqueue(new MockResponse().setBody("[{\"id\":\"1\",\"name\":\"Ana\",\"email\":\"a@example.com\",\"role\":\"ADMIN\",\"status\":\"ACTIVE\"}]"));
        ApiResult<List<User>> r = ApiCalls.execute(api.listUsers(), true);
        assertTrue(r.isSuccess());
        assertEquals(1, r.data.size());
        RecordedRequest req = server.takeRequest();
        assertEquals("GET", req.getMethod());
        assertEquals("/api/users", req.getPath());
        assertEquals("Bearer " + FAKE_TOKEN, req.getHeader("Authorization"));
    }

    @Test
    public void getUser_usesIdInPath() throws Exception {
        server.enqueue(new MockResponse().setBody("{\"id\":\"42\",\"name\":\"Luis\"}"));
        ApiResult<User> r = ApiCalls.execute(api.getUser("42"), true);
        assertEquals("Luis", r.data.name);
        assertEquals("/api/users/42", server.takeRequest().getPath());
    }

    @Test
    public void createUser_201_sendsExpectedBody() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(201));
        ApiResult<Void> r = ApiCalls.execute(api.createUser(new CreateUserRequest("id-1", "Ana", "a@example.com", "pw-de-prueba", "MEMBER")), false);
        assertTrue(r.isSuccess());
        RecordedRequest req = server.takeRequest();
        assertEquals("POST", req.getMethod());
        assertEquals("/api/users", req.getPath());
        String body = req.getBody().readUtf8();
        assertTrue(body.contains("\"id\":\"id-1\""));
        assertTrue(body.contains("\"role\":\"MEMBER\""));
    }

    @Test
    public void updateUser_putsToIdPath_withoutPasswordWhenNull() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(200));
        ApiResult<Void> r = ApiCalls.execute(api.updateUser("7", new UpdateUserRequest("Ana", "a@example.com", null, "REVIEWER", "ACTIVE")), false);
        assertTrue(r.isSuccess());
        RecordedRequest req = server.takeRequest();
        assertEquals("PUT", req.getMethod());
        assertEquals("/api/users/7", req.getPath());
        String body = req.getBody().readUtf8();
        assertFalse(body.contains("password"));
        assertTrue(body.contains("\"status\":\"ACTIVE\""));
    }

    @Test
    public void deleteUser_204_emptyBodyIsSuccess() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(204));
        assertTrue(ApiCalls.execute(api.deleteUser("7"), false).isSuccess());
        RecordedRequest req = server.takeRequest();
        assertEquals("DELETE", req.getMethod());
        assertEquals("/api/users/7", req.getPath());
    }

    @Test
    public void errorStatuses_areMapped() {
        int[] codes = {400, 401, 403, 404, 409, 500};
        for (int code : codes) {
            server.enqueue(new MockResponse().setResponseCode(code).setBody("{\"status\":" + code + ",\"message\":\"detalle\"}"));
            ApiResult<List<User>> r = ApiCalls.execute(api.listUsers(), true);
            assertFalse(r.isSuccess());
            assertEquals(code, r.error.httpCode);
            assertTrue(r.error.message.contains("HTTP " + code));
        }
    }

    @Test
    public void invalidJsonOnSuccess_isReportedAsInvalidResponse() {
        server.enqueue(new MockResponse().setBody("esto no es json"));
        ApiResult<List<User>> r = ApiCalls.execute(api.listUsers(), true);
        assertFalse(r.isSuccess());
        assertEquals(-1, r.error.httpCode);
    }

    @Test
    public void emptyBodyOnGet_isError() {
        server.enqueue(new MockResponse().setResponseCode(200));
        assertFalse(ApiCalls.execute(api.listUsers(), true).isSuccess());
    }

    @Test
    public void connectionDrop_isNetworkError_andNotRetried() {
        server.enqueue(new MockResponse().setSocketPolicy(SocketPolicy.DISCONNECT_AT_START));
        ApiResult<Void> r = ApiCalls.execute(api.createUser(new CreateUserRequest("i", "n", "e@example.com", "p", "MEMBER")), false);
        assertFalse(r.isSuccess());
        assertTrue(r.error.isNetwork());
        assertEquals(1, server.getRequestCount());
    }
}
