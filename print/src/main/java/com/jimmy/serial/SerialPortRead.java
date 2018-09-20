package com.jimmy.serial;

import android.os.Handler;
import android.os.Message;

import com.jimmy.printer.R;
import com.jimmy.serial.utils.SerialPort;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.security.InvalidParameterException;

public class SerialPortRead {

    private SerialPort serialPort;
    private InputStream is;
    private boolean isReading = false;
    private ReadCallback callback;
    private MyHandler myHandler;

    public void open(String path, int baudRate, ReadCallback callback) {
        try {
            this.myHandler = new MyHandler(this);
            this.callback = callback;
            this.isReading = true;
            this.serialPort = new SerialPort(new File(path), baudRate, 0);
            this.is = serialPort.getInputStream();
            new ReadThread(is).start();
        } catch (SecurityException e) {
            callback.onReading("SecurityException");
            //DisplayError(R.string.error_security);
        } catch (IOException e) {
            callback.onReading("IOException");
            //DisplayError(R.string.error_unknown);
        } catch (InvalidParameterException e) {
            callback.onReading("InvalidParameterException");
            //DisplayError(R.string.error_configuration);
        }
    }

    private class ReadThread extends Thread {

        private InputStream is;

        public ReadThread(InputStream is) {
            this.is = is;
        }

        @Override
        public void run() {
            while (isReading) {
                try {
                    if (is == null) return;
                    byte[] buffer = new byte[64];
                    int size = is.read(buffer);
                    String s = new String(buffer, 0, size);
                    sendMessage(s);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static class MyHandler extends Handler {

        private WeakReference<SerialPortRead> weakReference;

        public MyHandler(SerialPortRead read) {
            this.weakReference = new WeakReference<>(read);
        }

        @Override
        public void handleMessage(Message msg) {
            SerialPortRead read = weakReference.get();
            if (read != null && read.callback != null) {
                read.callback.onReading((String) msg.obj);
            }
        }
    }

    private void sendMessage(String s) {
        if (myHandler == null) return;
        Message msg = new Message();
        msg.obj = s;
        myHandler.sendMessage(msg);
    }

    public void close() {
        isReading = false;
        try {
            if (is != null) {
                is.close();
            }
            if (serialPort != null) {
                serialPort.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
