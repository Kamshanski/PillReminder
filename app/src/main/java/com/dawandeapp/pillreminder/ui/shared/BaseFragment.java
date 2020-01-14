package com.dawandeapp.pillreminder.ui.shared;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.dawandeapp.pillreminder.controller.Provider;

public abstract class BaseFragment extends Fragment {

    protected Provider provider;

    protected BaseFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    //TODO: заменить attachToRoot на true и поменть FrameL на ConstraintL во всех fragmentLayout
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(getLayoutID(), container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initInstance();
        findViews(getView());
        initUI(getView());
        setNotifiers(getView());
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
//        if (context instanceof MainViewModelForFragmentProvider) {
//            viewModel = ((MainViewModelForFragmentProvider) context).get();
//        } else {
//            throw new RuntimeException(context.toString() + " должен имплементить MainViewModelForFragmentProvider");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    protected abstract void initInstance();
    protected abstract void findViews(View fv);
    protected abstract void initUI(View fv);
    protected abstract void setNotifiers(View fv);
    protected abstract int getLayoutID();
}
