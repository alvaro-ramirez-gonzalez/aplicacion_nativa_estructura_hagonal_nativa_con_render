package com.example.gestionusuarios;

import static org.junit.Assert.*;

import com.example.gestionusuarios.model.Role;
import com.example.gestionusuarios.util.AppConfig;
import com.example.gestionusuarios.util.Validators;

import org.junit.Test;

public class ValidatorsTest {
    @Test
    public void email() {
        assertTrue(Validators.isValidEmail("a@example.com"));
        assertTrue(Validators.isValidEmail(" a@example.com "));
        assertFalse(Validators.isValidEmail("a@example"));
        assertFalse(Validators.isValidEmail("sin-arroba.com"));
        assertFalse(Validators.isValidEmail(""));
        assertFalse(Validators.isValidEmail(null));
    }

    @Test
    public void requiredAndChoices() {
        assertFalse(Validators.isNotBlank("   "));
        assertTrue(Validators.isNotBlank("x"));
        assertTrue(Validators.isOneOf("ADMIN", Role.ALL));
        assertFalse(Validators.isOneOf("ROOT", Role.ALL));
    }

    @Test
    public void passwordUpdateStaysDisabledUntilConfirmed() {
        assertFalse(AppConfig.PASSWORD_UPDATE_ENABLED);
        assertTrue(AppConfig.isBaseUrlPlaceholder("https://REEMPLAZAR-URL-DE-RENDER.onrender.com/"));
        assertFalse(AppConfig.isBaseUrlPlaceholder("https://mi-servicio.onrender.com/"));
    }
}
