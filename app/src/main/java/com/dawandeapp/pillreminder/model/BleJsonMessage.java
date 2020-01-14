package com.dawandeapp.pillreminder.model;

import androidx.annotation.NonNull;

import com.dawandeapp.pillreminder.utilities.M;
import com.dawandeapp.pillreminder.utilities.TimePair;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class BleJsonMessage {
    public static final char CLOCK = 'C';
    public static final char PILL = 'P';

    static final String TYPE = "type";
    static final String ID = "id";
    static final String DAYS = "days";
    static final String TIMES = "times";
    static final String SECONDS = "s";
    static final String MINUTES = "mi";
    static final String HOURS = "h";
    static final String DAY_OF_WEEK = "dw";
    static final String DAY_OF_MONTH = "dm";
    static final String MONTH = "mo";
    static final String YEAR = "y";


    private final JsonObject mObj;

    public BleJsonMessage(char messageType, Object src) {

        mObj = new JsonObject();
        mObj.add(TYPE, new JsonPrimitive(messageType));

        if (messageType == PILL) {
            addPillInfo((Pill) src);
        } else if (messageType == CLOCK) {
            addClockInfo((Clock) src);
        }
    }

    private void addPillInfo(Pill pill) {
        mObj.addProperty(ID, pill.getId());
        Schedule schedule = pill.getSchedule();

        JsonArray arrayDays = new JsonArray(schedule.getDays().size());
        for (int dayOfWeek : schedule.getDays()) {
            arrayDays.add(dayOfWeek);
        }
        mObj.add(DAYS, arrayDays);

        JsonArray arrayTimes = new JsonArray(schedule.getHoursAndMins().size());
        for (TimePair pair : schedule.getHoursAndMins()) {
            JsonArray hm = new JsonArray(2);
            hm.add(pair.first);
            hm.add(pair.second);
            arrayTimes.add(hm);
        }
        mObj.add(TIMES, arrayTimes);
    }

    private void addClockInfo(Clock clock) {
        mObj.addProperty(SECONDS, clock.sec);
        mObj.addProperty(MINUTES, clock.min);
        mObj.addProperty(HOURS, clock.hour);
        mObj.addProperty(DAY_OF_WEEK, clock.dow);
        mObj.addProperty(DAY_OF_MONTH, clock.dom);
        mObj.addProperty(MONTH, clock.month);
        mObj.addProperty(YEAR, clock.year);
    }

    @NonNull
    @Override
    public String toString() {
        return mObj.toString();
    }
}
