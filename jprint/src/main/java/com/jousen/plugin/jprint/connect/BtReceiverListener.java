package com.jousen.plugin.jprint.connect;

import android.bluetooth.BluetoothDevice;

public interface BtReceiverListener {
    void findDevice(BluetoothDevice device);

    void findDeviceFinish();

    void deviceBonded(BluetoothDevice device);
}
