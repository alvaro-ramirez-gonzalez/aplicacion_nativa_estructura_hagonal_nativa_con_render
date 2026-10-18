package com.example.gestionusuarios.session;

import android.content.SharedPreferences;

public final class PrefsKeyValueStore implements KeyValueStore {
    private final SharedPreferences prefs;

    public PrefsKeyValueStore(SharedPreferences prefs) {
        this.prefs = prefs;
    }

    @Override public String getString(String key) { return prefs.getString(key, null); }
    @Override public long getLong(String key, long def) { return prefs.getLong(key, def); }
    @Override public void putString(String key, String value) { prefs.edit().putString(key, value).apply(); }
    @Override public void putLong(String key, long value) { prefs.edit().putLong(key, value).apply(); }
    @Override public void remove(String key) { prefs.edit().remove(key).apply(); }
    @Override public void clear() { prefs.edit().clear().apply(); }
}
