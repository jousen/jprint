package com.jousen.example.jprint;

import com.jousen.plugin.jprint.esc.PrintDataMaker;
import com.jousen.plugin.jprint.esc.PrinterWriter;
import com.jousen.plugin.jprint.esc.PrinterWriter58mm;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 李易航
 * @date 2019/6/28
 */
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
