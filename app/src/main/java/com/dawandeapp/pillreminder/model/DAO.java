package com.dawandeapp.pillreminder.model;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.dawandeapp.pillreminder.utilities.M;

import java.util.List;

public class DAO {

    private PillDatabase db;
    private PillBluetooth bl;

    public final static int DB = 0;
    public final static int BL = 1;


    public DAO(PillDAO... daos) {
        if (daos != null) {
            for (int i = 0; i < daos.length; i++) {
                M.d(daos[i].getClass().toString());
                if (daos[i].getClass().equals(PillDatabase.class)) {
                    db = (PillDatabase) daos[i];
                } else if (daos[i].getClass().equals(PillBluetooth.class)) {
                    bl = (PillBluetooth) daos[i];
                } else {
                    M.d("Some wrong DB was posted inside DAOMap: " + daos.getClass().toGenericString());
                }

            }
        } else M.d("No model was passed into dao");
    }

    public PillDAO from(int source) {
        switch (source) {
            case DB:
                if (!DB_isNull()) {
                    M.d("DB is null");
                }
                return db;
            case BL:
                if (!BL_isNull()) {
                    M.d("BL is null");
                }
                return bl;
            default :
                M.d("Unknown DAO code");
                return null;
        }
    }

    public PillDatabase DB() {
        return db;
    }

    public PillBluetooth BL() {
        return bl;
    }

    public boolean DB_isNull() {
        return db == null;
    }

    public boolean BL_isNull() {
        return bl == null;
    }

}
