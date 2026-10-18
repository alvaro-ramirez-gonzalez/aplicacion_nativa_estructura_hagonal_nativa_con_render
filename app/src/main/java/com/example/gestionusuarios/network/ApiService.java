package com.example.gestionusuarios.network;

import com.example.gestionusuarios.model.CreateUserRequest;
import com.example.gestionusuarios.model.LoginRequest;
import com.example.gestionusuarios.model.LoginResponse;
import com.example.gestionusuarios.model.UpdateUserRequest;
import com.example.gestionusuarios.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {
    @POST("api/auth/login")
    Call<LoginResponse> login(@Body LoginRequest body);

    @GET("api/users")
    Call<List<User>> listUsers();

    @GET("api/users/{id}")
    Call<User> getUser(@Path("id") String id);

    @POST("api/users")
    Call<Void> createUser(@Body CreateUserRequest body);

    @PUT("api/users/{id}")
    Call<Void> updateUser(@Path("id") String id, @Body UpdateUserRequest body);

    @DELETE("api/users/{id}")
    Call<Void> deleteUser(@Path("id") String id);
}
