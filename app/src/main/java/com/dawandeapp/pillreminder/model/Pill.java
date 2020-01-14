package com.dawandeapp.pillreminder.model;

import com.dawandeapp.pillreminder.utilities.M;
import com.google.gson.Gson;

public class Pill {
    public static final String IMPORTANT = "important";
    public static final String UNIMPORTANT = "unimportant";
    public static final String VITAL = "vital";
    public static final String DIETARY_SUPPLEMENTS = "dietary_supplements";

    //@PrimaryKey
    private int _id;
    private String name;
    private Schedule schedule;
    private String importance;
    private long lastTime;
    private int times;

    public String getImportance() {
        return importance;
    }

    public void setImportance(String importance) {
        this.importance = importance;
    }

    public Pill() {
    }

    public Pill(int _id, String name, Schedule schedule, String importance, long lastTime, int times) {
        this._id = _id;
        this.name = name;
        this.schedule = schedule;
        this.importance = importance;
        this.lastTime = lastTime;
        this.times = times;
    }

    public static Pill getNull(int i) {
        M.d("Null Schedule");
        return new Pill(i, "", new Schedule(), "", -1L, -1);

    }

    public boolean isNull() {
        return times < 0 || lastTime < 0 || importance.isEmpty() || schedule.isNull() || name.isEmpty() || _id < 0;
    }

    public String toJson() {
        Gson gson = M.getGson();
        return gson.toJson(this);
    }

    public static Pill fromJson(String jPill) {
        Gson gson = M.getGson();
        return gson.fromJson(jPill, Pill.class);
    }



    public int getId() {
        return _id;
    }

    public void setId(int _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

    public long getLastTime() {
        return lastTime;
    }

    public void setLastTime(long lastTime) {
        this.lastTime = lastTime;
    }

    public int getTimes() {
        return times;
    }

    public void setTimes(int times) {
        this.times = times;
    }

    public String getJson() { return M.getGson().toJson(this);}

    @Override
    public String toString() {
        return "Pill{" +
                "_id=" + _id +
                ", name='" + name + '\'' +
                ", schedule=" + schedule.toString() +
                ", importance='" + importance + '\'' +
                ", lastTime='" + lastTime + '\'' +
                ", times=" + times +
                '}';
    }

    public static class PillBuilder {
        private int _id = -1;
        private String name = "";

        private Schedule schedule = null;
        private String importance = "";
        private long lastTime = -1;
        private int times = -1;

        public PillBuilder() {
        }

        public Pill build() {
            return new Pill(_id, name, schedule, importance, lastTime, times);
        }

        public PillBuilder setID(int _id) {
            this._id = _id;
            return this;
        }

        public PillBuilder setName(String name) {
            this.name = name;
            return this;
        }

        public PillBuilder setSchedule(String jSchedule) {
            this.schedule = Schedule.fromJson(jSchedule);
            return this;
        }

        public PillBuilder setSchedule(Schedule schedule) {
            this.schedule = schedule;
            return this;
        }

        public PillBuilder setImportance(String importance) {
            this.importance = importance;
            return this;
        }

        public PillBuilder setLastTime(long lastTime) {
            this.lastTime = lastTime;
            return this;
        }

        public PillBuilder setTimes(int times) {
            this.times = times;
            return this;
        }
    }
}