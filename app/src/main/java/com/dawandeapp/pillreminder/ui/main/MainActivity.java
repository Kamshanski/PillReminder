package com.dawandeapp.pillreminder.ui.main;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.widget.ImageButton;

import com.dawandeapp.pillreminder.controller.Provider;
import com.dawandeapp.pillreminder.R;
import com.dawandeapp.pillreminder.model.BleTransaction;
import com.dawandeapp.pillreminder.ui.myviews.BaseDialog;
import com.dawandeapp.pillreminder.ui.shared.SharedFragmentFactory;
import com.dawandeapp.pillreminder.utilities.M;
import com.google.android.material.tabs.TabLayout;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Provider provider;
    private FragmentManager fM;
    private int CONTAINER_ID;
    private static int TAB_LAYOUT_POSITION = 1;
    private SharedFragmentFactory fragmentFactory;
    private TabLayout.OnTabSelectedListener tabSelectedListener;
    private BaseDialog dialog;

    private final BroadcastReceiver mBleAdapterActionsHandler = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            M.d("Broadcast");

            if (action != null && action.equals(BluetoothAdapter.ACTION_STATE_CHANGED))
            {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        provider.onBleStateChanged(Provider.STATE_OFF);
                        break;
                    case BluetoothAdapter.STATE_ON:
                        M.d("State on must be");
                        provider.onBleStateChanged(Provider.STATE_ON);
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                    case BluetoothAdapter.STATE_TURNING_ON:
                        //
                        break;
                    default:
                        M.d("mBleAdapterActionsHandler ERROR");
                }
            } if (action != null && action.equals(BluetoothAdapter.ACTION_REQUEST_ENABLE)) {

            }
            else if (action != null) {
                switch (action) {
                    case BleTransaction.ACTION_DATA_AVAILABLE:
                        M.d("///BleTransaction.ACTION_DATA_AVAILABLE was received in BroadcastReceiver");
                        break;
                    case BleTransaction.ACTION_GATT_CONNECTED:
                        M.d("///BleTransaction.ACTION_GATT_CONNECTED was received in BroadcastReceiver");
                        provider.onBleStateChanged(Provider.STATE_CONNECTED);
                        break;
                    case BleTransaction.ACTION_GATT_DISCONNECTED:
                        M.d("///BleTransaction.ACTION_GATT_DISCONNECTED was received in BroadcastReceiver");
                        provider.onBleStateChanged(Provider.STATE_DISCONNECTED);
                        break;
                    case BleTransaction.ACTION_GATT_SERVICES_DISCOVERED:
                        M.d("///BleTransaction.ACTION_GATT_SERVICES_DISCOVERED was received in BroadcastReceiver");
                        break;
                    default:
                        M.d("///mBleAdapterActionsHandler Unknown action");
                }
            }
        }
    };

    TabLayout tabLayout;
    ImageButton imb_bluetooth, imb_sync;

    int MY_PERMISSIONS_REQUEST_READ_CONTACTS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        provider = Provider.getInstance(getApplicationContext(), null);
        provider.setMainView(this);

        // Registration of BroadcastReceiver locally
        LocalBroadcastManager.getInstance(this).registerReceiver(mBleAdapterActionsHandler,
                getBleIntentFilter());

        checkFileSystem();

        CONTAINER_ID = R.id.frameLayout;
        fM = getSupportFragmentManager();
        fragmentFactory = new SharedFragmentFactory();

        findViews();
        initViewControl();
        initUI();

        fM.beginTransaction()
                .replace(CONTAINER_ID, fragmentFactory.get(TAB_LAYOUT_POSITION, null))
                .commit();
    }


    private void findViews() {
        tabLayout = findViewById(R.id.tabLayout);
        imb_bluetooth = findViewById(R.id.imb_bluetooth);
        imb_sync = findViewById(R.id.imb_sync);
    }

    private void initViewControl() {

        //Просто проверка (ДелитМи)
//        provider.getPills().observe(this, new Observer<List<Pill>>() { //Просто проверка 9ДелитМи)
//            @Override //Просто проверка 9ДелитМи)
//            public void onChanged(List<Pill> pills) { //Просто проверка 9ДелитМи)
//                M.d("PillChanged"); //Просто проверка 9ДелитМи)
//                //Просто проверка 9ДелитМи)
//            }//Просто проверка 9ДелитМи)
//        });//Просто проверка (ДелитМи)

//        imb_sync.setOnClickListener(v -> {
//            if (provider.bluetoothIsEnabled()) {
//
//
//                PillBluetooth BL = PillBluetooth.getInstance();
//                M.d("Chosen MAC: provider.chosenMAC");
//                BL.setTransaction(new BleTransaction(BL.searcher.getDevice(provider.chosenMAC)));
//                BL.connectGatt(this);
//                BL.read(BleTransaction.READ_POTATO, new BleTransaction.AfterReadCallback<String>() {
//                    @Override
//                    public void execute(String... args) {
//                        if (args != null) {
//                            M.d("CALLBACK EXECUTE "+args.length+"_____________________+++++++++++++++&&&&&&&&&&&&&&&&&&&&&&&&&&&&:"
//                                    +args[0]);
//                        } else {
//                            M.d("CALLBACK EXECUTE NULL_____________________+++++++++++++++&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
//                        }
//                    }
//                });
//
//
////                BleTransaction.GattConnector connector = new BleTransaction.GattConnector() {
////                    @Override
////                    public BluetoothGatt connect(BluetoothDevice device, boolean autoConnect, BluetoothGattCallback gattCallback) {
////                        return device.connectGatt(MainActivity.this, autoConnect, gattCallback);
////                    }
////                };
////                provider.syncData(connector, notifier, callback);
//                //TODO добавить закрытие и прочее в onDestroy...
//            }
//        });
        imb_sync.setOnClickListener(v -> {
            provider.onSyncImb();
        });

        imb_bluetooth.setOnClickListener(v -> {
            provider.onBleImbClick(); // СДЕЛАТЬ В ПРОВАЙДЕРЕ BleButtonHandler, который будет следить за подключением и
            // выполнять действия внутри провайдера, а здесь только обращение к нему будет
        });

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                TAB_LAYOUT_POSITION = position;
                fM.beginTransaction()
                        .replace(CONTAINER_ID, fragmentFactory.get(TAB_LAYOUT_POSITION, null))
                        .commit();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    public void setBleConnected() {
        M.d("setConnected");
        imb_bluetooth.setImageDrawable(getResources().getDrawable(R.drawable.ble_connected, getTheme()));
    }
    public void setBleDisabled() {
        imb_bluetooth.setImageDrawable(getResources().getDrawable(R.drawable.ble_disable, getTheme()));
    }
    public void setBleDisconnected() {
        imb_bluetooth.setImageDrawable(getResources().getDrawable(R.drawable.ble_disconnected, getTheme()));
    }
    public void setBleSearching() {
        imb_bluetooth.setImageDrawable(getResources().getDrawable(R.drawable.ble_searching, getTheme()));
    }



    public void showBluetoothSearchDialog() {
        final BluetoothSearchDialog dialog = new BluetoothSearchDialog(this, provider);
        Observer searchDevicesObserver = new Observer<List<BluetoothDevice>>() {
            @Override
            public void onChanged(List<BluetoothDevice> bluetoothDevices) {
                if (bluetoothDevices.size() > 0) {
                    BluetoothDevice newDevice = bluetoothDevices.get(bluetoothDevices.size() - 1);
                    dialog.addDevice(newDevice.getName(),
                            newDevice.getAddress(),
                            newDevice.getUuids() != null ? newDevice.getUuids().length : -1);
                    M.d("New device added");
                } else M.d("Size of foundBluetoothDevices is 0, but Observer was invoked");
            }
        };

        dialog.setObserver(searchDevicesObserver);
        dialog.create();
        dialog.show();
        this.dialog = dialog;
        provider.startSearchingDevices();
        provider.onBleStateChanged(Provider.STATE_SEARCHING);
        provider.getFoundDevices().observe(this, searchDevicesObserver);
    }

    public void dismissSearchDevicesDialog() {
        dialog.dismiss();
    }

    public void askToOnBluetooth() {
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBtIntent, 0);
    }

    private void initUI() {

//        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
//                M.d(tab.getPosition());
//                int position = tab.getPosition();
//                TAB_LAYOUT_POSITION = position;
//                fM.beginTransaction()
//                        .replace(CONTAINER_ID, fragmentFactory.get(TAB_LAYOUT_POSITION))
//                        .commit();
//            }
//
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {
//
//            }
//
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {
//
//            }
//        });
        //M.d("listener Installed");
        if (provider.bleIsConnected()) {
            setBleConnected();
        } else if (provider.bluetoothIsEnabled()) {
            setBleDisconnected();
        } else {
            setBleDisabled();
        }

        //tabLayout.setScrollPosition(TAB_LAYOUT_POSITION, 0f, true); НЕ ИСПОЛЬЗОВАТЬ! МОЖЕТ НЕ РАБОТАТЬ В ПЕРВЫЙ КЛИК
        tabLayout.getTabAt(TAB_LAYOUT_POSITION).select();
    }

    private void checkFileSystem() {
        M.d(M.appPath().toString());
        try {
            if (Files.exists(M.appPath())) {
                M.d("Already Exists");
            } else {
                Files.createDirectory(M.appPath());
                M.d("Created");
            }
            M.d(M.appPath().toString());
        } catch (IOException ex) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_CONTACTS);
            M.d("Folder wasn't created");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        M.d("Resume MinActivity");
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        provider = Provider.getInstance(getApplicationContext(), savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {

        super.onSaveInstanceState(writeControllerState(outState));

    }

    private Bundle writeControllerState(Bundle state) {
        //TODO: сделать запись состояния контроллера
        return state;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        M.d("Unregister");
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBleAdapterActionsHandler);
    }

    private IntentFilter getBleIntentFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BleTransaction.ACTION_DATA_AVAILABLE);
        filter.addAction(BleTransaction.ACTION_GATT_CONNECTED);
        filter.addAction(BleTransaction.ACTION_GATT_DISCONNECTED);
        filter.addAction(BleTransaction.ACTION_GATT_SERVICES_DISCOVERED);
        return filter;
    }
}