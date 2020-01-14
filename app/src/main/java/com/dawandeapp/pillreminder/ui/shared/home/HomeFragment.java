package com.dawandeapp.pillreminder.ui.shared.home;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.dawandeapp.pillreminder.R;
import com.dawandeapp.pillreminder.controller.Provider;
import com.dawandeapp.pillreminder.model.Pill;
import com.dawandeapp.pillreminder.ui.myviews.PillRow;
import com.dawandeapp.pillreminder.ui.shared.BaseFragment;
import com.dawandeapp.pillreminder.utilities.M;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends BaseFragment {
    List<PillRow> pillRows = new ArrayList<>();

    public HomeFragment() {
        super();
    }

    @Override
    protected void initInstance() {
        provider = Provider.getInstance(null, null);
        provider.setHomeFragment(this);
    }

    @Override
    protected void findViews(View fv) {
        pillRows.add(fv.findViewById(R.id.pillRow1));
        pillRows.add(fv.findViewById(R.id.pillRow2));
        pillRows.add(fv.findViewById(R.id.pillRow3));
        pillRows.add(fv.findViewById(R.id.pillRow4));
    }

    @Override
    protected void initUI(View view) {
        LiveData<List<Pill>> pills = provider.getPills();

        for (int i = 0; i < pillRows.size(); i++) {
            PillRow row = pillRows.get(i);
            PillRow.PillRowAdapter adapter = new PillRow.PillRowAdapter(i) {

                @Override
                public void updatePill(Pill pill) {
                    updatePill(pill);
                }

                @Override
                public void insertPill(Pill pill) {
                    provider.insertPill(pill);
                }

                @Override
                public void deletePill(Pill pill) {
                    provider.deletePill(pill);
                }

                @Override
                public Pill getPill() {
                    return provider.getPills().getValue().get(this.getIndex());
                }

                @Override
                public void setPillObserver() {

                }
            };
            row.setAdapter(adapter);
        }
    }

    @Override
    protected void setNotifiers(View fv) {
        LiveData<List<Pill>> pills = provider.getPills();
        pills.observe(this, new Observer<List<Pill>>() {
            @Override
            public void onChanged(List<Pill> pills) {
                for (PillRow pillRow : pillRows) {
                    pillRow.notifyPillChanged();
                }
            }
        });
    }

    @Override
    public int getLayoutID() {
        return R.layout.fragment_home;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        M.d("Savavavavavvaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }

}

