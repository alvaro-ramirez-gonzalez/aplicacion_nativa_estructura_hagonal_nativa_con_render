package com.example.gestionusuarios.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.gestionusuarios.App;
import com.example.gestionusuarios.BuildConfig;
import com.example.gestionusuarios.R;
import com.example.gestionusuarios.data.Resource;
import com.example.gestionusuarios.databinding.ActivityLoginBinding;
import com.example.gestionusuarios.util.AppConfig;
import com.example.gestionusuarios.util.Validators;

public class LoginActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "message";

    private ActivityLoginBinding b;
    private LoginViewModel vm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        vm = new ViewModelProvider(this).get(LoginViewModel.class);

        if (((App) getApplication()).session.isLoggedIn()) {
            openList();
            return;
        }
        String msg = getIntent().getStringExtra(EXTRA_MESSAGE);
        if (msg != null && savedInstanceState == null) showError(msg);

        b.loginButton.setOnClickListener(v -> attempt());
        vm.state.observe(this, this::render);
    }

    private void attempt() {
        b.errorText.setVisibility(View.GONE);
        b.emailLayout.setError(null);
        b.passwordLayout.setError(null);
        String email = b.emailInput.getText() == null ? "" : b.emailInput.getText().toString().trim();
        String password = b.passwordInput.getText() == null ? "" : b.passwordInput.getText().toString();

        boolean ok = true;
        if (!Validators.isValidEmail(email)) { b.emailLayout.setError(getString(R.string.err_email)); ok = false; }
        if (!Validators.isNotBlank(password)) { b.passwordLayout.setError(getString(R.string.err_required)); ok = false; }
        if (!ok) return;
        if (AppConfig.isBaseUrlPlaceholder(BuildConfig.API_BASE_URL)) {
            showError(getString(R.string.url_not_configured));
            return;
        }
        vm.login(email, password);
    }

    private void render(Resource<Void> r) {
        if (r == null) return;
        boolean loading = r.status == Resource.Status.LOADING;
        b.progress.setVisibility(loading ? View.VISIBLE : View.GONE);
        b.slowHint.setVisibility(loading ? View.VISIBLE : View.GONE);
        b.loginButton.setEnabled(!loading);
        if (r.status == Resource.Status.SUCCESS) {
            b.passwordInput.setText("");
            openList();
        } else if (r.status == Resource.Status.ERROR) {
            showError(r.error.message);
        }
    }

    private void showError(String message) {
        b.errorText.setText(message);
        b.errorText.setVisibility(View.VISIBLE);
    }

    private void openList() {
        Intent i = new Intent(this, UserListActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }
}
