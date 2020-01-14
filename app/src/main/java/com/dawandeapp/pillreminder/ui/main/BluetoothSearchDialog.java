package com.dawandeapp.pillreminder.ui.main;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.lifecycle.Observer;

import com.dawandeapp.pillreminder.R;
import com.dawandeapp.pillreminder.controller.Provider;
import com.dawandeapp.pillreminder.ui.myviews.BaseDialog;
import com.dawandeapp.pillreminder.utilities.M;

import java.util.List;

//Можно запихнуть в MainActivity - тогда проще с provider
public class BluetoothSearchDialog extends BaseDialog {

    LinearLayout LL_bluetooth_search;
    Provider provider;
    Observer<List<BluetoothDevice>> observer = null;

    public BluetoothSearchDialog(@NonNull AppCompatActivity activity, Provider provider) {
        super(activity);
        this.provider = provider;
    }

    @Override
    protected void initDialog() {
        setTitle("Выберите устройство Браслет");
        setContentView(R.layout.dialog_search_devices);
    }

    @Override
    protected void findViews() {
        LL_bluetooth_search = findViewById(R.id.LL_bluetooth_search);
    }

    @Override
    protected void setDefaultValues() {
    }

    @Override
    public void setListeners() {

    }

    @Override
    protected void preform() {
    }

    public void setObserver(Observer observer) {
        this.observer = observer;
    }

    public void addDevice(String name, String MAC, int UUIDsNumber) {
        M.d("add device view");
        BluetoothDeviceHolder view = new BluetoothDeviceHolder(getContext(), name, MAC, UUIDsNumber);
        view.setOnClickListener(v -> {
            BluetoothDeviceHolder holder = (BluetoothDeviceHolder) v;
            provider.deviceWasChosen(holder.getMAC());
        });
        LL_bluetooth_search.addView(view);

    }

    @Override
    public void dismiss() {
        provider.getFoundDevices().removeObserver(observer);
        provider.dialogIsDismissed();
        M.d("I (dialog) dismiss");
        super.dismiss();
    }

    private static class BluetoothDeviceHolder extends LinearLayoutCompat {
        String name, MAC;
        int UUIDsNumber;

        public BluetoothDeviceHolder(Context context, String name, String MAC, int UUIDsNumber) {
            this(context, null, name, MAC, UUIDsNumber);
            M.d("View Device create");
        }

        public BluetoothDeviceHolder(Context context, AttributeSet attrs, String name, String MAC, int UUIDsNumber) {
            this(context, attrs, 0, name, MAC, UUIDsNumber);
        }

        public BluetoothDeviceHolder(Context context, AttributeSet attrs, int defStyleAttr, String name, String MAC, int UUIDsNumber) {
            super(context, attrs, defStyleAttr);
            this.name = name;
            this.MAC = MAC;
            this.UUIDsNumber = UUIDsNumber;
            init();
        }

        private void init() {
            removeAllViews();

            View view = LayoutInflater.from(getContext()).inflate(R.layout.bluetooth_device, this, false);
            findViews(view);

            addView(view);
            M.d("Created and added");
        }

        private void findViews(View view) {
            TextView tx_device_name = view.findViewById(R.id.tx_device_name);
            TextView tx_device_MAC = view.findViewById(R.id.tx_device_MAC);
            TextView tx_device_UUIDs_Number = view.findViewById(R.id.tx_device_UUIDs_Number);

            //TODO: вынести отдельно
            tx_device_name.setText(name);
            tx_device_MAC.setText(MAC);
            tx_device_UUIDs_Number.setText(String.valueOf(UUIDsNumber));
        }

        public String getName() {
            return name;
        }

        public String getMAC() {
            return MAC;
        }

        public int getUUIDsNumber() {
            return UUIDsNumber;
        }
    }
}