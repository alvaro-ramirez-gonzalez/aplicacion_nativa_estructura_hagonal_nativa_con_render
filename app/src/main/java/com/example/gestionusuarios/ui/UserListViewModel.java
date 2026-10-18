package com.example.gestionusuarios.ui;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.gestionusuarios.App;
import com.example.gestionusuarios.data.Resource;
import com.example.gestionusuarios.model.User;

import java.util.List;

public class UserListViewModel extends AndroidViewModel {
    public final MutableLiveData<Resource<List<User>>> state = new MutableLiveData<>();

    public UserListViewModel(@NonNull Application application) {
        super(application);
    }

    public void load() {
        Resource<List<User>> cur = state.getValue();
        if (cur != null && cur.status == Resource.Status.LOADING) return;
        state.setValue(Resource.loading());
        ((App) getApplication()).users.list(r -> state.postValue(Resource.from(r)));
    }
}
