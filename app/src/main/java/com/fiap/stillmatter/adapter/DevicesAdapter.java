package com.fiap.stillmatter.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fiap.stillmatter.R;
import com.polidea.rxandroidble.RxBleDevice;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by thaisandrade on 23/04/17.
 */

public class DevicesAdapter extends RecyclerView.Adapter<DevicesAdapter.DeviceViewHolder> {

    List<RxBleDevice> devices;
    Context context;
    HashMap<String, Integer> deviceRssi = new HashMap<>();

    public DevicesAdapter(Context context, List<RxBleDevice> devices, HashMap<String, Integer> rssi) {
        this.context = context;
        this.devices = devices;
        this.deviceRssi = rssi;
    }

    public void update(Set<RxBleDevice> setDevices) {
        devices.clear();
        devices.addAll(setDevices);

        notifyDataSetChanged();
    }

    @Override
    public DeviceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_device, parent, false);
        return new DeviceViewHolder(v);
    }

    @Override
    public void onBindViewHolder(DeviceViewHolder holder, int position) {
        RxBleDevice item = devices.get(position);
        holder.tvAddress.setText(item.getMacAddress());
        holder.tvName.setText(item.getName() != null ? item.getName() : "");
        holder.tvRssi.setText(String.valueOf(deviceRssi.get(item.getMacAddress())));
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    public class DeviceViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_address) TextView tvAddress;
        @BindView(R.id.tv_name) TextView tvName;
        @BindView(R.id.tv_rssi) TextView tvRssi;

        public DeviceViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}