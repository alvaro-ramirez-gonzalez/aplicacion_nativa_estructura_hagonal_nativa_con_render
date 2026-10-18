package com.example.gestionusuarios.model;

public class CreateUserRequest {
    public final String id;
    public final String name;
    public final String email;
    public final String password;
    public final String role;

    public CreateUserRequest(String id, String name, String email, String password, String role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }
}
