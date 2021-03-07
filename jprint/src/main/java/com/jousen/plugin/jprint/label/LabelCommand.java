package com.jousen.plugin.jprint.label;

import android.graphics.Bitmap;

import java.io.UnsupportedEncodingException;
import java.util.Vector;

/**
 * TSC Command
 */
public class LabelCommand {
    private final Vector<Byte> Command;

    public LabelCommand() {
        this.Command = new Vector<>();
    }

    public LabelCommand(int width, int height, int gap) {
        this.Command = new Vector<>(4096, 1024);
        addSize(width, height);
        addGap(gap);
    }

    public void clrCommand() {
        this.Command.clear();
    }

    private void addStrToCommand(String str) {
        byte[] bs = null;
        if (!str.equals("")) {
            try {
                bs = str.getBytes("GB2312");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            if (bs != null) {
                for (byte b : bs) {
                    this.Command.add(b);
                }
            }
        }
    }

    public void addGap(int gap) {
        String str = "GAP " + gap + " mm," + 0 + " mm" + "\r\n";
        addStrToCommand(str);
    }

    public void addSize(int width, int height) {
        String str = "SIZE " + width + " mm," + height + " mm" + "\r\n";
        addStrToCommand(str);
    }

    public void addCashdrwer(FOOT m, int t1, int t2) {
        String str = "CASHDRAWER " + m.getValue() + "," + t1 + "," + t2 + "\r\n";
        addStrToCommand(str);
    }

    public void addOffset(int offset) {
        String str = "OFFSET " + offset + " mm" + "\r\n";
        addStrToCommand(str);
    }

    public void addSpeed(SPEED speed) {
        String str = "SPEED " + speed.getValue() + "\r\n";
        addStrToCommand(str);
    }

    public void addDensity(DENSITY density) {
        String str = "DENSITY " + density.getValue() + "\r\n";
        addStrToCommand(str);
    }

    public void addDirection(DIRECTION direction, MIRROR mirror) {
        String str = "DIRECTION " + direction.getValue() + ',' + mirror.getValue() + "\r\n";
        addStrToCommand(str);
    }

    public void addReference(int x, int y) {
        String str = "REFERENCE " + x + "," + y + "\r\n";
        addStrToCommand(str);
    }

    public void addShif(int shift) {
        String str = "SHIFT " + shift + "\r\n";
        addStrToCommand(str);
    }

    public void addCls() {
        String str = "CLS\r\n";
        addStrToCommand(str);
    }

    public void addFeed(int dot) {
        String str = "FEED " + dot + "\r\n";
        addStrToCommand(str);
    }

    public void addBackFeed(int dot) {
        String str = "BACKFEED " + dot + "\r\n";
        addStrToCommand(str);
    }

    public void addFormFeed() {
        String str = "FORMFEED\r\n";
        addStrToCommand(str);
    }

    public void addHome() {
        String str = "HOME\r\n";
        addStrToCommand(str);
    }

    public void addPrint(int m, int n) {
        String str = "PRINT " + m + "," + n + "\r\n";
        addStrToCommand(str);
    }

    public void addPrint(int m) {
        String str = "PRINT " + m + "\r\n";
        addStrToCommand(str);
    }

    public void addCodePage(CODEPAGE page) {
        String str = "CODEPAGE " + page.getValue() + "\r\n";
        addStrToCommand(str);
    }

    public void addSound(int level, int interval) {
        String str = "SOUND " + level + "," + interval + "\r\n";
        addStrToCommand(str);
    }

    public void addLimitFeed(int n) {
        String str = "LIMITFEED " + n + "\r\n";
        addStrToCommand(str);
    }

    public void addSelfTest() {
        String str = "SELFTEST\r\n";
        addStrToCommand(str);
    }

    public void addBar(int x, int y, int width, int height) {
        String str = "BAR " + x + "," + y + "," + width + "," + height + "\r\n";
        addStrToCommand(str);
    }

    public void addText(int x, int y, String text) {
        String str = "TEXT " + x + "," + y + "," + "\"" + LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE.getValue() + "\"" + "," + LabelCommand.ROTATION.ROTATION_0.getValue() + "," +
                LabelCommand.FONTMUL.MUL_1.getValue() + "," + LabelCommand.FONTMUL.MUL_1.getValue() + "," + "\"" + text + "\"" + "\r\n";
        addStrToCommand(str);
    }

    public void addText(int x, int y, FONTTYPE font, ROTATION rotation, FONTMUL Xscal, FONTMUL Yscal, String text) {
        String str = "TEXT " + x + "," + y + "," + "\"" + font.getValue() + "\"" + "," + rotation.getValue() + "," +
                Xscal.getValue() + "," + Yscal.getValue() + "," + "\"" + text + "\"" + "\r\n";
        addStrToCommand(str);
    }

    public void add1DBarcode(int x, int y, BARCODETYPE type, int height, READABEL readable, ROTATION rotation, String content) {
        int narrow = 2;
        int width = 2;
        String str = "BARCODE " + x + "," + y + "," + "\"" + type.getValue() + "\"" + "," + height + "," + readable.getValue() +
                "," + rotation.getValue() + "," + narrow + "," + width + "," + "\"" + content + "\"" + "\r\n";
        addStrToCommand(str);
    }

    public void add1DBarcode(int x, int y, BARCODETYPE type, int height, READABEL readable, ROTATION rotation, int narrow, int width, String content) {
        String str = "BARCODE " + x + "," + y + "," + "\"" + type.getValue() + "\"" + "," + height + "," + readable.getValue() +
                "," + rotation.getValue() + "," + narrow + "," + width + "," + "\"" + content + "\"" + "\r\n";
        addStrToCommand(str);
    }

    public void addBox(int x, int y, int xend, int yend, int thickness) {
        String str = "BOX " + x + "," + y + "," + xend + "," + yend + "," + thickness + "\r\n";
        addStrToCommand(str);
    }

    public void addBitmap(int x, int y, BITMAP_MODE mode, int nWidth, Bitmap b) {
        if (b != null) {
            int width = (nWidth + 7) / 8 * 8;
            int height = b.getHeight() * width / b.getWidth();
            Bitmap grayBitmap = LabelUtils.toGrayscale(b);
            Bitmap rszBitmap = LabelUtils.resizeImage(grayBitmap, width, height);
            byte[] src = LabelUtils.bitmapToBWPix(rszBitmap);
            height = src.length / width;
            width /= 8;
            String str = "BITMAP " + x + "," + y + "," + width + "," + height + "," + mode.getValue() + ",";
            addStrToCommand(str);
            byte[] codeContent = LabelUtils.pixToLabelCmd(src);
            for (byte value : codeContent) {
                this.Command.add(value);
            }
        }
    }

    public void addErase(int x, int y, int xwidth, int yheight) {
        String str = "ERASE " + x + "," + y + "," + xwidth + "," + yheight + "\r\n";
        addStrToCommand(str);
    }

    public void addReverse(int x, int y, int xwidth, int yheight) {
        String str = "REVERSE " + x + "," + y + "," + xwidth + "," + yheight + "\r\n";
        addStrToCommand(str);
    }

    public void addQRCode(int x, int y, EEC level, int cellwidth, ROTATION rotation, String data) {
        String str;
        str = "QRCODE " + x + "," + y + "," + level.getValue() + "," + cellwidth + "," + 'A' + "," + rotation.getValue() +
                "," + "\"" + data + "\"" + "\r\n";
        addStrToCommand(str);
    }

    public Vector<Byte> getCommand() {
        return this.Command;
    }

    public void addQueryPrinterType() {
        String str = "~!T\r\n";
        addStrToCommand(str);
    }

    public void addQueryPrinterStatus() {
        this.Command.add((byte) 27);
        this.Command.add((byte) 33);
        this.Command.add((byte) 63);
    }

    public void addResetPrinter() {
        this.Command.add((byte) 27);
        this.Command.add((byte) 33);
        this.Command.add((byte) 82);
    }

    public void addQueryPrinterLife() {
        String str = "~!@\r\n";
        addStrToCommand(str);
    }

    public void addQueryPrinterMemory() {
        String str = "~!A\r\n";
        addStrToCommand(str);
    }

    public void addQueryPrinterFile() {
        String str = "~!F\r\n";
        addStrToCommand(str);
    }

    public void addQueryPrinterCodePage() {
        String str = "~!I\r\n";
        addStrToCommand(str);
    }

    public void addPeel(ENABLE enable) {
        String str = "";
        if (enable.getValue() == 0) {
            str = "SET PEEL " + enable.getValue() + "\r\n";
        }
        addStrToCommand(str);
    }

    public void addTear(ENABLE enable) {
        String str = "SET TEAR " + enable.getValue() + "\r\n";
        addStrToCommand(str);
    }

    public void addCutter(ENABLE enable) {
        String str = "SET CUTTER " + enable.getValue() + "\r\n";
        addStrToCommand(str);
    }

    public void addCutterBatch() {
        String str = "SET CUTTER BATCH\r\n";
        addStrToCommand(str);
    }

    public void addCutterPieces(short number) {
        String str = "SET CUTTER " + number + "\r\n";
        addStrToCommand(str);
    }

    public void addReprint(ENABLE enable) {
        String str = "SET REPRINT " + enable.getValue() + "\r\n";
        addStrToCommand(str);
    }

    public void addPrintKey(ENABLE enable) {
        String str = "SET PRINTKEY " + enable.getValue() + "\r\n";
        addStrToCommand(str);
    }

    public void addPrintKey(int m) {
        String str = "SET PRINTKEY " + m + "\r\n";
        addStrToCommand(str);
    }

    public void addPartialCutter(ENABLE enable) {
        String str = "SET PARTIAL_CUTTER " + enable.getValue() + "\r\n";

        addStrToCommand(str);
    }

    public void addUserCommand(String command) {
        addStrToCommand(command);
    }

    public enum BARCODETYPE {
        CODE128("128"), CODE128M("128M"), EAN128("EAN128"), ITF25("25"), ITF25C("25C"), CODE39("39"),
        CODE39C("39C"), CODE39S("39S"), CODE93("93"), EAN13("EAN13"), EAN13_2("EAN13+2"), EAN13_5("EAN13+5"),
        EAN8("EAN8"), EAN8_2("EAN8+2"), EAN8_5("EAN8+5"), CODABAR("CODA"), POST("POST"),
        UPCA("UPCA"), UPCA_2("UPCA+2"), UPCA_5("UPCA+5"), UPCE("UPCE13"), UPCE_2("UPCE13+2"),
        UPCE_5("UPCE13+5"), CPOST("CPOST"), MSI("MSI"),
        MSIC("MSIC"), PLESSEY("PLESSEY"), ITF14("ITF14"), EAN14("EAN14");

        private final String value;

        BARCODETYPE(String value) {
            this.value = value;
        }

        public String getValue() {
            return this.value;
        }
    }

    public enum BITMAP_MODE {
        OVERWRITE(0), OR(1), XOR(2);

        private final int value;

        BITMAP_MODE(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }
    }

    public enum CODEPAGE {
        PC437(437), PC850(850), PC852(852), PC860(860), PC863(863), PC865(865), WPC1250(1250), WPC1252(1252),
        WPC1253(1253), WPC1254(1254);

        private final int value;

        CODEPAGE(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }
    }

    public enum DENSITY {
        DNESITY0(0), DNESITY1(1), DNESITY2(2), DNESITY3(3), DNESITY4(4), DNESITY5(5), DNESITY6(6),
        DNESITY7(7), DNESITY8(8),
        DNESITY9(9), DNESITY10(10), DNESITY11(11), DNESITY12(12), DNESITY13(13), DNESITY14(14), DNESITY15(15);

        private final int value;

        DENSITY(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }
    }

    public enum DIRECTION {
        FORWARD(0), BACKWARD(1);

        private final int value;

        DIRECTION(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }
    }

    public enum EEC {
        LEVEL_L("L"), LEVEL_M("M"), LEVEL_Q("Q"), LEVEL_H("H");

        private final String value;

        EEC(String value) {
            this.value = value;
        }

        public String getValue() {
            return this.value;
        }
    }

    public enum FONTMUL {
        MUL_1(1), MUL_2(2), MUL_3(3), MUL_4(4), MUL_5(5), MUL_6(6), MUL_7(7), MUL_8(8), MUL_9(9), MUL_10(10);

        private final int value;

        FONTMUL(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }
    }

    public enum FONTTYPE {
        FONT_1("1"), FONT_2("2"), FONT_3("3"), FONT_4("4"), FONT_5("5"), FONT_6("6"), FONT_7("7"),
        FONT_8("8"), SIMPLIFIED_CHINESE("TSS24.BF2"), TRADITIONAL_CHINESE("TST24.BF2"), KOREAN("K");

        private final String value;

        FONTTYPE(String value) {
            this.value = value;
        }

        public String getValue() {
            return this.value;
        }
    }

    public enum FOOT {
        F2(0), F5(1);

        private final int value;

        FOOT(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }
    }

    public enum MIRROR {
        NORMAL(0), MIRROR(1);

        private final int value;

        MIRROR(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }
    }

    public enum READABEL {
        DISABLE(0), EANBEL(1);

        private final int value;

        READABEL(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }
    }

    public enum ROTATION {
        ROTATION_0(0), ROTATION_90(90), ROTATION_180(180), ROTATION_270(270);

        private final int value;

        ROTATION(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }
    }

    public enum SPEED {
        SPEED1DIV5(1.5F), SPEED2(2.0F), SPEED3(3.0F), SPEED4(4.0F);

        private final float value;

        SPEED(float value) {
            this.value = value;
        }

        public float getValue() {
            return this.value;
        }
    }

    public enum ENABLE {
        OFF(0), ON(1);

        private final int value;

        ENABLE(int value) {
            this.value = value;
        }

        public byte getValue() {
            return (byte) this.value;
        }
    }
}