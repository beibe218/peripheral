package com.jimmy.peripheral;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.jimmy.printer.command.EscCommand;
import com.jimmy.printer.common.SendCallback;
import com.jimmy.printer.common.SendResultCode;
import com.jimmy.printer.ethernet.EthernetPrint;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EthernetPrinterActivity extends AppCompatActivity {

    private EditText printerIpEt;
    private EditText printerPortEt;
    private EthernetPrint ethernetPrint;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ethernet_printer_act);
        getSupportActionBar().setTitle(R.string.ethernet_printer);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        printerIpEt = findViewById(R.id.ethernet_printer_ip_et);
        printerPortEt = findViewById(R.id.ethernet_printer_port_et);

        ethernetPrint = EthernetPrint.getInstance(sendCallback);
        requestPermission();
    }

    private void requestPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.INTERNET},
                    1);
        }
    }

    SendCallback sendCallback = new SendCallback() {
        @Override
        public void onCallback(int code, String printId) {
            String msg = "";
            if (code == SendResultCode.SEND_SUCCESS) {
                msg = getString(R.string.send_success);
            } else if (code == SendResultCode.SEND_FAILED) {
                msg = getString(R.string.send_failed);
            }
            Toast.makeText(EthernetPrinterActivity.this, printId + " " + msg, Toast.LENGTH_LONG).show();
        }
    };

    public boolean isIP(String address) {
        if (address.length() < 7 || address.length() > 15 || "".equals(address)) {
            return false;
        }
        String regex = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";
        Pattern pat = Pattern.compile(regex);
        Matcher mat = pat.matcher(address);
        return mat.find();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void testPrint(View view) {
        String ip = printerIpEt.getText().toString().trim();
        String port = printerPortEt.getText().toString().trim();
        boolean isIp = isIP(ip);
        if (!isIp) {
            Toast.makeText(this, R.string.ip_format_error, Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(port)) {
            Toast.makeText(this, R.string.port_cannot_be_empty, Toast.LENGTH_LONG).show();
            return;
        }
        EscCommand esc = new EscCommand();
        for (int i = 0; i < 13; i++) {
            esc.addSelectJustification(0);
            esc.addText("This is test print\n");
            esc.addSelectJustification(1);
            esc.addText("This is test hehe\n");
            esc.addSelectJustification(2);
            esc.addText("This is test haha\n");
        }
        esc.addPrintAndFeedLines((byte) 5);
        esc.addCutPaper();
        ethernetPrint.sendPrintCommand(ip, Integer.parseInt(port), esc.getByteArrayCommand());
//        for (int i = 0; i < 10; i++) {
//        }
    }
}
