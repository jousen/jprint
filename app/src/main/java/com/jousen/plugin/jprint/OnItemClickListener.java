package com.jousen.plugin.jprint;


import android.bluetooth.BluetoothDevice;

/**
 * @author 李易航
 * @date 2021/3/6
 */
public interface OnItemClickListener {
    void itemClick(int position, BluetoothDevice bluetoothDevice);
}