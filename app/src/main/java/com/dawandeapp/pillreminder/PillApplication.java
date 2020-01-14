package com.dawandeapp.pillreminder;

import android.app.Application;

import com.dawandeapp.pillreminder.controller.Provider;

import java.lang.ref.SoftReference;

public class PillApplication extends Application {
    private static SoftReference<Provider> srController;

    @Override
    public void onCreate() {
        super.onCreate();
        //ВАЖНО. На данном этапе АрplicationContext щё не создан!!!!
        //srController = new SoftReference<>(Provider.getInstance(null, null));
    }

    public static Provider getController() {
        return PillApplication.srController.get();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
    }
}
