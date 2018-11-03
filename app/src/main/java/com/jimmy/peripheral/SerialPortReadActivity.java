package com.jimmy.peripheral;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.jimmy.serial.ReadCallback;
import com.jimmy.serial.SerialPortRead;
import com.jimmy.serial.utils.SerialPortFinder;

public class SerialPortReadActivity extends AppCompatActivity {

    private String[] allDevicesPath;
    private SerialPortRead serialPortRead;
    private TextView pathTv;
    private TextView baudRateTv;
    private TextView readResultTv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serial_port_read);
        getSupportActionBar().setTitle(R.string.serial_port_read);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        try {
            SerialPortFinder serialPortFinder = new SerialPortFinder();
            allDevicesPath = serialPortFinder.getAllDevicesPath();
        } catch (Exception e) {
            Toast.makeText(this, R.string.get_local_serial_port_path_failed, Toast.LENGTH_LONG).show();
            allDevicesPath = getResources().getStringArray(R.array.serial_port_path);
        }

        serialPortRead = new SerialPortRead();
        pathTv = findViewById(R.id.path_tv);
        baudRateTv = findViewById(R.id.baud_rate_tv);
        readResultTv = findViewById(R.id.read_result_tv);
    }

    private ReadCallback readCallback = new ReadCallback() {
        @Override
        public void onReading(String s) {
            Log.d("serial port read = ", s);
            readResultTv.setText(s);
        }
    };

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

    private void open() {
        String path = pathTv.getText().toString();
        String baudRate = baudRateTv.getText().toString();
        if (TextUtils.isEmpty(baudRate) || TextUtils.isEmpty(path)) {
            return;
        }
        serialPortRead.open(path, Integer.parseInt(baudRate), readCallback);
    }

    public void testConnect(View view) {
        open();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (serialPortRead != null) {
            serialPortRead.close();
        }
    }
}
