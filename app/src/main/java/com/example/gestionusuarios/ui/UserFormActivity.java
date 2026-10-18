package com.example.gestionusuarios.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.example.gestionusuarios.R;
import com.example.gestionusuarios.data.Resource;
import com.example.gestionusuarios.databinding.ActivityUserFormBinding;
import com.example.gestionusuarios.model.Role;
import com.example.gestionusuarios.model.Status;
import com.example.gestionusuarios.model.User;
import com.example.gestionusuarios.util.AppConfig;
import com.example.gestionusuarios.util.Validators;

/** Crea (sin EXTRA_ID) o edita (con EXTRA_ID) un usuario. */
public class UserFormActivity extends BaseActivity {
    public static final String EXTRA_ID = "id";

    private ActivityUserFormBinding b;
    private UserFormViewModel vm;
    private String editId;
    private boolean populated;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityUserFormBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        setSupportActionBar(b.toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        vm = new ViewModelProvider(this).get(UserFormViewModel.class);

        editId = getIntent().getStringExtra(EXTRA_ID);
        boolean edit = editId != null;
        setTitle(edit ? R.string.edit_title : R.string.create_title);

        b.roleInput.setSimpleItems(Role.ALL);
        b.statusInput.setSimpleItems(Status.ALL);
        if (!edit) b.roleInput.setText(Role.MEMBER, false);
        b.statusLayout.setVisibility(edit ? View.VISIBLE : View.GONE);

        boolean showPassword = !edit || AppConfig.PASSWORD_UPDATE_ENABLED;
        b.passwordLayout.setVisibility(showPassword ? View.VISIBLE : View.GONE);
        if (edit) b.passwordLayout.setHint(getString(R.string.password_new_optional));
        b.passwordNotice.setVisibility(edit && showPassword ? View.VISIBLE : View.GONE);
        b.formLayout.setVisibility(edit ? View.GONE : View.VISIBLE); // en edición, oculto hasta cargar

        b.saveButton.setOnClickListener(v -> save());
        b.retryButton.setOnClickListener(v -> vm.load(editId));
        vm.saveState.observe(this, this::renderSave);
        if (edit) {
            vm.loaded.observe(this, this::renderLoaded);
            if (vm.loaded.getValue() == null) vm.load(editId);
        }
    }

    private void renderLoaded(Resource<User> r) {
        if (r == null) return;
        b.progress.setVisibility(r.status == Resource.Status.LOADING ? View.VISIBLE : View.GONE);
        b.retryButton.setVisibility(r.status == Resource.Status.ERROR ? View.VISIBLE : View.GONE);
        if (r.status == Resource.Status.SUCCESS) {
            b.errorText.setVisibility(View.GONE);
            b.formLayout.setVisibility(View.VISIBLE);
            if (!populated) {
                populated = true;
                b.nameInput.setText(r.data.name);
                b.emailInput.setText(r.data.email);
                b.roleInput.setText(r.data.role, false);
                b.statusInput.setText(r.data.status, false);
            }
        } else if (r.status == Resource.Status.ERROR && !handleUnauthorized(r.error)) {
            showError(r.error.message);
        }
    }

    private void save() {
        b.errorText.setVisibility(View.GONE);
        b.nameLayout.setError(null);
        b.emailLayout.setError(null);
        b.passwordLayout.setError(null);
        b.roleLayout.setError(null);
        b.statusLayout.setError(null);

        boolean edit = editId != null;
        String name = text(b.nameInput).trim();
        String email = text(b.emailInput).trim();
        String password = text(b.passwordInput);
        String role = text(b.roleInput);
        String status = text(b.statusInput);

        boolean ok = true;
        if (!Validators.isNotBlank(name)) { b.nameLayout.setError(getString(R.string.err_required)); ok = false; }
        if (!Validators.isValidEmail(email)) { b.emailLayout.setError(getString(R.string.err_email)); ok = false; }
        if (!edit && !Validators.isNotBlank(password)) { b.passwordLayout.setError(getString(R.string.err_required)); ok = false; }
        if (!Validators.isOneOf(role, Role.ALL)) { b.roleLayout.setError(getString(R.string.err_choose)); ok = false; }
        if (edit && !Validators.isOneOf(status, Status.ALL)) { b.statusLayout.setError(getString(R.string.err_choose)); ok = false; }
        if (!ok) return;

        if (edit) {
            String pw = AppConfig.PASSWORD_UPDATE_ENABLED && !password.isEmpty() ? password : null;
            vm.update(editId, name, email, pw, role, status);
        } else {
            vm.create(name, email, password, role);
        }
    }

    private void renderSave(Resource<Void> r) {
        if (r == null) return;
        boolean loading = r.status == Resource.Status.LOADING;
        b.progress.setVisibility(loading ? View.VISIBLE : View.GONE);
        b.saveButton.setEnabled(!loading);
        if (r.status == Resource.Status.SUCCESS) {
            setResult(RESULT_OK);
            finish();
        } else if (r.status == Resource.Status.ERROR && !handleUnauthorized(r.error)) {
            showError(r.error.message);
        }
    }

    private void showError(String message) {
        b.errorText.setText(message);
        b.errorText.setVisibility(View.VISIBLE);
    }

    private static String text(TextView v) {
        return v.getText() == null ? "" : v.getText().toString();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
