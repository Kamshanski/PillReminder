package com.dawandeapp.pillreminder.model;

import com.dawandeapp.pillreminder.utilities.M;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class PillDeserializer implements JsonDeserializer<ArrayList> {

    List<Pill> pillList;

    public PillDeserializer(List<Pill> pillList) {
        this.pillList = pillList;
    }

    @Override
    public ArrayList deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
    {
        //Предсталяем json в виде объекта
        JsonArray jsonArray = json.getAsJsonArray();

        if (jsonArray.size() <= 0) {
            M.d("Array is null");
            for (int i = 0; i < 4; i++) {
                pillList.add(null);
            }
        } else {
            M.d("Array size is: " + String.valueOf(jsonArray.size()));
            jsonArray.forEach(jsonElement -> {
                JsonObject jsonObject = (JsonObject) jsonElement;
                int number = jsonObject.get("_id").getAsInt();
                M.d("Pill #".concat(String.valueOf(number)));

                if (0 <= number && number < 4) {
                    pillList.add(number, context.deserialize(jsonObject, Pill.class));
                    M.d(String.format("Pill %d was added", number));
                }
            });
        }
        return (ArrayList<Pill>) pillList;
    }
}