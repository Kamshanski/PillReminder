package com.dawandeapp.pillreminder.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.dawandeapp.pillreminder.utilities.M;

import java.util.ArrayList;
import java.util.List;

public class PillDatabase implements PillDAO {

    private static volatile PillDatabase instance = null;
    private static volatile SQLiteDB sql = null;

    private MutableLiveData<List<Pill>> pills = new MutableLiveData<>();
    private MutableLiveData<List<Schedule>> schedules = new MutableLiveData<>();

    private List<Pill> pillList = new ArrayList<>();

    private PillDatabase(Context context) {
        sql = new SQLiteDB(context);
    }

    public static PillDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized(PillDatabase.class) {
                if (instance == null) {
                    instance = new PillDatabase(context);
                }
            }
        }
        return instance;
    }

    @Override
    public LiveData<List<Pill>> getPills() {
        if (pills == null || pillList == null || pillList.size() == 0) {
            pillList = sql.getPills();
            pills.setValue(pillList);
//        AsyncTask.execute(() -> {
//
//            M.d("Async SQL getPills execute");
//        });
            //M.d("SQL getPills execute");
        }
        return pills;
    }

    @Override
    public void insertPill(Pill pill) {
        int result = sql.insertPill(pill);
        if (result != SUCCESS) {
            M.d(String.format("Error Async SQL insertPill %d.", result));
            return ;
        }
        pillList.set(pill.getId(), pill);
        pills.setValue(pillList);
        M.d("Async SQL insertPill execute");
    }

    @Override
    public void updatePill(Pill pill) {
        int result = sql.insertPill(pill);
        if (result != SUCCESS) {
            M.d(String.format("Error Async SQL updatePill %d.", result));
            return ;
        }
        pillList.set(pill.getId(), pill);
        pills.setValue(pillList);
        M.d("Async SQL updatePill execute");
    }

    @Override
    public void deletePill(Pill pill) {
        Pill nullPill = Pill.getNull(pill.getId());
        int result = sql.insertPill(nullPill);
        if (result != SUCCESS) {
            M.d(String.format("Error Async SQL deletePill %d.", result));
            return ;
        }
        pillList.set(nullPill.getId(), nullPill);
        pills.setValue(pillList);
        M.d("Async SQL deletePill execute");
    }


    public static class SQLiteDB extends SQLiteOpenHelper {

        public static final int VERSION = 14;
        public static final String DATABASE_NAME = "pills.db";
        public static final String PILL_TABLE = "pill";
        public static final String KEY_ID = "_id";
        public static final String KEY_NAME = "name";
        public static final String KEY_SCHEDULE = "schedule";
        public static final String KEY_IMPORTANCE = "importance";
        public static final String KEY_LAST_TIME = "lastTime";
        public static final String KEY_TIMES = "times";
        public static final String[] COLUMNS = {KEY_ID, KEY_NAME, KEY_SCHEDULE, KEY_IMPORTANCE, KEY_LAST_TIME, KEY_TIMES};


        private SQLiteDB(@Nullable Context context) {
            this(context, DATABASE_NAME, null, VERSION);
        }

        private SQLiteDB(@Nullable Context context, @Nullable String name, @Nullable android.database.sqlite.SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(android.database.sqlite.SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + PILL_TABLE + " ("
                    + KEY_ID + " INTEGER PRIMARY KEY, "
                    + KEY_NAME + " TEXT, "
                    + KEY_SCHEDULE + " TEXT, "
                    + KEY_IMPORTANCE + " TEXT, "
                    + KEY_LAST_TIME + " TEXT, "
                    + KEY_TIMES + " INTEGER)");

            //Запись начальных пустых таблеток
            for (int i = 0; i < 4; i++) {
                ContentValues pillValues = getPillValues(Pill.getNull(i));
                db.insert(PILL_TABLE,
                        null,
                        pillValues);
            }
            M.d("DB Created");
        }

        @Override
        public void onUpgrade(android.database.sqlite.SQLiteDatabase db, int oldVersion, int newVersion) {
            M.d(String.format("Time to UPGRADE form %d to %d !!!!!", oldVersion, newVersion));
            db.execSQL("DROP TABLE IF EXISTS " + PILL_TABLE);
            this.onCreate(db);
        }

        public List<Pill> getPills() {
            SQLiteDatabase db = getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM " + PILL_TABLE
                            + " WHERE " + KEY_ID + " >= 0 AND " + KEY_ID + " < 4"
                            + " ORDER BY " + KEY_ID + " ASC",
                    null);

            Pill.PillBuilder pillBuilder = new Pill.PillBuilder();
            List<Pill> pills = new ArrayList<>(4);
            if (cursor.moveToFirst()) {
                do {
                    Pill newPill = pillBuilder.setID(cursor.getInt(0))
                            .setName(cursor.getString(1))
                            .setSchedule(Schedule.fromJson(cursor.getString(2)))
                            .setImportance(cursor.getString(3))
                            .setLastTime(cursor.getLong(4))
                            .setTimes(cursor.getInt(5))
                            .build();

                    pills.add(newPill);

                } while (cursor.moveToNext());
            }
            cursor.close();
            db.close();
            return pills;

//            try (){
//
//            } catch (Exception ex) {
//                M.d("DB get Crash: " + ex.getMessage());
//                return new ArrayList<Pill>(4);
//            }
        }

        public int insertPill(Pill pill) {
            //M.d(String.format("pill id is: %d", pill.getId()));
            //M.d("0 <= pill.getId() = " + (0 <= pill.getId()));
            //M.d("pill.getId() < 4 = " + (pill.getId() < 4));
            if (0 <= pill.getId() && pill.getId() < 4) {
                try (SQLiteDatabase db = getWritableDatabase()) {
                    ContentValues values = getPillValues(pill);
                    //M.d("Update Pill " + pill.getName());
                    if (db.replace(PILL_TABLE, null, values) == -1) {
                        return PillDAO.ERROR_INSERT;
                    }
                } catch (Exception ex) {
                    M.d("DB update Crash: " + ex.getMessage());
                    return PillDAO.ERROR_INSERT;
                }
                return PillDAO.SUCCESS;
            } else {
                return PillDAO.ERROR_ID_IS_NOT_VALID;
            }
        }

        private static ContentValues getPillValues(Pill pill) {
            ContentValues values = new ContentValues();
            values.put(KEY_ID, pill.getId());
            values.put(KEY_NAME, pill.getName());
            values.put(KEY_SCHEDULE, pill.getSchedule().toJson());
            values.put(KEY_IMPORTANCE, pill.getImportance());
            values.put(KEY_LAST_TIME, pill.getLastTime());
            values.put(KEY_TIMES, pill.getTimes());
            return values;
        }
    }
}

