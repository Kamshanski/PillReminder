package com.dawandeapp.pillreminder.utilities;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.dawandeapp.pillreminder.model.Schedule;
import com.dawandeapp.pillreminder.model.ScheduleTypeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.nio.file.Path;
import java.nio.file.Paths;

public abstract class M {
    public static String appPathStr() {
        return Environment.getExternalStorageDirectory().getPath().concat("/PILLS");
    }
    public static Path appPath() {
        return Paths.get(Environment.getExternalStorageDirectory().getPath().concat("/PILLS"));
    }
    public static Path appPath(String path) {
        return Paths.get(Environment.getExternalStorageDirectory().getPath().concat("/PILLS").concat(path));
    }

    public static void d(String str) {
        Log.i("MyDebug", str);
    }
    public static void d(int i) {
        M.d(String.valueOf(i));
    }
    public static void d(long i) {
        M.d(String.valueOf(i));
    }
    public static void d(byte i) {
        M.d(String.valueOf(i));
    }
    public static void d(float i) {
        M.d(String.valueOf(i));
    }
    public static void d(boolean i) {
        M.d(String.valueOf(i));
    }
    public static void d(byte[] i) {
        StringBuilder sb = new StringBuilder();
        for (byte b: i) {
            sb.append(b);
            sb.append(", ");
        }
        M.d(sb.substring(0, sb.length() - 1));
    }

    public static void wSh(Context context, String str) {
        Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
    }
    public static void wL(Context context, String str) {
        Toast.makeText(context, str, Toast.LENGTH_LONG).show();
    }

    public static Gson getGson() {
        return new GsonBuilder()
                .registerTypeAdapter(Schedule.class, new ScheduleTypeAdapter())
                .create();
    }
}
