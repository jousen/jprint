# jprint

**Android Bluetooth Print Tool.** 

**Android 蓝牙打印工具库**

------

## 1、Feature 特性

- Support Android 5.0+       Android 5.0以上系统版本支持
- Support Only AndroidX    只支持 AndroidX
- Supports ESC and TSC（Label） Command 支持ESC小票和TSC标签打印命令

## 2、Import 依赖

1、Add the JitPack maven repository to the list of repositories 将JitPack存储库添加到您的构建文件中(项目根目录下build.gradle文件)

**build.gradle**

```
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

2、Add dependencies 

```
[![](https://jitpack.io/v/jousen/jprint.svg)](https://jitpack.io/#jousen/jprint)
```

```
dependencies {
    implementation 'com.github.jousen:jprint:2.5'
}
```

## 3、Usage 使用

##### 1、Auto Import Bellow Permission 已自动添加下面的权限

```
<uses-permission android:name="android.permission.BLUETOOTH" />
<uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
```

if Android 6.0+ ，need android.permission.ACCESS_COARSE_LOCATION and android.permission.ACCESS_FINE_LOCATION for find BluetoothDevice 

大于Android 6.0 ，需要申请android.permission.ACCESS_COARSE_LOCATION 和 android.permission.ACCESS_FINE_LOCATION，用于发现周围的蓝牙设备

------

##### 2、Search BluetoothDevice  Example 搜索蓝牙设备范例

In Activity onCreate 在进入Activity初始化后

```
    public class MainActivity extends AppCompatActivity {
    private final Context context = MainActivity.this;
    private DeviceListAdapter deviceListAdapter;

    private BtBroadcastReceiver btBroadcastReceiver;
    private Set<String> deviceCheck;
    private List<BluetoothDevice> items;
    private BluetoothDevice selectDevice;
        
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
   }
```

DeviceListAdapter

```
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
```

OnItemClickListener

```
public interface OnItemClickListener {
    void itemClick(int position, BluetoothDevice bluetoothDevice);
}
```

------

##### 3、Activity onDestroy 关闭Activity时

```
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
```

------

##### 4、ESC print 小票打印模式

```
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
```

```
public class TestDataMaker implements PrintDataMaker {

    @Override
    public List<byte[]> getPrintData(int printer_type) {
        ArrayList<byte[]> data = new ArrayList<>();
        try {
            PrinterWriter printer = new PrinterWriter58mm();
            printer.setAlignCenter();
            data.add(printer.getDataAndReset());
            printer.setAlignCenter();
            printer.setEmphasizedOn();
            printer.setFontSize(1);
            printer.println("测试小票打印");
            printer.setFontSize(0);
            printer.setEmphasizedOff();
            printer.printLineFeed();
            printer.printLine();
            printer.setAlignLeft();
            printer.println("时间：2019-01-01 10:00:00");
            printer.printLine();
            printer.printInOneLine("名称", "数量", 0);
            printer.printLine();
            printer.printInOneLine("产品名称", "2× 60.00", 0);
            printer.setAlignCenter();
            data.add(printer.getDataAndReset());
            printer.printLineFeed();
            printer.printLine();
            printer.printLineFeed();
            printer.printLineFeed();
            printer.printLineFeed();
            printer.feedPaperCutPartial();
            data.add(printer.getDataAndClose());
            return data;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
}
```

##### 5、TSC print 标签打印（必须使用专用的标签打印机）

```
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
```

```
public class TestLabelDataMaker implements LabelPrintDataMaker {
    private final int copy;//打印份数

    private final String code;
    private final String name;
    private final String model;
    private final String price;

    public TestLabelDataMaker(String name, String code, String model, String price, int copy) {
        this.code = code;
        this.name = name;
        this.model = model;
        this.price = price;
        this.copy = copy;
    }

    @Override
    public byte[] getPrintData(int labelWidth, int labelHeight, int labelSpace) {
        //计算打印区域,设置在标签中间打印
        int widthDots = labelWidth * 8;
        int heightDots = labelHeight * 8;
        //计算左边距dots
        //左边距 dots
        int leftSpan = (int) ((float) widthDots * 0.05);
        //计算条码左边距dots
        //条码左边距 dots
        int leftCodeSpan = (widthDots - 240) / 2 - 20;
        //计算上边距dots
        //上边距 dots
        int topSpan = (heightDots - 176) / 2;

        LabelCommand printer = new LabelCommand();
        printer.addSize(labelWidth, labelHeight); // 设置标签尺寸，按照实际尺寸设置
        printer.addGap(labelSpace); // 设置标签间隙，按照实际尺寸设置，如果为无间隙纸则设置为0
        printer.addDirection(LabelCommand.DIRECTION.BACKWARD, LabelCommand.MIRROR.NORMAL);// 设置打印方向
        printer.addReference(20, 20);// 设置原点坐标
        printer.addTear(LabelCommand.ENABLE.ON); // 撕纸模式开启
        printer.addCls();// 清除打印缓冲区
        //详情
        printer.addText(leftSpan, topSpan, "名称：" + name);
        printer.addText(leftSpan, topSpan + 32, "规格：" + model);
        printer.addText(leftSpan, topSpan + 64, "价格：" + price + " 元");
        //条形码
        printer.add1DBarcode(leftCodeSpan, topSpan + 104, LabelCommand.BARCODETYPE.CODE128, 48, LabelCommand.READABEL.EANBEL, LabelCommand.ROTATION.ROTATION_0, code);
        //打印标签
        printer.addPrint(copy);
        //打印标签后 蜂鸣器响
        printer.addSound(2, 100);
        //发送数据
        Vector<Byte> data = printer.getCommand();
        return LabelUtils.Byte2byte(data);
    }
}
```

------

##参考

[AlexMofer/ProjectX](https://github.com/AlexMofer/ProjectX/tree/master/printer)

Thanks



# Licenses

```
Copyright 2021 jousen

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```