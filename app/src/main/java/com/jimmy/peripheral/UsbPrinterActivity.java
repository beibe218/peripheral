package com.jimmy.peripheral;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jimmy.printer.command.EscCommand;
import com.jimmy.printer.common.PrinterFinderCallback;
import com.jimmy.printer.common.SendCallback;
import com.jimmy.printer.common.SendResultCode;
import com.jimmy.printer.ethernet.EthernetPrint;
import com.jimmy.printer.usb.UsbPrinter;
import com.jimmy.printer.usb.UsbPrinterFinder;
import com.jimmy.printer.usb.UsbPrint;

import java.util.ArrayList;
import java.util.List;

public class UsbPrinterActivity extends AppCompatActivity {
    private static final String TAG = "UsbPrinterActivity";
    private ListAdapter listAdapter;
    private UsbPrint usbPrint;
    private UsbPrinterFinder printerFinder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_printer);
        getSupportActionBar().setTitle(R.string.usb_printer);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ListView listView = findViewById(R.id.printer_list);
        listAdapter = new ListAdapter();
        listView.setAdapter(listAdapter);

        usbPrint = UsbPrint.getInstance(this, sendCallback);

        printerFinder = new UsbPrinterFinder(this, printerFinderCallback);
        printerFinder.startFinder();
    }

    private SendCallback sendCallback = new SendCallback() {
        @Override
        public void onCallback(int code, String printId) {
            String msg = "";
            if (code == SendResultCode.SEND_SUCCESS) {
                msg = getString(R.string.send_success);
            } else if (code == SendResultCode.SEND_FAILED) {
                msg = getString(R.string.send_failed);
            }
            Toast.makeText(UsbPrinterActivity.this, printId + " " + msg, Toast.LENGTH_LONG).show();
        }
    };

    private PrinterFinderCallback<UsbPrinter> printerFinderCallback = new PrinterFinderCallback<UsbPrinter>() {
        @Override
        public void onStart() {
            Log.d(TAG, "startFind print");
        }

        @Override
        public void onFound(UsbPrinter usbPrinter) {
            //listAdapter.addData(usbPrinter);
            Log.d(TAG, "onFound deviceName = " + usbPrinter.getPrinterName());
        }

        @Override
        public void onFinished(List<UsbPrinter> usbPrinters) {
            Log.d(TAG, "printCount = " + usbPrinters.size());
            listAdapter.replaceDatas(usbPrinters);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (printerFinder != null) {
            printerFinder.unregisterReceiver();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private class ListAdapter extends BaseAdapter {

        List<UsbPrinter> printers;

        public ListAdapter() {
            printers = new ArrayList<>();
        }

        public void replaceDatas(List<UsbPrinter> datas) {
            printers = datas;
            notifyDataSetChanged();
        }

        public void addData(UsbPrinter usbPrinter) {
            printers.add(usbPrinter);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return printers == null ? 0 : printers.size();
        }

        @Override
        public UsbPrinter getItem(int i) {
            return printers.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = LayoutInflater.from(UsbPrinterActivity.this).inflate(R.layout.printer_item_layout, viewGroup, false);
            }
            final UsbPrinter item = getItem(i);
            TextView printerName = view.findViewById(R.id.printerName);
            printerName.setText(item.getPrinterName());

            Button testPrint = view.findViewById(R.id.testPrint);
            testPrint.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (usbPrint != null) {
                        printTestText(item);
                    }
                }
            });
            return view;
        }
    }

    private void printTestText(UsbPrinter printer) {
        EscCommand esc = new EscCommand();
        esc.addText("This is test print\n");
        esc.addText("This is test hehe\n");
        esc.addText("This is test haha\n");
        esc.addText("This is test xixi\n");
        esc.addPrintAndFeedLines((byte) 8);
        esc.addCutPaper();
        //esc.addCleanCache();
        usbPrint.sendPrintCommand(printer, esc.getByteArrayCommand());
    }

}
