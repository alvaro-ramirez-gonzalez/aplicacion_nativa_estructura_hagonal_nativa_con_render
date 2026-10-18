package com.example.gestionusuarios.ui;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.gestionusuarios.App;
import com.example.gestionusuarios.data.Resource;

public class LoginViewModel extends AndroidViewModel {
    public final MutableLiveData<Resource<Void>> state = new MutableLiveData<>();

    public LoginViewModel(@NonNull Application application) {
        super(application);
    }

    public void login(String email, String password) {
        Resource<Void> cur = state.getValue();
        if (cur != null && cur.status == Resource.Status.LOADING) return;
        state.setValue(Resource.loading());
        ((App) getApplication()).auth.login(email, password,
                r -> state.postValue(Resource.from(r)));
    }
}
