package com.dawandeapp.pillreminder.model;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.dawandeapp.pillreminder.utilities.M;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.List;

public class PillBluetooth implements PillDAO, Ble {

    private static volatile PillBluetooth instance = null;
    public static boolean isConnected;

    public BleDeviceSearcher searcher;
    public BleTransaction transaction;

    BluetoothAdapter bluetoothAdapter;

    private PillBluetooth() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(bluetoothAdapter != null) {
            M.d("Ваше устройство оснащено Bluetooth");
            if (bluetoothAdapter.isEnabled()) {
                M.d("Bluetooth сейчас выключен");
            }
        } else M.d("У вас не Bluetooth. Купите нормальный телефон");
    }

    public static PillBluetooth getInstance() {
        if (instance == null) {
            synchronized(PillBluetooth.class) {
                if (instance == null) {
                    instance = new PillBluetooth();
                }
            }
        }
        return instance;
    }

    @Override
    public LiveData<List<Pill>> getPills() {
        return null;
    }

    @Override
    public void insertPill(Pill pill) {

        M.d("Bluetooth add pill to database and bracelet");
    }

    @Override
    public void updatePill(Pill pill) {
        M.d("Bluetooth update pill to database and bracelet");
    }

    @Override
    public void deletePill(Pill pill) {
        M.d("Bluetooth delete pill from database and bracelet");
    }

    @Override
    public boolean isEnabled() {
        return bluetoothAdapter.isEnabled();
    }

    @Override
    public boolean isConnected() {
        return isConnected;
    }

    @Override
    public LiveData<List<BluetoothDevice>> getFoundDevices() {
        if (searcher == null) {
            startSearchingDevices();
        }
        return searcher.getDevices();
        //Продолжить с переноса Сёрчера
    }

    @Override
    public void startSearchingDevices() {
        if (this.searcher == null) {
            this.searcher = new BleDeviceSearcher(bluetoothAdapter);
        } else {
            searcher.discard();
        }
        searcher.startSearchingDevices();
    }

    @Override
    public void stopSearchingDevices() {
        M.d("stopSearchingDevices");
        searcher.stopSearchingDevices();
    }

    @Override
    public void connect(BluetoothGatt gatt) {
        transaction = new BleTransaction();
        transaction.connect(gatt);
    }

    @Override
    public void disconnect() {
        try {
            transaction.disconnect();
            transaction.gatt.disconnect();
        }
        catch (NullPointerException ex) {
            M.d("Empty to be disconnected");
        }
        transaction = null;
    }

    public void readService() {
        transaction.readService();
    }

    @Override
    public void sync(Clock clock, List<Pill> pills) {
        BleJsonMessage[] messages = new BleJsonMessage[5];
        JsonArray arr = new JsonArray();

        messages[4] = new BleJsonMessage(BleJsonMessage.CLOCK, clock);
        for (int i = 0; i < 4; i++) {
            messages[i] = new BleJsonMessage(BleJsonMessage.PILL, pills.get(i));
        }
        String msg = "["+ messages[0] +","+
                messages[1]+","+
                messages[2]+","+
                messages[3]+","+
                messages[4]+"]";
        transaction.postToBracelet(msg);

//        for (BleJsonMessage msg : messages) {
//            transaction.postToBracelet(msg.toString());
//        }
    }
}
