package com.jimmy.peripheral;

import android.content.Context;
import android.content.Intent;
import android.hardware.display.DisplayManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView deviceInfoTv = findViewById(R.id.device_info);
        String deviceInfoText = getDeviceInfoText();
        deviceInfoTv.setText(deviceInfoText);
    }

    private String getDeviceInfoText() {
        StringBuilder info = new StringBuilder();
        String manufacturer = Build.MANUFACTURER;
        String brand = Build.BRAND;
        String model = Build.MODEL;

        info.append("制造商：").append(manufacturer).append("\n")
                .append("品牌：").append(brand).append("\n")
                .append("型号：").append(model).append("\n").append("\n");

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        info.append("主屏宽度：").append(displayMetrics.widthPixels).append("\n")
                .append("主屏高度：").append(displayMetrics.heightPixels).append("\n")
                .append("主屏dpi：").append(displayMetrics.densityDpi).append("\n").append("\n");

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
            DisplayManager displayManager = (DisplayManager) getSystemService(Context.DISPLAY_SERVICE);
            if (displayManager != null) {
                Display[] displays = displayManager.getDisplays();
                if (displays.length > 1) {
                    Display display = displays[1];
                    DisplayMetrics metrics = new DisplayMetrics();
                    display.getMetrics(metrics);
                    info.append("副屏宽度：").append(metrics.widthPixels).append("\n")
                            .append("副屏高度：").append(metrics.heightPixels).append("\n")
                            .append("副屏dpi：").append(metrics.densityDpi);
                }
            }
        }
        return info.toString();
    }

    public void toSerialPortAct(View view) {
        Intent intent = new Intent(this, SerialPortReadActivity.class);
        startActivity(intent);
    }

    public void toMoveViewAct(View view) {
        Intent intent = new Intent(this, MoveViewActivity.class);
        startActivity(intent);
    }

    public void toUsbPrinterAct(View view) {
        Intent intent = new Intent(this, UsbPrinterActivity.class);
        startActivity(intent);
    }

    public void toEthernetPrinterAct(View view) {
        Intent intent = new Intent(this, EthernetPrinterActivity.class);
        startActivity(intent);
    }

    public void toBluetoothPrinterAct(View view) {
        Intent intent = new Intent(this, BluetoothPrinterActivity.class);
        startActivity(intent);
    }

    public void toSerialPortWriteAct(View view) {
        Intent intent = new Intent(this, SerialPortWriteActivity.class);
        startActivity(intent);
    }
}
