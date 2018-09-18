package com.jimmy.printer.usb;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.text.TextUtils;

public class UsbPrinter {
    private UsbDevice usbDevice;
    private UsbInterface usbInterface;
    private UsbEndpoint usbIn;
    private UsbEndpoint usbOut;
    private UsbDeviceConnection usbDeviceConnection;

    public UsbPrinter(UsbDevice usbDevice,
                      UsbInterface usbInterface,
                      UsbEndpoint usbIn,
                      UsbEndpoint usbOut,
                      UsbDeviceConnection usbDeviceConnection
    ) {
        this.usbDevice = usbDevice;
        this.usbInterface = usbInterface;
        this.usbIn = usbIn;
        this.usbOut = usbOut;
        this.usbDeviceConnection = usbDeviceConnection;
    }

    public UsbDevice getUsbDevice() {
        return usbDevice;
    }

    public void setUsbDevice(UsbDevice usbDevice) {
        this.usbDevice = usbDevice;
    }

    public UsbInterface getUsbInterface() {
        return usbInterface;
    }

    public void setUsbInterface(UsbInterface usbInterface) {
        this.usbInterface = usbInterface;
    }

    public UsbEndpoint getUsbIn() {
        return usbIn;
    }

    public void setUsbIn(UsbEndpoint usbIn) {
        this.usbIn = usbIn;
    }

    public UsbEndpoint getUsbOut() {
        return usbOut;
    }

    public void setUsbOut(UsbEndpoint usbOut) {
        this.usbOut = usbOut;
    }

    public UsbDeviceConnection getUsbDeviceConnection() {
        return usbDeviceConnection;
    }

    public void setUsbDeviceConnection(UsbDeviceConnection usbDeviceConnection) {
        this.usbDeviceConnection = usbDeviceConnection;
    }

    public String getPrinterName() {
        UsbDevice usbDevice = getUsbDevice();
        if (usbDevice == null) {
            return "null usbDevice";
        }

        StringBuilder nameSb = new StringBuilder();
        String manufacturerName = null;
        String productName = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            manufacturerName = usbDevice.getManufacturerName();
            productName = usbDevice.getProductName();
        }
        if (!TextUtils.isEmpty(manufacturerName)) {
            nameSb.append(manufacturerName).append(" ");
        }
        if (!TextUtils.isEmpty(productName)) {
            nameSb.append(productName);
        }
        return nameSb.toString();
    }
}
