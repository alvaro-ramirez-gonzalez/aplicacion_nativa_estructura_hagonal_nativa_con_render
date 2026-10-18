package com.example.gestionusuarios.data;

import com.example.gestionusuarios.model.CreateUserRequest;
import com.example.gestionusuarios.model.UpdateUserRequest;
import com.example.gestionusuarios.model.User;
import com.example.gestionusuarios.network.ApiCalls;
import com.example.gestionusuarios.network.ApiResult;
import com.example.gestionusuarios.network.ApiService;
import com.example.gestionusuarios.session.SessionManager;

import java.util.List;
import java.util.concurrent.Executor;

import retrofit2.Call;

public class UserRepository {
    private final ApiService api;
    private final SessionManager session;
    private final Executor executor;

    public UserRepository(ApiService api, SessionManager session, Executor executor) {
        this.api = api;
        this.session = session;
        this.executor = executor;
    }

    public void list(Callback<List<User>> cb) { run(api.listUsers(), true, cb); }
    public void get(String id, Callback<User> cb) { run(api.getUser(id), true, cb); }
    public void create(CreateUserRequest r, Callback<Void> cb) { run(api.createUser(r), false, cb); }
    public void update(String id, UpdateUserRequest r, Callback<Void> cb) { run(api.updateUser(id, r), false, cb); }
    public void delete(String id, Callback<Void> cb) { run(api.deleteUser(id), false, cb); }

    private <T> void run(Call<T> call, boolean bodyRequired, Callback<T> cb) {
        executor.execute(() -> {
            ApiResult<T> r = ApiCalls.execute(call, bodyRequired);
            if (!r.isSuccess() && r.error.isUnauthorized()) session.clear();
            cb.onResult(r);
        });
    }
}
