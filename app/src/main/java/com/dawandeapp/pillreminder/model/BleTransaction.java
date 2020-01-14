package com.dawandeapp.pillreminder.model;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.os.Handler;
import android.os.HandlerThread;

import com.dawandeapp.pillreminder.utilities.M;

import java.util.UUID;

public class BleTransaction {
    public final static String ACTION_GATT_CONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE =
            "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA =
            "com.example.bluetooth.le.EXTRA_DATA";


    public static final String SERVICE_UUID = "C6FBDD3C-7123-4C9E-86AB-005F1A7EDA01";
    public static final String TX_CHARACTERISTIC_UUID = "D769FACF-A4DA-47BA-9253-65359EE480FB";  //potatka
    public static final String RX_CHARACTERISTIC_UUID = "B88E098B-E464-4B54-B827-79EB2B150A9F";

    public boolean isProcessing = false;
    private BleThread bleThread;
    public BluetoothGatt gatt;

    public BleTransaction() {
    }

    void connect(final BluetoothGatt gatt) {
        if (!isProcessing) {
            isProcessing = true;
            M.d("Start sync");
            this.gatt = gatt;
            bleThread = new BleThread("BleTransactionThread");
            bleThread.actWhenStart(() -> {
                BleTransaction.this.gatt.discoverServices();
                M.d("discover Services initiation");
            });
            bleThread.start();
        }
    }


    public void readService() {
        bleThread.handler.post(new Runnable() {
            @Override
            public void run() {
                M.d("Read char.");
                BluetoothGattService service = gatt.getService(UUID.fromString(SERVICE_UUID));
                BluetoothGattCharacteristic ch = service.getCharacteristic(UUID.fromString(TX_CHARACTERISTIC_UUID));

                M.d("CHARACTERISTIC **************** : "+ch.getStringValue(0));
            }
        });
    }

    public void disconnect() {
        if (isProcessing) {
            gatt.disconnect();
            bleThread.quitSafely();

            isProcessing = false;
            gatt = null;
            bleThread = null;
        }
    }

    public void postToBracelet(String msg) {
        bleThread.postTask(() -> {
            BluetoothGattCharacteristic ch = gatt
                    .getService(UUID.fromString(BleTransaction.SERVICE_UUID))
                    .getCharacteristic(UUID.fromString(BleTransaction.RX_CHARACTERISTIC_UUID));

            M.d("result json object");
            M.d(msg);

            // Отправка на браслет
            ch.setValue(msg);
            M.d("Sending to bracelet");
            ch.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
            gatt.writeCharacteristic(ch);
            M.d("Sent ch to ble");
        });
    }

    static class BleThread extends HandlerThread {

        Handler handler;
        Runnable runWhenStart;

        public BleThread(String name) {
            super(name);
        }

        /**
         * This method takes a task to run it in a new thread.
         * @param task task to be run
         */
        public void postTask(Runnable task){
            handler.post(task);
        }

        public void actWhenStart(Runnable task) {
            runWhenStart = task;
        }

        /**
         * This method is needed to run Tasks in a separate thread.
         */
        @Override
        protected void onLooperPrepared() {
            super.onLooperPrepared();
            handler = new Handler(getLooper());
            handler.post(runWhenStart);
            runWhenStart = null;
            M.d("HandlerIsCreated");
        }
    }
}
