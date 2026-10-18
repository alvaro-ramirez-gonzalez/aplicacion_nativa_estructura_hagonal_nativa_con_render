package com.example.gestionusuarios.data;

import com.example.gestionusuarios.network.ApiResult;

public interface Callback<T> {
    void onResult(ApiResult<T> result);
}
