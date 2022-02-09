package com.jousen.example.jprint;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jousen.plugin.jprint.connect.BtBroadcastFilter;
import com.jousen.plugin.jprint.connect.BtBroadcastReceiver;
import com.jousen.plugin.jprint.connect.BtReceiverListener;
import com.jousen.plugin.jprint.connect.BtUtil;
import com.jousen.plugin.jprint.connect.PrintSocketHolder;
import com.jousen.plugin.jprint.esc.PrintExecutor;
import com.jousen.plugin.jprint.label.LabelPrintExecutor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private final Context context = MainActivity.this;
    private DeviceListAdapter deviceListAdapter;

    private BtBroadcastReceiver btBroadcastReceiver;
    private Set<String> deviceCheck;
    private List<BluetoothDevice> items;
    private BluetoothDevice selectDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindView();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void bindView() {
        //检查蓝牙设置
        if (BtUtil.isBluetoothDisable()) {
            Toast.makeText(context, "蓝牙未打开", Toast.LENGTH_SHORT).show();
        }
        deviceCheck = new HashSet<>();
        items = new ArrayList<>();
        deviceListAdapter = new DeviceListAdapter(items);
        btBroadcastReceiver = new BtBroadcastReceiver();
        btBroadcastReceiver.setOnReceiverListener(new BtReceiverListener() {
            @Override
            public void findDevice(BluetoothDevice device) {
                //相同device校验
                if (!deviceCheck.contains(device.getAddress())) {
                    deviceCheck.add(device.getAddress());
                    items.add(device);
                    deviceListAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void findDeviceFinish() {
                unregisterReceiver(btBroadcastReceiver);
                Toast.makeText(context, "搜索完成", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void deviceBonded(BluetoothDevice device) {
                deviceListAdapter.notifyDataSetChanged();
            }
        });

        RecyclerView recyclerView = findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
        recyclerView.setAdapter(deviceListAdapter);
        deviceListAdapter.setOnItemClickListener((position, bluetoothDevice) -> selectDevice = bluetoothDevice);
        //搜索蓝牙
        findViewById(R.id.bt_search).setOnClickListener(v -> {
            Toast.makeText(context, "开始搜索", Toast.LENGTH_SHORT).show();
            registerReceiver(btBroadcastReceiver, BtBroadcastFilter.getFilter());
            BtUtil.startSearch();
        });
        //打印小票
        findViewById(R.id.test_esc).setOnClickListener(v -> printEsc());
        //打印标签
        findViewById(R.id.test_label).setOnClickListener(v -> printLabel());
    }

    private void printEsc() {
        //可根据mac地址获取device实例
        //BluetoothDevice device = BtUtil.getDeviceByAddress(deviceMac);
        TestDataMaker maker = new TestDataMaker();
        //异步打印
        PrintExecutor executor = new PrintExecutor(selectDevice, PrintExecutor.TYPE_58);
        executor.doPrinterRequestAsync(maker);
        executor.setOnPrintResultListener(errorCode -> {
            if (errorCode == PrintSocketHolder.ERROR_0) {
                Toast.makeText(context, "打印成功", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "打印出现问题", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void printLabel() {
        //可根据mac地址获取device实例
        //BluetoothDevice device = BtUtil.getDeviceByAddress(deviceMac);
        TestLabelDataMaker maker = new TestLabelDataMaker("测试商品", "123456", "测试规格", "123.0元", 1);
        //异步打印
        LabelPrintExecutor executor = new LabelPrintExecutor(selectDevice);
        executor.doPrinterRequestAsync(maker);
        executor.setOnPrintResultListener(errorCode -> {
            if (errorCode == PrintSocketHolder.ERROR_0) {
                Toast.makeText(context, "打印成功", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "打印出现问题", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //退出前记得检查是否解绑
        BtUtil.cancelSearch();
        if (btBroadcastReceiver != null) {
            unregisterReceiver(btBroadcastReceiver);
            btBroadcastReceiver = null;
        }
    }
}