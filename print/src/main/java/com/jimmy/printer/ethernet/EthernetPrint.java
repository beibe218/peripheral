package com.jimmy.printer.ethernet;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.jimmy.printer.common.SendCallback;
import com.jimmy.printer.common.SendResultCode;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EthernetPrint {
    private static final String TAG = "EthernetPrint";
    private int timeout = 3000;
    private static EthernetPrint INSTANCE;
    private final ExecutorService threadPool;
    private MyHandler myHandler;
    private SendCallback sendCallback;

    private EthernetPrint(SendCallback sendCallback) {
        this.sendCallback = sendCallback;
        this.myHandler = new MyHandler(this);
        this.threadPool = Executors.newFixedThreadPool(3);
    }

    public static EthernetPrint getInstance(SendCallback sendCallback) {
        if (INSTANCE == null) {
            synchronized (EthernetPrint.class) {
                if (INSTANCE == null) {
                    INSTANCE = new EthernetPrint(sendCallback);
                }
            }
        }
        return INSTANCE;
    }

    public void sendPrintCommand(String ip, int port, byte[] bytes) {
        SendCommandThread thread = new SendCommandThread(ip, port, bytes);
        threadPool.execute(thread);
    }

    private class SendCommandThread extends Thread {

        private Socket socket;
        private InetAddress inetAddress;
        private InetSocketAddress socketAddress;
        private byte[] bytes;
        private String ip;

        public SendCommandThread(String ip, int port, byte[] bytes) {
            try {
                this.ip = ip;
                this.bytes = bytes;
                this.socket = new Socket();
                this.inetAddress = Inet4Address.getByName(ip);
                this.socketAddress = new InetSocketAddress(inetAddress, port);
            } catch (UnknownHostException e) {
                e.printStackTrace();
                Log.d(TAG, "IpAddress is invalid", e);
                sendStatus(ip, SendResultCode.SEND_FAILED);
            }
        }

        @Override
        public void run() {
            try {
                socket.connect(socketAddress, timeout);
                OutputStream os = socket.getOutputStream();
                os.write(bytes);
                os.flush();
                socket.close();
                sendStatus(ip, SendResultCode.SEND_SUCCESS);
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG, "Socket connect fail", e);
                sendStatus(ip, SendResultCode.SEND_FAILED);
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

        private WeakReference<EthernetPrint> reference;

        public MyHandler(EthernetPrint manager) {
            this.reference = new WeakReference<>(manager);
        }

        @Override
        public void handleMessage(Message msg) {
            EthernetPrint manager = reference.get();
            if (manager != null) {
                manager.sendCallback.onCallback(msg.what, (String) msg.obj);
            }
        }
    }
}
