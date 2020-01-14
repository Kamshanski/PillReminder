package com.dawandeapp.pillreminder.ui.shared.preferences;

import android.view.View;

import com.dawandeapp.pillreminder.R;
import com.dawandeapp.pillreminder.controller.Provider;
import com.dawandeapp.pillreminder.ui.shared.BaseFragment;


public class PreferencesFragment extends BaseFragment {

    @Override
    protected void initInstance() {
        provider = Provider.getInstance(null, null);
        provider.setPreferencesFragment(this);
    }

    @Override
    protected void findViews(View fv) {

    }

    public PreferencesFragment() {
        super();
    }

    @Override
    protected void initUI(View view) {

    }

    @Override
    protected void setNotifiers(View fv) {

    }

    @Override
    public int getLayoutID() {
        return R.layout.fragment_preferences;
    }

}
