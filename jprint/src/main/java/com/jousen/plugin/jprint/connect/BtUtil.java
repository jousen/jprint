package com.jousen.plugin.jprint.connect;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

public class BtUtil {

    /**
     * 检查蓝牙是否关闭 若关闭尝试自动开启
     */
    @SuppressLint("MissingPermission")
    public static boolean isBluetoothDisable() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            return true;
        }
        if (bluetoothAdapter.isEnabled()) {
            return false;
        }
        boolean result = bluetoothAdapter.enable();
        if (result) {
            return false;
        }
        return !bluetoothAdapter.isEnabled();
    }


    /**
     * 根据mac地址获取设备
     */
    @SuppressLint("MissingPermission")
    public static BluetoothDevice getDeviceByAddress(String mac) {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothDevice findDevice = null;
        for (BluetoothDevice device : bluetoothAdapter.getBondedDevices()) {
            if (device.getAddress().equals(mac)) {
                findDevice = device;
                break;
            }
        }
        return findDevice;
    }

    /**
     * 开启搜索的设备
     */
    @SuppressLint("MissingPermission")
    public static void startSearch() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }
        bluetoothAdapter.startDiscovery();
    }

    /**
     * 取消搜索
     */
    @SuppressLint("MissingPermission")
    public static void cancelSearch() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }
    }
}
