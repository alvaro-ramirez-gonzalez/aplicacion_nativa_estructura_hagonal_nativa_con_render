package com.example.gestionusuarios.model;

public class UpdateUserRequest {
    public final String name;
    public final String email;
    public final String password;
    public final String role;
    public final String status;

    public UpdateUserRequest(String name, String email, String password, String role, String status) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.status = status;
    }
}
