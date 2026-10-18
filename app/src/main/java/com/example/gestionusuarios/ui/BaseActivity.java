package com.example.gestionusuarios.ui;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gestionusuarios.App;
import com.example.gestionusuarios.R;
import com.example.gestionusuarios.network.ApiError;

public abstract class BaseActivity extends AppCompatActivity {

    protected App app() {
        return (App) getApplication();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!app().session.isLoggedIn()) {
            goToLogin(getString(R.string.session_expired));
        }
    }

    protected void goToLogin(String message) {
        Intent i = new Intent(this, LoginActivity.class);
        if (message != null) i.putExtra(LoginActivity.EXTRA_MESSAGE, message);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }

    /** @return true si el error era 401 y ya se redirigió al login. */
    protected boolean handleUnauthorized(ApiError error) {
        if (error != null && error.isUnauthorized()) {
            app().session.clear();
            goToLogin(getString(R.string.session_expired));
            return true;
        }
        return false;
    }
}
