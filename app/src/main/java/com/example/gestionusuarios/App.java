package com.example.gestionusuarios;

import android.app.Application;

import com.example.gestionusuarios.data.AuthRepository;
import com.example.gestionusuarios.data.UserRepository;
import com.example.gestionusuarios.network.ApiClient;
import com.example.gestionusuarios.network.ApiService;
import com.example.gestionusuarios.session.KeystoreAesGcmCipher;
import com.example.gestionusuarios.session.PrefsKeyValueStore;
import com.example.gestionusuarios.session.SessionManager;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class App extends Application {
    public SessionManager session;
    public AuthRepository auth;
    public UserRepository users;

    @Override
    public void onCreate() {
        super.onCreate();
        session = new SessionManager(
                new PrefsKeyValueStore(getSharedPreferences("session_prefs", MODE_PRIVATE)),
                new KeystoreAesGcmCipher(),
                System::currentTimeMillis);
        ApiService api = ApiClient.create(BuildConfig.API_BASE_URL, session::getToken);
        Executor io = Executors.newFixedThreadPool(3);
        auth = new AuthRepository(api, session, io);
        users = new UserRepository(api, session, io);
    }
}
