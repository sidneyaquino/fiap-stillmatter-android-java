package com.fiap.stillmatter.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Button;

import com.fiap.stillmatter.AppDefaults;
import com.fiap.stillmatter.R;
import com.fiap.stillmatter.adapter.DevicesAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.polidea.rxandroidble.RxBleClient;
import com.polidea.rxandroidble.RxBleDevice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.subscriptions.CompositeSubscription;

public class MainActivity extends AppCompatActivity {
    RxBleClient rxBleClient;
    @BindView(R.id.bt_scan) Button buttonScan;
    @BindView(R.id.rv_devices) RecyclerView rvDevices;
    CompositeSubscription subscription = new CompositeSubscription();
    Set<RxBleDevice> devices = new HashSet<>();
    HashMap<String, Integer> deviceRssi = new HashMap<>();
    DevicesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        rxBleClient = RxBleClient.create(this);
        setupRecyclerView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        subscription.clear();
    }

    @OnClick(R.id.bt_scan)
    void onButtonScanClick() {
        scan();
    }

    private void setupRecyclerView() {
        adapter = new DevicesAdapter(this, new ArrayList<>(devices), deviceRssi);
        rvDevices.setAdapter(adapter);
        rvDevices.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    }

    private void scan() {
        subscription.add(rxBleClient.scanBleDevices()
                .subscribe(
                        rxBleScanResult -> {
                            devices.add(rxBleScanResult.getBleDevice());
                            deviceRssi.put(rxBleScanResult.getBleDevice().getMacAddress(), rxBleScanResult.getRssi());
                            adapter.update(devices);
                            sendToFirebase(rxBleScanResult.getBleDevice(), rxBleScanResult.getRssi());
                        },
                        throwable -> {
                            // Handle an error here.
                            Log.d("BLE_DEVICE", throwable.getMessage());
                        }
                ));
    }

    private void sendToFirebase(RxBleDevice device, int rssi) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReferenceFromUrl(AppDefaults.URL_FIREBASE_BEACONS + device.getMacAddress());
        HashMap<Object, Object> data = new HashMap<>();
        data.put("mac_address", device.getMacAddress());
        data.put("name", device.getName());
        data.put("rssi", rssi);
        reference.setValue(data);
    }
}