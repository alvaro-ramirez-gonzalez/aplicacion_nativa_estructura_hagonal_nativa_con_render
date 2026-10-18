package com.example.gestionusuarios.model;

public final class Status {
    public static final String ACTIVE = "ACTIVE";
    public static final String INACTIVE = "INACTIVE";
    public static final String PENDING = "PENDING";
    public static final String BLOCKED = "BLOCKED";
    public static final String[] ALL = {ACTIVE, INACTIVE, PENDING, BLOCKED};

    private Status() { }
}
