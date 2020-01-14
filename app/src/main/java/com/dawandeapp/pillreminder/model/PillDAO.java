package com.dawandeapp.pillreminder.model;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import java.util.List;

public interface PillDAO {

    int SUCCESS = 0;
    int ERROR_ID_IS_NOT_VALID = -1;
    int ERROR_INSERT = -2;

    @NonNull
    LiveData<List<Pill>> getPills();
    void insertPill(Pill pill);
    void updatePill(Pill pill);
    void deletePill(Pill pill);
}