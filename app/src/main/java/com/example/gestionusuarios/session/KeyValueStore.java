package com.example.gestionusuarios.session;

public interface KeyValueStore {
    String getString(String key);
    long getLong(String key, long def);
    void putString(String key, String value);
    void putLong(String key, long value);
    void remove(String key);
    void clear();
}
