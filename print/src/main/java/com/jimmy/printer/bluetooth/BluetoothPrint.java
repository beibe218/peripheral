package com.jimmy.printer.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;

import com.jimmy.printer.common.SendCallback;
import com.jimmy.printer.common.SendResultCode;
import com.jimmy.printer.ethernet.EthernetPrint;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BluetoothPrint {
    private static final String TAG = "BluetoothPrint";

    private static final String SPP_UUID = "00001101-0000-1000-8000-00805F9B34FB";
    private static BluetoothPrint INSTANCE;
    private SendCallback sendCallback;
    private UUID uuid;
    private MyHandler myHandler;
    private final ExecutorService threadPool;

    private BluetoothPrint(SendCallback sendCallback) {
        this.uuid = UUID.fromString(SPP_UUID);
        this.myHandler = new MyHandler(this);
        this.sendCallback = sendCallback;
        this.threadPool = Executors.newFixedThreadPool(3);
    }

    public static BluetoothPrint getInstance(SendCallback sendCallback) {
        if (INSTANCE == null) {
            synchronized (BluetoothPrint.class) {
                if (INSTANCE == null) {
                    INSTANCE = new BluetoothPrint(sendCallback);
                }
            }
        }
        return INSTANCE;
    }

    public void sendPrintCommand(BluetoothDevice device, byte[] bytes) {
        SendCommandThread thread = new SendCommandThread(device, bytes);
        threadPool.execute(thread);
    }

    private class SendCommandThread extends Thread {
        private BluetoothDevice device;
        private byte[] bytes;
        private BluetoothSocket socket;

        public SendCommandThread(BluetoothDevice device, byte[] bytes) {
            this.device = device;
            this.bytes = bytes;
        }

        @Override
        public void run() {
            try {
                socket = device.createRfcommSocketToServiceRecord(uuid);
                socket.connect();
                OutputStream os = socket.getOutputStream();
                os.write(bytes);
                os.flush();
                os.close();
                sendStatus(device.getName(), SendResultCode.SEND_SUCCESS);
            } catch (IOException e) {
                e.printStackTrace();
                sendStatus(device.getName(), SendResultCode.SEND_FAILED);
            }
        }
    }

    private void sendStatus(String ip, int code) {
        Message msg = Message.obtain();
        msg.what = code;
        msg.obj = ip;
        myHandler.sendMessage(msg);
    }

    private static class MyHandler extends Handler {

        private WeakReference<BluetoothPrint> reference;

        public MyHandler(BluetoothPrint manager) {
            this.reference = new WeakReference<>(manager);
        }

        @Override
        public void handleMessage(Message msg) {
            BluetoothPrint manager = reference.get();
            if (manager != null) {
                manager.sendCallback.onCallback(msg.what, (String) msg.obj);
            }
        }
    }
}
