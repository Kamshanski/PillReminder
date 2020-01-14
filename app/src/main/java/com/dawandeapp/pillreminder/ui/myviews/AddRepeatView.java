package com.dawandeapp.pillreminder.ui.myviews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.widget.LinearLayoutCompat;

import com.dawandeapp.pillreminder.R;

public class AddRepeatView extends LinearLayoutCompat {

    private EditText pillAmount, pillTimePoint;

    public AddRepeatView(Context context) {
        this(context, null);
    }
    public AddRepeatView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public AddRepeatView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    //TODO: заменить attachToRoot на true и заменить в layout LinearLayout на merge
    private void init() {
        removeAllViews();
        View view = LayoutInflater.from(getContext()).inflate(R.layout.add_repeat_row, this, false);
        findViews(view);
        addView(view);
    }

    private void findViews(View view) {
        pillTimePoint = view.findViewById(R.id.edt_pillTimePoint);
        pillAmount = view.findViewById(R.id.edt_pillAmount);

        //TODO: заменить это, на адекватный Editor с маской
        //pillTimePoint.setOn
    }

    public String getPillAmount() {
        return pillAmount.getText().toString();
    }

    public String getTime() {
        return pillTimePoint.getText().toString();
    }

    public void setTime(String time) {
        pillTimePoint.setText(time);
    }
}
