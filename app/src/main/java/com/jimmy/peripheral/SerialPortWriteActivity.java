package com.jimmy.peripheral;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.jimmy.serial.SerialPortWrite;
import com.jimmy.serial.utils.SerialPortFinder;

public class SerialPortWriteActivity extends AppCompatActivity {

    private String[] allDevicesPath;
    private TextView pathTv;
    private TextView baudRateTv;
    private SerialPortWrite serialPortWrite;
    private EditText testDataEt;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serial_port_write);

        SerialPortFinder serialPortFinder = new SerialPortFinder();
        allDevicesPath = serialPortFinder.getAllDevicesPath();
        serialPortWrite = new SerialPortWrite();

        pathTv = findViewById(R.id.path_tv);
        baudRateTv = findViewById(R.id.baud_rate_tv);
        testDataEt = findViewById(R.id.test_data_et);
    }

    public void showSerialPortPathDialog(View view) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.serial_port_path)
                .setItems(allDevicesPath, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        pathTv.setText(allDevicesPath[which]);
                    }
                })
                .show();
    }

    public void showBaudRateDialog(View view) {
        final String[] baudRates = new String[]{"2400", "9600"};
        new AlertDialog.Builder(this)
                .setTitle(R.string.serial_port_path)
                .setItems(baudRates, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        baudRateTv.setText(baudRates[which]);
                    }
                })
                .show();
    }

    public void open(View view) {
        String path = pathTv.getText().toString();
        String baudRate = baudRateTv.getText().toString();
        if (TextUtils.isEmpty(baudRate) || TextUtils.isEmpty(path)) {
            return;
        }
        serialPortWrite.open(path, Integer.parseInt(baudRate));
    }

    public void write(View view) {
        if (serialPortWrite == null)
            return;
        String s = testDataEt.getText().toString();
        serialPortWrite.write(s, SerialPortWrite.WriteType.TYPE_CHARGE);
    }
}
