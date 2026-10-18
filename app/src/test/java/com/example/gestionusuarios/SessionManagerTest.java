package com.example.gestionusuarios;

import static org.junit.Assert.*;

import com.example.gestionusuarios.session.JwtClaims;
import com.example.gestionusuarios.session.KeyValueStore;
import com.example.gestionusuarios.session.SessionManager;
import com.example.gestionusuarios.session.TimeSource;
import com.example.gestionusuarios.session.TokenCipher;

import org.junit.Before;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class SessionManagerTest {
    private static final String TOKEN = "token-de-prueba-no-real";

    static class FakeStore implements KeyValueStore {
        final Map<String, Object> map = new HashMap<>();
        public String getString(String k) { return (String) map.get(k); }
        public long getLong(String k, long d) { Object v = map.get(k); return v == null ? d : (Long) v; }
        public void putString(String k, String v) { map.put(k, v); }
        public void putLong(String k, long v) { map.put(k, v); }
        public void remove(String k) { map.remove(k); }
        public void clear() { map.clear(); }
    }

    static class FakeCipher implements TokenCipher {
        boolean failDecrypt;
        public String encrypt(String p) { return "enc:" + new StringBuilder(p).reverse(); }
        public String decrypt(String e) throws GeneralSecurityException {
            if (failDecrypt) throw new GeneralSecurityException("clave invalidada");
            return new StringBuilder(e.substring(4)).reverse().toString();
        }
    }

    private FakeStore store;
    private FakeCipher cipher;
    private long now;
    private SessionManager session;

    @Before
    public void setUp() {
        store = new FakeStore();
        cipher = new FakeCipher();
        now = 1_000_000L;
        session = new SessionManager(store, cipher, () -> now);
    }

    @Test
    public void expiresUsingReceivedExpiresIn() throws Exception {
        session.saveSession(TOKEN, 900, "ADMIN");
        now += 899_000L;
        assertEquals(TOKEN, session.getToken());
        now += 1_000L;
        assertNull(session.getToken());
        assertTrue(store.map.isEmpty());
    }

    @Test
    public void respectsDifferentExpiresIn() throws Exception {
        session.saveSession(TOKEN, 60, null);
        now += 61_000L;
        assertFalse(session.isLoggedIn());
    }

    @Test
    public void neverStoresPlainToken() throws Exception {
        session.saveSession(TOKEN, 900, "ADMIN");
        for (Object v : store.map.values()) assertFalse(String.valueOf(v).contains(TOKEN));
    }

    @Test
    public void undecryptableToken_clearsSession() throws Exception {
        session.saveSession(TOKEN, 900, "ADMIN");
        cipher.failDecrypt = true;
        assertNull(session.getToken());
        assertTrue(store.map.isEmpty());
    }

    @Test
    public void clearRemovesEverything() throws Exception {
        session.saveSession(TOKEN, 900, "ADMIN");
        session.clear();
        assertFalse(session.isLoggedIn());
        assertNull(session.getRoleHint());
    }

    @Test
    public void roleHintAvailableWhileValid() throws Exception {
        session.saveSession(TOKEN, 900, "REVIEWER");
        assertEquals("REVIEWER", session.getRoleHint());
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsNonPositiveExpiresIn() throws Exception {
        session.saveSession(TOKEN, 0, null);
    }

    private static String jwt(String payloadJson) {
        Base64.Encoder enc = Base64.getUrlEncoder().withoutPadding();
        return enc.encodeToString("{\"alg\":\"none\"}".getBytes(StandardCharsets.UTF_8)) + "."
                + enc.encodeToString(payloadJson.getBytes(StandardCharsets.UTF_8)) + ".firma";
    }

    @Test
    public void jwtRoleClaim() {
        assertEquals("ADMIN", JwtClaims.roleFromToken(jwt("{\"role\":\"ADMIN\"}")));
        assertEquals("REVIEWER", JwtClaims.roleFromToken(jwt("{\"role\":\"ROLE_REVIEWER\"}")));
        assertNull(JwtClaims.roleFromToken(jwt("{\"sub\":\"x\"}")));
        assertNull(JwtClaims.roleFromToken("basura"));
    }
}
