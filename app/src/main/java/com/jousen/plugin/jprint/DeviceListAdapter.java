package com.jousen.plugin.jprint;

import android.bluetooth.BluetoothDevice;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DeviceListAdapter extends RecyclerView.Adapter<DeviceListAdapter.VH> {
    private final List<BluetoothDevice> items;
    private int selectedDevice = -1;
    private OnItemClickListener onItemClickListener;

    public DeviceListAdapter(List<BluetoothDevice> items) {
        this.items = items;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        BluetoothDevice device = items.get(position);
        String name = device.getName();
        String address = device.getAddress();
        String bound = (device.getBondState() == BluetoothDevice.BOND_BONDED ? "已绑定" : "未绑定");
        if (position == selectedDevice) {
            bound = "当前选择的打印设备";
        }
        String info = "名称：" + name + "\n设备：" + address + "\n状态：" + bound;
        holder.text.setText(info);
        holder.text.setOnClickListener(v -> {
            if (device.getBondState() == BluetoothDevice.BOND_NONE) {
                device.createBond();
                return;
            }
            if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                selectedDevice = position;
                onItemClickListener.itemClick(position, device);
                notifyDataSetChanged();
            }
        });
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.device_items, parent, false);
        return new VH(v);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class VH extends RecyclerView.ViewHolder {
        public final TextView text;

        public VH(View v) {
            super(v);
            text = v.findViewById(R.id.text);
        }
    }
}
