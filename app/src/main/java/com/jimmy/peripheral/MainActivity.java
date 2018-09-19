package com.jimmy.peripheral;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
}
