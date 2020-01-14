package com.dawandeapp.pillreminder.ui.shared;

import android.content.Context;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.dawandeapp.pillreminder.R;
import com.dawandeapp.pillreminder.model.Pill;
import com.dawandeapp.pillreminder.model.Schedule;
import com.dawandeapp.pillreminder.ui.myviews.AddRepeatView;
import com.dawandeapp.pillreminder.ui.myviews.BaseDialog;
import com.dawandeapp.pillreminder.ui.myviews.PillRow;
import com.dawandeapp.pillreminder.utilities.M;
import com.dawandeapp.pillreminder.utilities.TimePair;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PillDialog extends BaseDialog {
    private PillRow.PillRowAdapter pillAdapter;
    private int id;

    RadioGroup radbGroup_importance;
    EditText edt_containerCapacity, edt_pillName;
    List<CheckBox> checkBoxes = new ArrayList<>();
    List<AddRepeatView> addRepeatViews = new ArrayList<>();
    TextView tx_addRepeatRow;
    Button btn_create;
    LinearLayout LL_repeats;

    public PillDialog(@NonNull Context context, PillRow.PillRowAdapter adapter) {
        super(context, 0);
        this.pillAdapter = adapter;
    }

    public void setID(int id) {
        this.id = id;
    }

    @Override
    protected void initDialog() {
        setTitle("Добавить таблетки в ячёйку " + id);
        setContentView(R.layout.dialog_add_pill);
    }

    @Override
    protected void findViews() {
        radbGroup_importance = findViewById(R.id.radbGroup_importance);
        //edt_containerCapacity = findViewById(R.id.edt_containerCapacity);
        edt_pillName = findViewById(R.id.tx_pillName);
        //tx_addRepeatRow = findViewById(R.id.tx_addRepeatRow);  // Будет использоватья при динамическом списке
        btn_create = findViewById(R.id.btn_createPill);
        LL_repeats = findViewById(R.id.LL_repeat);

        checkBoxes.add(findViewById(R.id.сhbox_monday));
        checkBoxes.add(findViewById(R.id.сhbox_tuesday));
        checkBoxes.add(findViewById(R.id.сhbox_wednesday));
        checkBoxes.add(findViewById(R.id.сhbox_thursday));
        checkBoxes.add(findViewById(R.id.сhbox_friday));
        checkBoxes.add(findViewById(R.id.сhbox_saturday));
        checkBoxes.add(findViewById(R.id.сhbox_sunday));

        addRepeatViews.add(findViewById(R.id.addRepeatView1));

        //Убрать и заменить на динамически редактируемый список
        addRepeatViews.add(findViewById(R.id.addRepeatView2));
        addRepeatViews.add(findViewById(R.id.addRepeatView3));
    }
    @Override
    protected void setListeners() {
        btn_create.setOnClickListener(v -> {

            Pill.PillBuilder pillBuilder = new Pill.PillBuilder();

            pillBuilder.setID(id);

            //TODO: в будущем применить количество таблеток в браслете
            //edt_containerCapacity.getText();

            //TODO: добавить все возможне проверки и фильтры на EditText
            pillBuilder.setName(edt_pillName.getText().toString());

            int importance_id = radbGroup_importance.getCheckedRadioButtonId();
            switch (importance_id) {
                case R.id.radb_importance_vital:
                    pillBuilder.setImportance(Pill.VITAL);
                    break;
                case R.id.radb_importance_important:
                    pillBuilder.setImportance(Pill.IMPORTANT);
                    break;
                case R.id.radb_importance_unimportatnt:
                    pillBuilder.setImportance(Pill.UNIMPORTANT);
                    break;
                case R.id.radb_importance_dietary_supplements:
                    pillBuilder.setImportance(Pill.DIETARY_SUPPLEMENTS);
                    break;
            }

            Schedule schedule = new Schedule();

            for (CheckBox box : checkBoxes) {
                int day = 0;
                if (box.isChecked()) {
                    switch (box.getId()) {
                        case R.id.сhbox_monday:
                            day = 0;
                            break;
                        case R.id.сhbox_tuesday:
                            day = 1;
                            break;
                        case R.id.сhbox_wednesday:
                            day = 2;
                            break;
                        case R.id.сhbox_thursday:
                            day = 3;
                            break;
                        case R.id.сhbox_friday:
                            day = 4;
                            break;
                        case R.id.сhbox_saturday:
                            day = 5;
                            break;
                        case R.id.сhbox_sunday:
                            day = 6;
                            break;
                    }
                    schedule.addDay(day);
                }
            }

            //TODO заменить на список с AddRepeatView
            for (AddRepeatView row : addRepeatViews) {
                int hour, min, colonIndex;
                String timeStr = row.getTime();
                colonIndex = timeStr.indexOf(":");

                hour = Integer.valueOf(timeStr.substring(0, colonIndex++));
                min = Integer.valueOf(timeStr.substring(colonIndex));

                schedule.addTime(new TimePair(hour, min));
            }

            pillBuilder.setSchedule(schedule)
                    .setTimes(0)
                    .setLastTime(0);


            Pill pill = pillBuilder.build();

            M.d("Pill to be added");
            M.d(pill.toString());

            pillAdapter.insertPill(pill);

            dismiss();
        });

    }

    @Override
    protected void preform() {

    }

    @Override
    protected void setDefaultValues() {
        Pill prevPill = pillAdapter.getPill();
        if (prevPill != null && !prevPill.isNull()) {

            edt_pillName.setText(prevPill.getName());

            switch (prevPill.getImportance()) {
                case Pill.VITAL:
                    radbGroup_importance.check(R.id.radb_importance_vital); break;
                case Pill.UNIMPORTANT:
                    radbGroup_importance.check(R.id.radb_importance_unimportatnt); break;
                case Pill.IMPORTANT:
                    radbGroup_importance.check(R.id.radb_importance_important); break;
                case Pill.DIETARY_SUPPLEMENTS:
                    radbGroup_importance.check(R.id.radb_importance_dietary_supplements); break;
            }

            for (int i = 0; i < 7; i++) {
                if (prevPill.getSchedule().hasDay(i)) {
                    checkBoxes.get(i).setChecked(true);
                }
            }

            Iterator<TimePair> iterator = prevPill.getSchedule().getHoursAndMins().iterator();

            if (iterator.hasNext()) {
                for (int i = 0; iterator.hasNext(); i++) {
                    StringBuilder sb = new StringBuilder();
                    TimePair time = iterator.next();
                    sb.append(time.first)
                            .append(":")
                            .append(time.second);
                    addRepeatViews.get(i).setTime(sb.toString());
                }
            }
        }
        prevPill = null; // View isn't to hold pill
    }
}
