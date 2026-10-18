package com.example.gestionusuarios;

import static org.junit.Assert.*;

import com.example.gestionusuarios.model.CreateUserRequest;
import com.example.gestionusuarios.model.LoginResponse;
import com.example.gestionusuarios.model.UpdateUserRequest;
import com.example.gestionusuarios.model.User;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import org.junit.Test;

import java.util.List;

public class ModelJsonTest {
    private final Gson gson = new Gson();

    @Test
    public void parsesUser() {
        User u = gson.fromJson("{\"id\":\"1\",\"name\":\"Ana\",\"email\":\"a@example.com\",\"role\":\"ADMIN\",\"status\":\"ACTIVE\"}", User.class);
        assertEquals("1", u.id);
        assertEquals("Ana", u.name);
        assertEquals("ADMIN", u.role);
        assertEquals("ACTIVE", u.status);
    }

    @Test
    public void parsesUserList() {
        List<User> list = gson.fromJson("[{\"id\":\"1\"},{\"id\":\"2\"}]", new TypeToken<List<User>>() { }.getType());
        assertEquals(2, list.size());
    }

    @Test
    public void parsesLoginResponseUsingReceivedExpiresIn() {
        LoginResponse r = gson.fromJson("{\"accessToken\":\"t\",\"tokenType\":\"Bearer\",\"expiresIn\":1234}", LoginResponse.class);
        assertEquals("t", r.accessToken);
        assertEquals(1234L, r.expiresIn);
    }

    @Test
    public void createRequestHasContractFieldsAndNoStatus() {
        JsonObject o = JsonParser.parseString(gson.toJson(new CreateUserRequest("id-1", "Ana", "a@example.com", "pw", "MEMBER"))).getAsJsonObject();
        assertEquals(5, o.size());
        assertEquals("id-1", o.get("id").getAsString());
        assertEquals("MEMBER", o.get("role").getAsString());
        assertFalse(o.has("status"));
    }

    @Test
    public void updateRequestOmitsNullPassword() {
        JsonObject o = JsonParser.parseString(gson.toJson(new UpdateUserRequest("Ana", "a@example.com", null, "ADMIN", "ACTIVE"))).getAsJsonObject();
        assertFalse(o.has("password"));
        assertEquals("ACTIVE", o.get("status").getAsString());
    }
}
