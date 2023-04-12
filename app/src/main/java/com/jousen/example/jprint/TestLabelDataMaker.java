package com.jousen.example.jprint;

import com.jousen.plugin.jprint.label.LabelCommand;
import com.jousen.plugin.jprint.label.LabelPrintDataMaker;
import com.jousen.plugin.jprint.label.LabelUtils;

import java.util.Vector;

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