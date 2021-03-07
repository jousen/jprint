package com.jousen.plugin.jprint.label;

import android.bluetooth.BluetoothDevice;
import android.os.AsyncTask;

import com.jousen.plugin.jprint.connect.PrintSocketHolder;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;

/**
 * 打印执行者
 */
public class LabelPrintExecutor {
    private int labelWidth;
    private int labelHeight;
    private int labelSpace;
    private final PrintSocketHolder holder;
    private int mReconnectTimes = 0;
    private int time = 0;
    private PrintSocketHolder.OnStateChangedListener listener;
    private WeakReference<OnPrintResultListener> mListener;

    public LabelPrintExecutor(BluetoothDevice device) {
        holder = new PrintSocketHolder(device);
        setLabelWidth(40);
        setLabelHeight(30);
        setLabelSpace(2);
    }

    public LabelPrintExecutor(BluetoothDevice device, int labelWidth, int labelHeight, int labelSpace) {
        holder = new PrintSocketHolder(device);
        setLabelWidth(labelWidth);
        setLabelHeight(labelHeight);
        setLabelSpace(labelSpace);
    }

    public LabelPrintExecutor(String ip, int port, int labelWidth, int labelHeight, int labelSpace) {
        holder = new PrintSocketHolder(ip, port);
        setLabelWidth(labelWidth);
        setLabelHeight(labelHeight);
        setLabelSpace(labelSpace);
    }

    /**
     * 执行打印请求
     *
     * @return 错误代码
     */
    private int doRequest(LabelPrintDataMaker maker) {
        if (mReconnectTimes == 0) {
            holder.onPrinterStateChanged(PrintSocketHolder.STATE_0);
            byte[] data = maker.getPrintData(labelWidth, labelHeight, labelSpace);
            if (!holder.isSocketPrepared()) {
                int prepare = holder.prepareSocket();
                if (prepare != PrintSocketHolder.ERROR_0)
                    return prepare;
            }
            return holder.sendData(data);
        } else {
            holder.onPrinterStateChanged(PrintSocketHolder.STATE_0);
            byte[] data = maker.getPrintData(labelWidth, labelHeight, labelSpace);
            if (holder.isSocketPrepared()) {
                if (sendData(data))
                    return PrintSocketHolder.ERROR_0;
                else
                    return PrintSocketHolder.ERROR_100;
            } else {
                if (prepareSocket() && sendData(data)) {
                    return PrintSocketHolder.ERROR_0;
                } else {
                    return PrintSocketHolder.ERROR_100;
                }
            }
        }
    }

    /**
     * 执行打印请求
     *
     * @return 错误代码
     */
    public int doPrinterRequest(LabelPrintDataMaker maker) {
        holder.setOnStateChangedListener(listener);
        return doRequest(maker);
    }

    private boolean prepareSocket() {
        time++;
        return time < mReconnectTimes &&
                (holder.prepareSocket() == PrintSocketHolder.ERROR_0 || prepareSocket());
    }

    private boolean sendData(byte[] data) {
        if (holder.sendData(data) == PrintSocketHolder.ERROR_0) {
            time = 0;
            return true;
        } else {
            return prepareSocket() && sendData(data);
        }
    }

    /**
     * 异步执行打印请求
     */
    public void doPrinterRequestAsync(LabelPrintDataMaker maker) {
        new PrintTask().execute(maker);
    }

    /**
     * 获取输入流
     *
     * @return 输入流
     * @throws IOException 错误
     */
    public InputStream getInputStream() throws IOException {
        return holder.getInputStream();
    }

    /**
     * 销毁
     */
    @SuppressWarnings("UnusedReturnValue")
    public int closeSocket() {
        return holder.closeSocket();
    }

    /**
     * 设置IP及端口
     *
     * @param ip   IP
     * @param port 端口
     */
    public void setIp(String ip, int port) {
        holder.setIp(ip, port);
    }

    /**
     * 设置蓝牙
     *
     * @param device 设备
     */
    public void setDevice(BluetoothDevice device) {
        holder.setDevice(device);
    }

    /**
     * 设置打印标签
     *
     * @param labelWidth 标签宽度
     */
    public void setLabelWidth(int labelWidth) {
        this.labelWidth = labelWidth;
    }

    /**
     * 设置打印标签
     *
     * @param labelHeight 标签高度
     */
    public void setLabelHeight(int labelHeight) {
        this.labelHeight = labelHeight;
    }

    /**
     * 设置打印标签
     *
     * @param labelSpace 标签间距
     */
    public void setLabelSpace(int labelSpace) {
        this.labelSpace = labelSpace;
    }

    /**
     * 设置状态监听
     *
     * @param listener 监听
     */
    public void setOnStateChangedListener(PrintSocketHolder.OnStateChangedListener listener) {
        this.listener = listener;
    }

    /**
     * 设置重连次数
     *
     * @param times 次数
     */
    public void setReconnectTimes(int times) {
        mReconnectTimes = times;
    }

    /**
     * 设置结果回调
     *
     * @param listener 回调
     */
    public void setOnPrintResultListener(OnPrintResultListener listener) {
        mListener = new WeakReference<>(listener);
    }

    public interface OnPrintResultListener {
        void onResult(int errorCode);
    }

    @SuppressWarnings("ALL")
    private class PrintTask extends AsyncTask<LabelPrintDataMaker, Integer, Integer> implements
            PrintSocketHolder.OnStateChangedListener {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            holder.setOnStateChangedListener(this);
        }

        @Override
        protected Integer doInBackground(LabelPrintDataMaker... makers) {
            if (makers == null || makers.length < 1)
                return PrintSocketHolder.ERROR_0;
            return doRequest(makers[0]);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            if (values == null || values.length < 1)
                return;
            if (listener != null)
                listener.onStateChanged(values[0]);
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            if (integer != null) {
                onResult(integer);
            }
        }

        /**
         * 打印结果
         *
         * @param errorCode 错误代码
         */
        private void onResult(int errorCode) {
            try {
                if (mListener != null)
                    mListener.get().onResult(errorCode);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onStateChanged(int state) {
            publishProgress(state);
        }
    }
}