package com.jimmy.printer.bluetooth;

import android.bluetooth.BluetoothDevice;

public class BluetoothPrinter {
    private BluetoothDevice device;

    public BluetoothPrinter(BluetoothDevice device) {
        this.device = device;
    }

    public BluetoothDevice getDevice() {
        return device;
    }

    public void setDevice(BluetoothDevice device) {
        this.device = device;
    }

    public String getName() {
        if (getDevice() != null) {
            return getDevice().getName();
        }
        return "null device";
    }

    public String getAddress() {
        if (getDevice() != null) {
            return getDevice().getAddress();
        }
        return "null address";
    }
}
