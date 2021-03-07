package com.jousen.plugin.jprint.connect;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.IntentFilter;

public class BtBroadcastFilter {
    /**
     * 获取广播拦截设置
     *
     * @return IntentFilter
     */
    public static IntentFilter getFilter() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);//周围设备查询
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);//设备绑定状态变化
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);//设备绑定状态变化
        return intentFilter;
    }
}
