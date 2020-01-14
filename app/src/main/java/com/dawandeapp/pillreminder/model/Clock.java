package com.dawandeapp.pillreminder.model;

public class Clock {
    public final int sec, min, hour, dow, dom, month, year;

    public Clock(int seconds, int minutes, int hours, int dayOfWeek, int dayOfMonth, int month, int year) {
        this.sec = seconds;
        this.min = minutes;
        this.hour = hours;
        this.dow = dayOfWeek;
        this.dom = dayOfMonth;
        this.month = month;
        this.year = year;
    }
}
