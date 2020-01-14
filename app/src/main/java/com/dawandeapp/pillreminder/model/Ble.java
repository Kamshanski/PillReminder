package com.dawandeapp.pillreminder.model;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;

import androidx.lifecycle.LiveData;

import java.util.List;

public interface Ble {
    boolean isEnabled();
    boolean isConnected();
    LiveData<List<BluetoothDevice>> getFoundDevices();
    void startSearchingDevices();
    void stopSearchingDevices();

    void connect(BluetoothGatt gatt);
    void disconnect();
    void sync(Clock clock, List<Pill> pills);

}
