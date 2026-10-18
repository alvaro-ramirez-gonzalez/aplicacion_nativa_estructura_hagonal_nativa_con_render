package com.example.gestionusuarios.util;

import java.util.regex.Pattern;

public final class Validators {
    private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    private Validators() { }

    public static boolean isNotBlank(String s) {
        return s != null && !s.trim().isEmpty();
    }

    public static boolean isValidEmail(String s) {
        return s != null && EMAIL.matcher(s.trim()).matches();
    }

    public static boolean isOneOf(String value, String[] allowed) {
        if (value == null) return false;
        for (String a : allowed) if (a.equals(value)) return true;
        return false;
    }
}
