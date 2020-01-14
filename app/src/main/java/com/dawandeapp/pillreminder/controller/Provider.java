package com.dawandeapp.pillreminder.controller;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.dawandeapp.pillreminder.GlobalConst;
import com.dawandeapp.pillreminder.model.Ble;
import com.dawandeapp.pillreminder.model.BleJsonMessage;
import com.dawandeapp.pillreminder.model.BleTransaction;
import com.dawandeapp.pillreminder.model.Clock;
import com.dawandeapp.pillreminder.model.Pill;
import com.dawandeapp.pillreminder.model.PillBluetooth;
import com.dawandeapp.pillreminder.model.PillDAO;
import com.dawandeapp.pillreminder.model.PillDatabase;
import com.dawandeapp.pillreminder.ui.main.MainActivity;
import com.dawandeapp.pillreminder.ui.shared.home.HomeFragment;
import com.dawandeapp.pillreminder.ui.shared.pills.PillsFragment;
import com.dawandeapp.pillreminder.ui.shared.preferences.PreferencesFragment;
import com.dawandeapp.pillreminder.utilities.M;

import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import static com.dawandeapp.pillreminder.model.BleTransaction.ACTION_DATA_AVAILABLE;
import static com.dawandeapp.pillreminder.model.BleTransaction.ACTION_GATT_CONNECTED;

public class Provider {
    private Context appContext = null;

    private static Provider instance;
    private PillDAO blPills, dbPill;
    private Ble ble;
    private BluetoothGattCallback gattCallback;
    private WeakReference<MainActivity> mainActivityWr;
    private WeakReference<HomeFragment> homeFragmentWr;
    private WeakReference<PillsFragment> pillsFragmentWr;
    private WeakReference<PreferencesFragment> preferencesFragmentWr;

    public static Provider getInstance(Context context, Bundle bundle) {
        if (instance == null) {
            synchronized(Provider.class) {
                if (instance == null) {
                    instance = new Provider();
                    M.d("NewProvider");
                }
            }
        }
        if (context != null) {
            instance.appContext = context;
        }
        if (instance.blPills == null) {
            instance.blPills = PillBluetooth.getInstance();
        }
        if (instance.dbPill == null) {
            instance.dbPill = PillDatabase.getInstance(instance.appContext);
        }
        if (instance.ble == null) {
            instance.ble = PillBluetooth.getInstance();
        }
        if (bundle != null) {
            instance.restoreFromBundle(bundle);
        }
        instance.bleState = instance.verifyBleBtnState();
        return instance;

    }

    private Provider() {

    }

    private void  restoreFromBundle(Bundle bundle) {
    }

    public void setMainView(MainActivity mainActivity) {
        mainActivityWr = new WeakReference<>(mainActivity);
    }

    public void setHomeFragment(HomeFragment homeFragment) {
        homeFragmentWr = new WeakReference<>(homeFragment);
    }
    public void setPillsFragment(PillsFragment pillsFragment) {
        pillsFragmentWr = new WeakReference<>(pillsFragment);
    }
    public void setPreferencesFragment(PreferencesFragment preferencesFragment) {
        preferencesFragmentWr = new WeakReference<>(preferencesFragment);
    }

    // MAIN CODE

    //PILL_CODE //////////////////////////////////////////////////////////////////////////////

    private String MAC;  // MAC that is chosen to connect to


    public LiveData<List<Pill>> getPills() {
        return dbPill.getPills();
    }

    public void insertPill(Pill pill) {
        dbPill.insertPill(pill);
    }

    public void deletePill(Pill pill) {
        dbPill.deletePill(pill);
    }

    public void updatePill(Pill pill) {
        dbPill.updatePill(pill);
    }


    //BLE_CODE  ///////////////////////////////////////////////////////////////////////////////

    public boolean bluetoothIsEnabled() {
        return ble.isEnabled();
    }

    public boolean bleIsConnected() {
        return ble.isConnected();
    }

    public LiveData<List<BluetoothDevice>> getFoundDevices() {
        return ble.getFoundDevices();
    }

    public boolean isBraceletDevice(String MAC) {
        BluetoothDevice device = null;
        for (BluetoothDevice d : getFoundDevices().getValue()) {
            if (d.getAddress().equals(MAC)) {
                device = d;
                break;
            }
        }

        M.d(String.format("Chosen device is %s * %s * %s",
                device.getName(),
                device.getAddress(),
                device.getUuids() != null ? device.getUuids().length : -1));

        if (device.getName() != null && device.getName().contains(GlobalConst.BRACELET_NAME)) {
            M.d(String.format("Device %s is one of Dolsona's Bracelets", device.getName()));
            //stopSearchingDevices(true);  // Это должно происходить не здесь
            return true;
        }
        return false;
    }



    public void onSyncImb() {
        M.d("State sync is : " + bleState);
        if (bleState == STATE_CONNECTED) {
            Calendar c = Calendar.getInstance();
            Clock clock = new Clock(
                    c.get(Calendar.SECOND)+5,
                    c.get(Calendar.MINUTE),
                    c.get(Calendar.HOUR_OF_DAY),
                    rusDow(c.get(Calendar.DAY_OF_WEEK)),
                    c.get(Calendar.DAY_OF_MONTH),
                    c.get(Calendar.MONTH) + 1,
                    c.get(Calendar.YEAR) % 100);
            ble.sync(clock, getPills().getValue());
        } else M.d("No way to sync 'cause there is no connection");
    }

    private int rusDow(int amDow) {
        switch (amDow) {
            case Calendar.MONDAY: return 1;
            case Calendar.TUESDAY: return 2;
            case Calendar.WEDNESDAY: return 3;
            case Calendar.THURSDAY: return 4;
            case Calendar.FRIDAY: return 5;
            case Calendar.SATURDAY: return 6;
            case Calendar.SUNDAY: return 7;
            default:
                M.d("WARNING! WRONG DoW! 0 was passed to sync!");
                return 0;
        }
    }

    private BluetoothGattCallback getGattCallback() {
        if (gattCallback == null) {
            gattCallback = new BluetoothGattCallback() {
                @Override
                public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                    super.onConnectionStateChange(gatt, status, newState);
                    M.d("GattCallback: onConnectionStateChange: newState=" + newState);
                    if (mainActivityWr.get() != null) {
                        if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                            bleState = STATE_ON;
                            PillBluetooth.isConnected = false;
                            mainActivityWr.get().setBleDisconnected();
                        } else if (newState == BluetoothProfile.STATE_CONNECTED) {
                            bleState = STATE_CONNECTED;
                            PillBluetooth.isConnected = true;
                            mainActivityWr.get().setBleConnected();
                            PillBluetooth.getInstance().transaction.gatt.discoverServices();    // Immediate Services discovering after connecting
                        }
                    }
                }

                @Override
                public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                    super.onServicesDiscovered(gatt, status);
                    M.d("GattCallback: onServicesDiscovered: status" + status);
                    PillBluetooth.getInstance().readService();
//                    for (BluetoothGattService s: gatt.getServices()) {
//                        try {
//                            String q1 = s.getUuid().toString();
//
//                            String q2 = String.valueOf(s.getCharacteristics().size());
//                            M.d(String.format("Service %s with %s cahrs", q1, q2));
//                            try {
//                            for (BluetoothGattCharacteristic ch : s.getCharacteristics()) {
//                                String w1 = ch.getUuid().toString();
//                                String w2 = ch.getStringValue(0);
//                                M.d("----Char: " + w1 + " =" + w2);
//                                if (ch.getUuid().equals(UUID.fromString("d769facf-a4da-47ba-9253-65359ee480fb"))) {
//                                    gatt.readCharacteristic(ch);
//                                }
//                            }
//                            } catch (Exception exx) {
//                                M.d("ExChar: " + exx.getMessage());
//                                exx.printStackTrace();
//                            }
//                        } catch (Exception ex) {
//                            M.d("ExService: " + ex.getMessage());
//                            ex.printStackTrace();
//                        }
//                    }
                }

                @Override
                public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                    super.onCharacteristicRead(gatt, characteristic, status);
                    M.d("GattCallback: onCharacteristicRead: status" + status +
                            " UUID " + characteristic.getUuid() +
                            ", charact: " + characteristic.getStringValue(0));
                    Intent intent = new Intent(ACTION_DATA_AVAILABLE);

                    LocalBroadcastManager.getInstance(mainActivityWr.get()).sendBroadcast(intent);
                }

                @Override
                public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                    super.onCharacteristicWrite(gatt, characteristic, status);
                    M.d("GattCallback: onCharacteristicWrite: status" + status + ", charact: " + characteristic.getStringValue(0));
                    Intent intent = new Intent(ACTION_DATA_AVAILABLE);

                    //LocalBroadcastManager.getInstance(mainActivityWr.get()).sendBroadcast(intent);

                }

                @Override
                public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                    super.onCharacteristicChanged(gatt, characteristic);
                    M.d("GattCallback: onCharacteristicChanged charact: " + characteristic.getStringValue(0));
                    Intent intent = new Intent(ACTION_DATA_AVAILABLE);

                    LocalBroadcastManager.getInstance(mainActivityWr.get()).sendBroadcast(intent);
                }
            };
        }
        return gattCallback;
    }

    // Переделать нормально (Убрать из PillRow возожность образаться к pill напрямую, Передавать сюда только индекс...)
    public void onPillRightSwipe(Pill pill) {
        if (pill != null) {
            // Получение характеристики нужной
            BluetoothGatt gatt = PillBluetooth.getInstance().transaction.gatt;
            BluetoothGattCharacteristic ch = gatt
                    .getService(UUID.fromString(BleTransaction.SERVICE_UUID))
                    .getCharacteristic(UUID.fromString(BleTransaction.RX_CHARACTERISTIC_UUID));

            // Подготовка сообщения к отправке
            BleJsonMessage msg = new BleJsonMessage(BleJsonMessage.PILL, pill);
            String jsonMsg = msg.toString();
            M.d("result json object");
            M.d(jsonMsg);

            // Отправка на браслет
            ch.setValue(jsonMsg);
            M.d("Sending to bracelet");
            ch.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
            gatt.writeCharacteristic(ch);
            M.d("Sent ch to ble");
        } else M.d("pill is null. Transaction failure!");
    }

    // BleImbHandler

    public static final int STATE_ON = 1;
    public static final int STATE_OFF = 0;
    public static final int STATE_SEARCHING = 2;
    public static final int STATE_CONNECTED = 3;
    public static final int STATE_SEARCHING_FINISHED = 4;
    public static final int STATE_DISCONNECTED = 5;

    private int bleState = STATE_OFF;

    public void onBleStateChanged(int newState) {
        M.d("State : " + newState);
        switch (newState) {
            case STATE_ON:
                if (ble.isEnabled() && !ble.isConnected()) {
                    mainActivityWr.get().setBleDisconnected();
                    ble.disconnect();
                    break;
                }
                return;
            case STATE_OFF:
                if (!ble.isEnabled()) {
                    mainActivityWr.get().setBleDisabled();
                    ble.disconnect();
                    break;
                }
                return;
            case STATE_CONNECTED:
                if (ble.isConnected()) {
                    mainActivityWr.get().setBleConnected(); break;
                }
                return;
            case STATE_SEARCHING:
                if (bleState == STATE_ON) {
                    mainActivityWr.get().setBleSearching(); break;
                }
                return;
            case STATE_SEARCHING_FINISHED:
                if (ble.isConnected()) {
                    onBleStateChanged(STATE_CONNECTED);
                } else {
                    if (ble.isEnabled()) {
                        onBleStateChanged(STATE_OFF);
                    } else {
                        onBleStateChanged(STATE_ON);
                    }
                }
                if (bleState != STATE_CONNECTED) {
                    mainActivityWr.get().setBleDisconnected();
                    onBleStateChanged(STATE_ON);                // Connection failed
                }
                return;
            case STATE_DISCONNECTED:
                if (!ble.isEnabled()) {
                    onBleStateChanged(STATE_OFF);
                } else {
                    onBleStateChanged(STATE_ON);
                }
            default:
                M.d("Unexpected new bleState! onBleStateChanged(" + bleState + ") is wrong!!!");
                return;
        }
        M.d("New BLE state: " + newState);
        bleState = newState;
    }

    public void onBleImbClick() {
        M.d("Ble btn Click. State: " + bleState);
        switch (bleState) {
            case STATE_ON:
                M.d("state on");
                startSearching(); break;
            case STATE_OFF:
                M.d("STATE_OFF");
                requestToOnBluetooth(); break;
            case STATE_CONNECTED:
                M.d("STATE_CONNECTED");
                requestToDisconnect(); break;
            case STATE_SEARCHING:
            default:
                M.d("Unexpected behaviour! Click form bleState " + bleState + " is Impossible!!!");
        }
    }

    private int verifyBleBtnState() {
        if (bleIsConnected()) {
            M.d("Connnectedddddddddddd");
            return STATE_CONNECTED;
        } else if (bluetoothIsEnabled()) {
            M.d("Onnnnnnnnn");
            return STATE_ON;
        } else {
            M.d("Offfffffffffffffffff");
            return STATE_OFF;
        }
//        if (ble.isEnabled()) {
//            if (ContextCompat.checkSelfPermission(appContext, Manifest.permission.ACCESS_COARSE_LOCATION) >= 0) {
//
//                return bleState == STATE_OFF ? STATE_ON : bleState;
//            } else {
//                try {
//                    mainActivityWr.get().requestPermissions(new String[] {Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
//                } catch (NullPointerException ex) {
//                    M.d("Null activity");
//                }
//                M.d("Scanning was not invoked. Permission ACCESS_COARSE_LOCATION is -1");
//            }
//        }
    }

    private void requestToDisconnect() {
        // Добавить диалог для подтвержения дисконнекта
        ble.disconnect();
        onBleStateChanged(STATE_ON);
    }

    private void requestToOnBluetooth() {
        mainActivityWr.get().askToOnBluetooth();
    }

    private void startSearching() {
        mainActivityWr.get().showBluetoothSearchDialog();
        M.d("Scanning started");
    }

    public  void startSearchingDevices() {

        ble.startSearchingDevices();
    }


    public void dialogIsDismissed() {
        ble.stopSearchingDevices();

        onBleStateChanged(STATE_SEARCHING_FINISHED);
    }

    public void deviceWasChosen(String MAC) {
        if (isBraceletDevice(MAC)) {
            this.MAC = MAC;
            mainActivityWr.get().dismissSearchDevicesDialog();

            List<BluetoothDevice> list = ble.getFoundDevices().getValue();

            if (list != null) {
                for (BluetoothDevice device : list) {
                    if (device.getAddress().equals(MAC)) {
                        ble.connect(device.connectGatt(appContext, false, getGattCallback()));
                        M.d("sync init in provider");
                    }
                }
            }
        }
        M.d(MAC);
    }
}
