package com.example.gestionusuarios.session;

import java.security.GeneralSecurityException;

public interface TokenCipher {
    String encrypt(String plain) throws GeneralSecurityException;
    String decrypt(String encrypted) throws GeneralSecurityException;
}
