package com.dawandeapp.pillreminder.ui.myviews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.dawandeapp.pillreminder.R;
import com.dawandeapp.pillreminder.controller.Provider;
import com.dawandeapp.pillreminder.model.Pill;
import com.dawandeapp.pillreminder.ui.shared.PillDialog;
import com.dawandeapp.pillreminder.utilities.M;
import com.dawandeapp.pillreminder.utilities.OnSwipeTouchListener;

public class PillRow extends LinearLayoutCompat {

    TextView tx_name, tx_schedule, tx_number;
    ImageButton button;
    ConstraintLayout background;

    PillRowAdapter adapter;

    public PillRow(Context context) {
        this(context, null);
    }

    public PillRow(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PillRow(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    //TODO: заменить attachToRoot на true и заменить в layout LinearLayout на merge
    private void init() {
        removeAllViews();

        View view = LayoutInflater.from(getContext()).inflate(R.layout.pill_row, this, false);
        findViews(view);

        addView(view);
    }

    private void findViews(View view) {
        tx_name = view.findViewById(R.id.tx_name);
        tx_schedule = view.findViewById(R.id.tx_schedule);
        tx_number = view.findViewById(R.id.tx_number);
        button = view.findViewById(R.id.imb_add_edit);
        background = view.findViewById(R.id.background);
    }

    public void setAdapter(PillRowAdapter adapter) {
        this.adapter = adapter;
        setPill(adapter.getPill());
    }

    private void setPill(Pill pill) {
        if (pill != null) {
            if (!pill.isNull()) {
                setID(pill.getId());
                setName(pill.getName());
                setSchedule(pill.getSchedule().toString());
                setImportance(pill.getImportance());

                setEditButton();
                setDeleteSwipe();
            } else {
                setID(pill.getId());
                setName("Null Pill");
                setSchedule("No schedule");
                setImportance("No importance");

                setAddButton();
                setDeleteSwipe();
                M.d("pill " + pill.getId() + " is NullPill!");
            }
        } else {
            M.d("pill is null!");
        }
    }

    private void setID(int id) {
        if (tx_number != null) {
            tx_number.setText(String.valueOf(id+1));
        } else { M.d("tx_number is null!"); }
    }

    private void setName(String name) {
        if (tx_name != null) {
            tx_name.setText(name);
        } else M.d("tx_name is null!");

    }

    private void setSchedule(String schedule) {
        if (tx_schedule != null) {
            tx_schedule.setText(schedule);
        } else M.d("tx_schedule is null!");
    }

    private void setImportance(String importance) {
        switch (importance) {
            case Pill.DIETARY_SUPPLEMENTS:
                //
            case Pill.UNIMPORTANT:
                //
            case Pill.IMPORTANT:
                //
            case Pill.VITAL:
                background.setBackground(getResources().getDrawable(
                        R.drawable.card_pill,
                        getContext().getTheme()));
                break;
            default:
                background.setBackground(getResources().getDrawable(
                        R.drawable.card_no_pill,
                        getContext().getTheme()));
                break;
        }
    }

    private void setEditButton() {
        if (button != null) {
            button.setImageDrawable(button.getResources().getDrawable(R.drawable.ic_edit, button.getContext().getTheme()));
            button.setOnClickListener(v -> runAddPillDialog());
        } else M.d("buttonEdit is null!");
    }

    private void setAddButton() {
        if (button != null) {
            button.setImageDrawable(button.getResources().getDrawable(R.drawable.ic_add, button.getContext().getTheme()));
            button.setOnClickListener(v -> runAddPillDialog());
        } else M.d("buttonAdd is null!");
    }


    private void setDeleteSwipe() {
        setOnTouchListener(new OnSwipeTouchListener(getContext()) {
            @Override
            public void onSwipeRight() {
                M.d("Move pill RIGHT: " + adapter.getPill().getName());
                Provider provider = Provider.getInstance(null, null);
                M.d("Send text to BLE :" + adapter.getPill().getName());
                provider.onPillRightSwipe(adapter.getPill());
                M.d("sent pillName to BLE");
            }

            @Override
            public void onSwipeLeft() {
                M.d("Move pill LEFT: " + adapter.getPill().getName());
                M.wSh(getContext(), "Препарат " + adapter.getPill().getName() + " был удалён");
                adapter.deletePill(adapter.getPill());
            }

            @Override
            public void onSwipeTop() {
                M.d("Move pill TOP: " + adapter.getPill().getName());
            }

            @Override
            public void onSwipeBottom() {
                M.d("Move pill BOTTOM: " + adapter.getPill().getName());
            }
        });
    }

    private void runAddPillDialog() {
        final PillDialog addDialog = new PillDialog(getContext(), adapter);
        addDialog.setID(adapter.getPill().getId());
        addDialog.create(); //Создание Диалога
        addDialog.show(); //Показ Диалога
    }



    public void notifyPillChanged() {
        setPill(adapter.getPill());
    }

    public abstract static class PillRowAdapter {
        private final int index;

        public PillRowAdapter(int index) {
            this.index = index;
        }

        public final int getIndex() {
            return index;
        }
        public abstract void updatePill(Pill pill);
        public abstract void insertPill(Pill pill);
        public abstract void deletePill(Pill pill);
        public abstract Pill getPill();
        public abstract void setPillObserver();
    }
}
