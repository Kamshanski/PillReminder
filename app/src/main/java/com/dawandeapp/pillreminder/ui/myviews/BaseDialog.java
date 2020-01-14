package com.dawandeapp.pillreminder.ui.myviews;

import android.app.Dialog;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public abstract class BaseDialog extends Dialog {
    public BaseDialog(@NonNull Context context) {
        this(context, 0);
    }

    protected BaseDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        initDialog();
    }

    protected BaseDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        initDialog();
    }

    abstract protected void initDialog();

    @Override
    public void create() {
        super.create();
        findViews();
        setDefaultValues();
        setListeners();
    }

    @Override
    public void show() {
        super.show();
        preform();
    }

    abstract protected void findViews();
    abstract protected void setDefaultValues();
    abstract protected void setListeners();
    abstract protected void preform();
}

