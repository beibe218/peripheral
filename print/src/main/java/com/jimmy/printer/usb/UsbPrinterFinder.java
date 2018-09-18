package com.jimmy.printer.usb;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.util.Log;

import com.jimmy.printer.common.PrinterFinderCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class UsbPrinterFinder {
    private static final String TAG = "UsbPrinterFinder";
    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    private final UsbManager usbManager;
    private final PendingIntent usbPermissionIntent;
    private Context context;
    private PrinterFinderCallback<UsbPrinter> finderCallback;
    private List<UsbPrinter> usbPrinters;

    public UsbPrinterFinder(Context context, PrinterFinderCallback<UsbPrinter> finderCallback) {
        this.usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        this.usbPermissionIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_USB_PERMISSION), 0);
        this.context = context;
        this.finderCallback = finderCallback;
        this.usbPrinters = new ArrayList<>();

        IntentFilter usbFilter = new IntentFilter();
        usbFilter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        usbFilter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        context.registerReceiver(usbReceiver, usbFilter);

        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        context.registerReceiver(usbPermissionReceiver, filter);
    }

    private BroadcastReceiver usbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            startFinder();
        }
    };

    private BroadcastReceiver usbPermissionReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                UsbDevice usbDevice = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                boolean hasPermission = intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false);
                if (hasPermission && usbDevice != null) {
                    UsbPrinter usbPrinter = getUsbPrinter(usbDevice);
                    if (usbPrinter != null) {
                        usbPrinters.add(usbPrinter);
                        if (finderCallback != null) {
                            finderCallback.onFound(usbPrinter);
                            finderCallback.onFinished(usbPrinters);
                        }
                        Log.d(TAG, "usbPermissionReceiver add " + usbPrinter.getPrinterName());
                    }
                }
            }
        }
    };

    public void startFinder() {
        usbPrinters.clear();
        if (finderCallback != null) {
            finderCallback.onStart();
        }

        HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();
        Set<Map.Entry<String, UsbDevice>> entries = deviceList.entrySet();
        for (Map.Entry<String, UsbDevice> entry : entries) {
            UsbDevice usbDevice = entry.getValue();
            if (!isUsbPrinter(usbDevice)) {
                continue;
            }

            if (usbManager.hasPermission(usbDevice)) {
                UsbPrinter usbPrinter = getUsbPrinter(usbDevice);
                if (usbPrinter != null) {
                    usbPrinters.add(usbPrinter);
                    if (finderCallback != null) {
                        finderCallback.onFound(usbPrinter);
                    }
                    Log.d(TAG, "hasPermission add " + usbPrinter.getPrinterName());
                }

            } else {
                usbManager.requestPermission(usbDevice, usbPermissionIntent);
                Log.d(TAG, "requestPermission " + usbDevice.getDeviceName());
            }

        }

        if (finderCallback != null) {
            finderCallback.onFinished(usbPrinters);
            Log.d(TAG, "for startFinder finished");
        }
    }

    private boolean isUsbPrinter(UsbDevice usbDevice) {
        if (usbDevice == null) {
            return false;
        }

        UsbInterface usbInterface = null;
        for (int intf = 0; intf < usbDevice.getInterfaceCount(); intf++) {
            if (usbDevice.getInterface(intf).getInterfaceClass() == 7) {
                usbInterface = usbDevice.getInterface(intf);
                break;
            }
        }
        return usbInterface != null;
    }

    private UsbPrinter getUsbPrinter(UsbDevice usbDevice) {
        if (usbDevice == null) {
            return null;
        }

        UsbInterface usbInterface = null;
        UsbEndpoint usbIn = null;
        UsbEndpoint usbOut = null;
        UsbDeviceConnection usbDeviceConnection = null;

        for (int intf = 0; intf < usbDevice.getInterfaceCount(); intf++) {
            if (usbDevice.getInterface(intf).getInterfaceClass() == 7) {
                usbInterface = usbDevice.getInterface(intf);
                break;
            }
        }

        if (usbInterface != null) {
            for (int ep = 0; ep < usbInterface.getEndpointCount(); ep++) {
                int dir = usbInterface.getEndpoint(ep).getDirection();
                if (usbInterface.getEndpoint(ep).getType() == UsbConstants.USB_ENDPOINT_XFER_BULK && dir == UsbConstants.USB_DIR_OUT) {
                    usbOut = usbInterface.getEndpoint(ep);
                } else if (usbInterface.getEndpoint(ep).getType() == UsbConstants.USB_ENDPOINT_XFER_BULK && dir == UsbConstants.USB_DIR_IN) {
                    usbIn = usbInterface.getEndpoint(ep);
                }
            }
        }

        if (usbIn != null && usbOut != null) {
            usbDeviceConnection = usbManager.openDevice(usbDevice);
        }

        if (usbDeviceConnection != null) {
            usbDeviceConnection.close();
            return new UsbPrinter(usbDevice, usbInterface, usbIn, usbOut, usbDeviceConnection);
        }

        return null;
    }

    public void unregisterReceiver() {
        Log.d(TAG, "unregisterReceiver");
        if (context != null) {
            context.unregisterReceiver(usbReceiver);
            context.unregisterReceiver(usbPermissionReceiver);
        }
    }
}
