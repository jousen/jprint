package com.jousen.plugin.jprint.connect;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BtBroadcastReceiver extends BroadcastReceiver {
    private BtReceiverListener btReceiverListener;

    public void setOnReceiverListener(BtReceiverListener btReceiverListener) {
        this.btReceiverListener = btReceiverListener;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onReceive(Context context, Intent intent) {
        String intentAction = intent.getAction();
        if (intentAction == null) {
            return;
        }
        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        if (device == null) {
            return;
        }
        //发现设备且只选择打印类型的设备
        if (intentAction.equals(BluetoothDevice.ACTION_FOUND) && device.getBluetoothClass().getMajorDeviceClass() == BluetoothClass.Device.Major.IMAGING) {
            btReceiverListener.findDevice(device);
            return;
        }
        //设备绑定
        if (intentAction.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED) && device.getBondState() == BluetoothDevice.BOND_BONDED) {
            btReceiverListener.deviceBonded(device);
            return;
        }
        if (intentAction.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
            btReceiverListener.findDeviceFinish();
        }
    }
}