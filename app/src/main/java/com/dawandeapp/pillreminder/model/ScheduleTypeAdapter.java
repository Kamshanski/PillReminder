package com.dawandeapp.pillreminder.model;

import com.dawandeapp.pillreminder.utilities.TimePair;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class ScheduleTypeAdapter implements JsonDeserializer<Schedule>, JsonSerializer<Schedule> {
    static final String DAYS = "days";
    static final String TIMES = "times";

    @Override
    public Schedule deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Schedule schedule = new Schedule();

        JsonObject obj = json.getAsJsonObject();
        JsonArray daysArray = obj.get(DAYS).getAsJsonArray();
        JsonArray timesArray = obj.get(TIMES).getAsJsonArray();

        for (JsonElement el: daysArray) {
            schedule.addDay(el.getAsInt());
        }

        for (JsonElement el: timesArray) {
            JsonArray pair = el.getAsJsonArray();
            TimePair time = new TimePair(pair.get(0).getAsInt(), pair.get(1).getAsInt());
            schedule.addTime(time);
        }

        return schedule;
    }


    @Override
    public JsonElement serialize(Schedule src, Type typeOfSrc, JsonSerializationContext context) {
        //M.d("I'm used to create Json of schedule");
        JsonObject obj = new JsonObject();
        JsonArray daysArray = new JsonArray();
        JsonArray timesArrays = new JsonArray();

        for (Integer integer : src.getDays()) {
            daysArray.add(integer);
        }

        for (TimePair time : src.getHoursAndMins()) {
            JsonArray pair = new JsonArray(2);
            pair.add(time.first);
            pair.add(time.second);
            timesArrays.add(pair);
        }

        obj.add(DAYS, daysArray);
        obj.add(TIMES, timesArrays);

        return obj;
    }
}

