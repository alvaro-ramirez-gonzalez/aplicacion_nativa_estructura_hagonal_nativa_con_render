package com.example.gestionusuarios.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gestionusuarios.databinding.ItemUserBinding;
import com.example.gestionusuarios.model.User;
import com.example.gestionusuarios.util.Labels;

import java.util.Objects;

public class UserAdapter extends ListAdapter<User, UserAdapter.VH> {

    public interface OnUserClick { void onClick(User user); }

    private static final DiffUtil.ItemCallback<User> DIFF = new DiffUtil.ItemCallback<User>() {
        @Override public boolean areItemsTheSame(@NonNull User a, @NonNull User b) {
            return Objects.equals(a.id, b.id);
        }
        @Override public boolean areContentsTheSame(@NonNull User a, @NonNull User b) {
            return Objects.equals(a.name, b.name) && Objects.equals(a.email, b.email)
                    && Objects.equals(a.role, b.role) && Objects.equals(a.status, b.status);
        }
    };

    private final OnUserClick listener;

    public UserAdapter(OnUserClick listener) {
        super(DIFF);
        this.listener = listener;
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VH(ItemUserBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        User u = getItem(position);
        Context c = h.b.getRoot().getContext();
        h.b.nameText.setText(u.name);
        h.b.emailText.setText(u.email);
        h.b.metaText.setText(Labels.role(c, u.role) + " · " + Labels.status(c, u.status));
        h.b.getRoot().setOnClickListener(v -> listener.onClick(u));
    }

    static class VH extends RecyclerView.ViewHolder {
        final ItemUserBinding b;
        VH(ItemUserBinding b) { super(b.getRoot()); this.b = b; }
    }
}
