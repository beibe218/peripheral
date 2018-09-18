package com.jimmy.printer.usb;

import android.content.Context;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.os.Message;

import com.jimmy.printer.common.SendCallback;
import com.jimmy.printer.common.SendResultCode;

import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UsbPrint {
    private static UsbPrint INSTANCE;
    private final UsbManager usbManager;
    private SendCallback sendCallback;
    private final ExecutorService threadPool;
    private MyHandler myHandler;

    private UsbPrint(Context context, SendCallback sendCallback) {
        this.usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        this.sendCallback = sendCallback;
        this.threadPool = Executors.newFixedThreadPool(3);
        this.myHandler = new MyHandler(this);
    }

    public static UsbPrint getInstance(Context context, SendCallback sendCallback) {
        if (INSTANCE == null) {
            synchronized (UsbPrint.class) {
                if (INSTANCE == null) {
                    INSTANCE = new UsbPrint(context, sendCallback);
                }
            }
        }
        return INSTANCE;
    }

    public void sendPrintCommand(UsbPrinter printer, byte[] bytes) {
        if (printer == null || printer.getUsbDevice() == null) {
            return;
        }
        SendCommandThread thread = new SendCommandThread(usbManager, printer, bytes);
        threadPool.execute(thread);
    }

    private class SendCommandThread extends Thread {
        private UsbManager usbManager;
        private UsbPrinter usbPrinter;
        private byte[] bytes;

        public SendCommandThread(UsbManager usbManager, UsbPrinter usbPrinter, byte[] bytes) {
            this.usbManager = usbManager;
            this.usbPrinter = usbPrinter;
            this.bytes = bytes;
        }

        @Override
        public void run() {
            super.run();
            UsbDeviceConnection connection = usbManager.openDevice(usbPrinter.getUsbDevice());
            if (connection != null && connection.claimInterface(usbPrinter.getUsbInterface(), true)) {
                int result = connection.bulkTransfer(usbPrinter.getUsbOut(), bytes, bytes.length, 500);
                connection.close();

                int sendResultCode = result > 0 ? SendResultCode.SEND_SUCCESS : SendResultCode.SEND_FAILED;
                sendMessage(sendResultCode, usbPrinter.getPrinterName());
            }
        }
    }

    private void sendMessage(int resultCode, String printerId) {
        Message msg = new Message();
        msg.obj = printerId;
        msg.what = resultCode;
        myHandler.sendMessage(msg);
    }

    private static class MyHandler extends Handler {
        private WeakReference<UsbPrint> weakReference;

        public MyHandler(UsbPrint usbPrint) {
            this.weakReference = new WeakReference<>(usbPrint);
        }

        @Override
        public void handleMessage(Message msg) {
            UsbPrint usbPrint = weakReference.get();
            if (usbPrint != null && usbPrint.sendCallback != null) {
                usbPrint.sendCallback.onCallback(msg.what, (String) msg.obj);
            }
        }
    }

}
