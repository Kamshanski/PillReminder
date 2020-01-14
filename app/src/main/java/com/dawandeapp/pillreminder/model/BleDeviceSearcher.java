package com.dawandeapp.pillreminder.model;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.dawandeapp.pillreminder.utilities.M;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class BleDeviceSearcher {

    public static final long SCAN_PERIOD = 15000L;
    private boolean isSearching = false;
    private Timer mTimer;
    private TimerTask mTimerTask;
    private final BluetoothAdapter mAdapter;
    private BluetoothLeScanner scanner;
    private ScanCallback scanCallback = null;
    private MutableLiveData<List<BluetoothDevice>> foundDevices;


    public BleDeviceSearcher(BluetoothAdapter adapter) {
        this.mAdapter = adapter;
        this.scanner = mAdapter.getBluetoothLeScanner();

        discard();
    }


    public void startSearchingDevices() {
        if (!isSearching) {
            startTimer();
            List<ScanFilter> noFilter = Collections.emptyList();
            ScanSettings scanSettings = new ScanSettings.Builder()
                    .setReportDelay(0)
                    .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                    .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
                    .build();
            scanner.startScan(noFilter, scanSettings, getScanCallback());
            M.d("Scanning started successfully");
            isSearching = true;
        }
    }

    public boolean stopSearchingDevices() {
        if (isSearching) {
            scanner.stopScan(scanCallback);
            scanCallback = null;
            isSearching = false;
        }
        return true;
    }

    public BluetoothDevice getDevice(String MAC) {
        if (foundDevices.getValue() != null) {
            M.d("foundDevices is NULL");
            return null;
        }
        if (foundDevices.getValue().size() < 1) {
            M.d("Number of found devices is 0");
            return null;
        }
        for (BluetoothDevice device : foundDevices.getValue()) {
            if (device.getAddress().equals(MAC)) {
                return device;
            }
        }
        M.d("No such device were found");
        return null;
    }

    public LiveData<List<BluetoothDevice>> getDevices() {
        return foundDevices;
    }

    // Сброс посиковика на самое начало
    public void discard() {
        totalStop();
        foundDevices = new MutableLiveData<>(new ArrayList<>());
        scanCallback = null;
        scanCallback = null;
    }

    public void totalStop() {
        stopTimer();
        stopSearchingDevices();
    }

    private ScanCallback getScanCallback() {
        if (scanCallback == null) {
            scanCallback = new ScanCallback() {
                @Override
                public void onScanResult(int callbackType, ScanResult result) {
                    super.onScanResult(callbackType, result);
                    BleDeviceSearcher.this.onScanResult(result);
                }

                @Override
                public void onBatchScanResults(List<ScanResult> results) {
                    super.onBatchScanResults(results);
                    M.d(String.format("Some batch results: (%d)", results.size()));
                }

                @Override
                public void onScanFailed(int errorCode) {
                    M.d("SCAN_FAILED: " + errorCode);
                    super.onScanFailed(errorCode);
                }
            };
        }
        return scanCallback;
    }

    private void onScanResult(ScanResult result) {
        BluetoothDevice foundDevice = result.getDevice();
        if (isNewDevice(foundDevice)) {
            List<BluetoothDevice> l = foundDevices.getValue();
            l.add(foundDevice);
            foundDevices.postValue(l);
        }
    }

    private boolean isNewDevice(BluetoothDevice newDevice) {
        for (BluetoothDevice device : foundDevices.getValue()) {
            if (newDevice.getAddress().equals(device.getAddress())) {
                return false;
            }
        }
        return true;
    }

    private void startTimer() {
        mTimer = new Timer();

        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                if (!isSearching) {
                    mTimerTask = null;
                    stopSearchingDevices();
                }
            }
        };
        mTimer.schedule(mTimerTask, SCAN_PERIOD);
    }

    private void stopTimer() {
        try {
            mTimerTask.cancel();
            mTimer.cancel();
            mTimer.purge();
        } catch (Exception e) {
            M.d("stopTimer Null Ex: "+e.getMessage());
        }
    }



}
