package com.example.gestionusuarios.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.example.gestionusuarios.R;
import com.example.gestionusuarios.data.Resource;
import com.example.gestionusuarios.databinding.ActivityUserDetailBinding;
import com.example.gestionusuarios.model.Role;
import com.example.gestionusuarios.model.User;
import com.example.gestionusuarios.util.Labels;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

public class UserDetailActivity extends BaseActivity {
    public static final String EXTRA_ID = "id";

    private ActivityUserDetailBinding b;
    private UserDetailViewModel vm;
    private String id;
    private String currentName = "";

    private final ActivityResultLauncher<Intent> editLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            r -> {
                if (r.getResultCode() == RESULT_OK) {
                    setResult(RESULT_OK); // la lista debe recargarse
                    vm.load(id);
                }
            });

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityUserDetailBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        setSupportActionBar(b.toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(R.string.detail_title);

        id = getIntent().getStringExtra(EXTRA_ID);
        if (id == null) { finish(); return; }
        vm = new ViewModelProvider(this).get(UserDetailViewModel.class);

        boolean isAdmin = Role.ADMIN.equals(app().session.getRoleHint()); // solo presentación
        b.actionsLayout.setVisibility(isAdmin ? View.VISIBLE : View.GONE);
        b.editButton.setOnClickListener(v -> editLauncher.launch(
                new Intent(this, UserFormActivity.class).putExtra(UserFormActivity.EXTRA_ID, id)));
        b.deleteButton.setOnClickListener(v -> confirmDelete());
        b.retryButton.setOnClickListener(v -> vm.load(id));

        vm.user.observe(this, this::renderUser);
        vm.deleteState.observe(this, this::renderDelete);
        if (vm.user.getValue() == null) vm.load(id);
    }

    private void renderUser(Resource<User> r) {
        if (r == null) return;
        b.progress.setVisibility(r.status == Resource.Status.LOADING ? View.VISIBLE : View.GONE);
        b.contentLayout.setVisibility(r.status == Resource.Status.SUCCESS ? View.VISIBLE : View.GONE);
        b.errorLayout.setVisibility(r.status == Resource.Status.ERROR ? View.VISIBLE : View.GONE);
        if (r.status == Resource.Status.SUCCESS) {
            User u = r.data;
            currentName = u.name == null ? "" : u.name;
            b.nameValue.setText(u.name);
            b.emailValue.setText(u.email);
            b.roleValue.setText(Labels.role(this, u.role));
            b.statusValue.setText(Labels.status(this, u.status));
            b.idValue.setText(u.id);
        } else if (r.status == Resource.Status.ERROR && !handleUnauthorized(r.error)) {
            b.errorText.setText(r.error.message);
        }
    }

    private void confirmDelete() {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.delete_title)
                .setMessage(getString(R.string.delete_message, currentName))
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.delete, (d, w) -> vm.delete(id))
                .show();
    }

    private void renderDelete(Resource<Void> r) {
        if (r == null) return;
        boolean loading = r.status == Resource.Status.LOADING;
        if (loading) b.progress.setVisibility(View.VISIBLE);
        b.editButton.setEnabled(!loading);
        b.deleteButton.setEnabled(!loading);
        if (r.status == Resource.Status.SUCCESS) {
            setResult(RESULT_OK);
            finish();
        } else if (r.status == Resource.Status.ERROR) {
            b.progress.setVisibility(View.GONE);
            if (!handleUnauthorized(r.error)) {
                Snackbar.make(b.getRoot(), r.error.message, Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.cancel, v -> { }).show();
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
