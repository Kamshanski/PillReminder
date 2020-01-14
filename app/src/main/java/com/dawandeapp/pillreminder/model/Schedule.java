package com.dawandeapp.pillreminder.model;

import androidx.annotation.NonNull;

import com.dawandeapp.pillreminder.utilities.M;
import com.dawandeapp.pillreminder.utilities.TimePair;
import com.google.gson.Gson;

import java.lang.reflect.UndeclaredThrowableException;
import java.util.TreeSet;

public class Schedule {
    private TreeSet<Integer> days = new TreeSet<>();
    private TreeSet<TimePair> hoursAndMins = new TreeSet<>();

    public Schedule() {
    }

    public Schedule(TreeSet<Integer> days, TreeSet<TimePair> hoursAndMins) {
        if (days != null) {
            this.days = days;
        }
        if (hoursAndMins != null) {
            this.hoursAndMins = hoursAndMins;
        }
    }

    public void addDay(int day) {
        days.add(day);
    }

    public void addTime(TimePair time) {
        hoursAndMins.add(time);
    }

    public TreeSet<Integer> getDays() {
        return days;
    }

    public TreeSet<TimePair> getHoursAndMins() {
        return hoursAndMins;
    }

    public boolean hasDay(int dayOfWeek) {
        if (days.add(dayOfWeek)) {
            if (!days.remove(dayOfWeek)) {
                throw new UndeclaredThrowableException(new Throwable("bya"));
            }
            return false;
        }
        return true;
    }

    public boolean hasTime(TimePair time) {
        if (hoursAndMins.add(time)) {
            if (!hoursAndMins.remove(time)) {
                throw new UndeclaredThrowableException(new Throwable("byaTime"));
            }
            return true;
        }
        return false;
    }

    public String toJson() {
        Gson gson = M.getGson();
        return gson.toJson(this);
    }

    public static Schedule fromJson(String jSchedule) {
        Gson gson = M.getGson();
        return gson.fromJson(jSchedule, Schedule.class);
    }

    public boolean isNull() {
        return days.isEmpty() || hoursAndMins.isEmpty();
    }

    @NonNull
    @Override
    public Schedule clone() {
        TreeSet<Integer> days = new TreeSet<>();
        TreeSet<TimePair> hoursAndMins = new TreeSet<>();

        for (int day : this.days) {
            days.add((int) day);
        }
        for (TimePair time : this.hoursAndMins) {
            hoursAndMins.add(new TimePair((int) time.first, (int) time.first));
        }

        return new Schedule(days, hoursAndMins);
    }

    @NonNull
    @Override
    public String toString() {
        return toJson();
    }

    public long dayToMilis(long day) {
        return day*24*60*60*1000;
    }

    public long hourToMilis(long hour) {
        return hour*60*60*1000;
    }

    public long minToMilis(long min) {
        return min*60*1000;
    }

    public long secToMilis(long sec) {
        return sec*1000;
    }

}
