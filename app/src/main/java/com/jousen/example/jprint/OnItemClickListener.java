package com.jousen.example.jprint;


import android.bluetooth.BluetoothDevice;

public interface OnItemClickListener {
    void itemClick(int position, BluetoothDevice bluetoothDevice);
}