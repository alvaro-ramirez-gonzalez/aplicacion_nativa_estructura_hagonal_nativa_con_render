package com.example.gestionusuarios.util;

import android.content.Context;

import com.example.gestionusuarios.R;

public final class Labels {
    private Labels() { }

    public static String role(Context c, String role) {
        if (role == null) return "—";
        switch (role) {
            case "ADMIN": return c.getString(R.string.role_admin);
            case "MEMBER": return c.getString(R.string.role_member);
            case "REVIEWER": return c.getString(R.string.role_reviewer);
            default: return role;
        }
    }

    public static String status(Context c, String status) {
        if (status == null) return "—";
        switch (status) {
            case "ACTIVE": return c.getString(R.string.status_active);
            case "INACTIVE": return c.getString(R.string.status_inactive);
            case "PENDING": return c.getString(R.string.status_pending);
            case "BLOCKED": return c.getString(R.string.status_blocked);
            default: return status;
        }
    }
}
