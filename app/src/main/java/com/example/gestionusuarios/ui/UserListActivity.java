package com.example.gestionusuarios.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.gestionusuarios.R;
import com.example.gestionusuarios.data.Resource;
import com.example.gestionusuarios.databinding.ActivityUserListBinding;
import com.example.gestionusuarios.model.Role;
import com.example.gestionusuarios.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class UserListActivity extends BaseActivity {
    private ActivityUserListBinding b;
    private UserListViewModel vm;
    private UserAdapter adapter;
    private List<User> all = new ArrayList<>();

    private final ActivityResultLauncher<Intent> launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            r -> { if (r.getResultCode() == RESULT_OK) vm.load(); });

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityUserListBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        setSupportActionBar(b.toolbar);
        vm = new ViewModelProvider(this).get(UserListViewModel.class);

        adapter = new UserAdapter(u -> launcher.launch(
                new Intent(this, UserDetailActivity.class).putExtra(UserDetailActivity.EXTRA_ID, u.id)));
        b.recycler.setLayoutManager(new LinearLayoutManager(this));
        b.recycler.setAdapter(adapter);

        // Ayuda visual según rol (el servidor sigue siendo la autoridad).
        boolean isAdmin = Role.ADMIN.equals(app().session.getRoleHint());
        b.newUserFab.setVisibility(isAdmin ? View.VISIBLE : View.GONE);
        b.newUserFab.setOnClickListener(v -> launcher.launch(new Intent(this, UserFormActivity.class)));

        b.searchInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int a, int c, int d) { }
            @Override public void onTextChanged(CharSequence s, int a, int c, int d) { }
            @Override public void afterTextChanged(Editable s) { applyFilter(); }
        });
        b.retryButton.setOnClickListener(v -> vm.load());
        vm.state.observe(this, this::render);
        if (vm.state.getValue() == null) vm.load();
    }

    private void render(Resource<List<User>> r) {
        if (r == null) return;
        b.progress.setVisibility(r.status == Resource.Status.LOADING ? View.VISIBLE : View.GONE);
        b.errorLayout.setVisibility(r.status == Resource.Status.ERROR ? View.VISIBLE : View.GONE);
        if (r.status == Resource.Status.SUCCESS) {
            all = r.data;
            applyFilter();
        } else {
            b.recycler.setVisibility(View.GONE);
            b.emptyText.setVisibility(View.GONE);
            if (r.status == Resource.Status.ERROR && !handleUnauthorized(r.error)) {
                b.errorText.setText(r.error.message);
            }
        }
    }

    private void applyFilter() {
        String q = b.searchInput.getText() == null ? "" : b.searchInput.getText().toString().trim().toLowerCase(Locale.ROOT);
        List<User> shown = new ArrayList<>();
        for (User u : all) {
            if (q.isEmpty() || contains(u.name, q) || contains(u.email, q)) shown.add(u);
        }
        adapter.submitList(shown);
        boolean empty = shown.isEmpty();
        b.recycler.setVisibility(empty ? View.GONE : View.VISIBLE);
        b.emptyText.setVisibility(empty ? View.VISIBLE : View.GONE);
        b.emptyText.setText(all.isEmpty() ? R.string.list_empty : R.string.no_matches);
    }

    private static boolean contains(String s, String q) {
        return s != null && s.toLowerCase(Locale.ROOT).contains(q);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_refresh) { vm.load(); return true; }
        if (item.getItemId() == R.id.action_logout) {
            app().session.clear(); // cierre solo local: la API no ofrece logout
            goToLogin(null);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
