package com.jousen.plugin.jprint.label;

/**
 * Print Maker
 */
public interface LabelPrintDataMaker {
    byte[] getPrintData(int labelWidth, int labelHeight, int labelSpace);
}