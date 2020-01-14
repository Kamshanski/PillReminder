package com.dawandeapp.pillreminder.ui.shared;

import android.os.Bundle;

import com.dawandeapp.pillreminder.ui.shared.home.HomeFragment;
import com.dawandeapp.pillreminder.ui.shared.pills.PillsFragment;
import com.dawandeapp.pillreminder.ui.shared.preferences.PreferencesFragment;

public class SharedFragmentFactory {

    //Сдлеать FlyWeight list для того, чтобы потом использовать onSavedState для сохранения состояние

    public SharedFragmentFactory() {
    }

    public BaseFragment get(int position, Bundle bundle) {
        BaseFragment fragment;
        switch (position) {
            case 0:
                fragment = new PillsFragment();
                break;
            case 2:
                fragment = new PreferencesFragment();
                break;
            default: // case 1:
                fragment = new HomeFragment();
                break;
        }
        fragment.setArguments(bundle);
        return fragment;
    }
}
