package com.example.gestionusuarios.ui;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.gestionusuarios.App;
import com.example.gestionusuarios.data.Resource;
import com.example.gestionusuarios.model.User;

public class UserDetailViewModel extends AndroidViewModel {
    public final MutableLiveData<Resource<User>> user = new MutableLiveData<>();
    public final MutableLiveData<Resource<Void>> deleteState = new MutableLiveData<>();

    public UserDetailViewModel(@NonNull Application application) {
        super(application);
    }

    public void load(String id) {
        user.setValue(Resource.loading());
        ((App) getApplication()).users.get(id, r -> user.postValue(Resource.from(r)));
    }

    public void delete(String id) {
        Resource<Void> cur = deleteState.getValue();
        if (cur != null && cur.status == Resource.Status.LOADING) return;
        deleteState.setValue(Resource.loading());
        ((App) getApplication()).users.delete(id, r -> deleteState.postValue(Resource.from(r)));
    }
}
