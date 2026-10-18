package com.example.gestionusuarios.ui;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.gestionusuarios.App;
import com.example.gestionusuarios.data.Resource;
import com.example.gestionusuarios.model.CreateUserRequest;
import com.example.gestionusuarios.model.UpdateUserRequest;
import com.example.gestionusuarios.model.User;

import java.util.UUID;

public class UserFormViewModel extends AndroidViewModel {
    public final MutableLiveData<Resource<User>> loaded = new MutableLiveData<>();
    public final MutableLiveData<Resource<Void>> saveState = new MutableLiveData<>();
    private final String newId = UUID.randomUUID().toString();

    public UserFormViewModel(@NonNull Application application) {
        super(application);
    }

    public void load(String id) {
        loaded.setValue(Resource.loading());
        ((App) getApplication()).users.get(id, r -> loaded.postValue(Resource.from(r)));
    }

    private boolean busy() {
        Resource<Void> cur = saveState.getValue();
        return cur != null && cur.status == Resource.Status.LOADING;
    }

    public void create(String name, String email, String password, String role) {
        if (busy()) return;
        saveState.setValue(Resource.loading());
        ((App) getApplication()).users.create(new CreateUserRequest(newId, name, email, password, role),
                r -> saveState.postValue(Resource.from(r)));
    }

    public void update(String id, String name, String email, String passwordOrNull, String role, String status) {
        if (busy()) return;
        saveState.setValue(Resource.loading());
        ((App) getApplication()).users.update(id, new UpdateUserRequest(name, email, passwordOrNull, role, status),
                r -> saveState.postValue(Resource.from(r)));
    }
}
