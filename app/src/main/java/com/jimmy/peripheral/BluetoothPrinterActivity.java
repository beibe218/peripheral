package com.jimmy.peripheral;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jimmy.printer.bluetooth.BluetoothPrint;
import com.jimmy.printer.bluetooth.BluetoothPrinter;
import com.jimmy.printer.command.EscCommand;
import com.jimmy.printer.common.SendCallback;
import com.jimmy.printer.common.SendResultCode;
import com.jimmy.printer.ethernet.EthernetPrint;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class BluetoothPrinterActivity extends AppCompatActivity {
    public static String TAG = "BluetoothPrinterActivity";
    private static final int REQUEST_BLUETOOTH_ENABLE = 1;
    private ProgressBar loadingPb;
    private ListAdapter listAdapter;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothPrint bluetoothPrint;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bluetooth_printer_act);
        getSupportActionBar().setTitle(R.string.bluetooth_printer);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loadingPb = findViewById(R.id.bluetooth_loading_pb);
        ListView listView = findViewById(R.id.bluetooth_list);
        listAdapter = new ListAdapter();
        listView.setAdapter(listAdapter);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(this, R.string.device_does_not_support_bluetooth, Toast.LENGTH_LONG).show();
        } else {
            if (!bluetoothAdapter.isEnabled()) {
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(intent, REQUEST_BLUETOOTH_ENABLE);
            } else {
                loadingPb.setVisibility(View.VISIBLE);
                bluetoothAdapter.startDiscovery();
            }
        }

        bluetoothPrint = BluetoothPrint.getInstance(sendCallback);

        IntentFilter bluetoothFilter = new IntentFilter();
        bluetoothFilter.addAction(BluetoothDevice.ACTION_FOUND);
        bluetoothFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(bluetoothReceiver, bluetoothFilter);

        requestPermission();
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
            Toast.makeText(BluetoothPrinterActivity.this, printId + " " + msg, Toast.LENGTH_LONG).show();
        }
    };

    private void requestPermission() {
        int local = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        if (local != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(bluetoothReceiver);
    }

    private BroadcastReceiver bluetoothReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                listAdapter.addData(new BluetoothPrinter(bluetoothDevice));
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                loadingPb.setVisibility(View.INVISIBLE);
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_BLUETOOTH_ENABLE) {
            if (resultCode == RESULT_OK) {
                loadingPb.setVisibility(View.VISIBLE);
                bluetoothAdapter.startDiscovery();
                Log.d(TAG, "open success");
            } else {
                Log.d(TAG, "open failed");
            }
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

        List<BluetoothPrinter> printers;

        public ListAdapter() {
            this.printers = new ArrayList<>();
        }

        public void addData(BluetoothPrinter printer) {
            printers.add(printer);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return printers == null ? 0 : printers.size();
        }

        @Override
        public BluetoothPrinter getItem(int position) {
            return printers.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater
                        .from(BluetoothPrinterActivity.this)
                        .inflate(R.layout.bluetooth_printer_item_layout, parent, false);
            }
            final BluetoothPrinter item = getItem(position);
            TextView nameTv = convertView.findViewById(R.id.bluetooth_device_name_tv);
            nameTv.setText(String.format("%s\n%s", item.getName(), item.getAddress()));
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BluetoothDevice device = item.getDevice();
                    int bondState = device.getBondState();
                    if (bondState == BluetoothDevice.BOND_BONDED) {
                        showTestPrintDialog(device);
                    } else if (bondState == BluetoothDevice.BOND_NONE) {
                        try {
                            Method creMethod = BluetoothDevice.class.getMethod("createBond");
                            creMethod.invoke(device);
                        } catch (NoSuchMethodException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }
                    Log.d(TAG, "device bondState=" + bondState);
                }
            });
            return convertView;
        }
    }

    private void showTestPrintDialog(final BluetoothDevice device) {
        new AlertDialog.Builder(this)
                .setTitle(device.getName())
                .setMessage(R.string.bluetooth_device_bonded_print_test_data)
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        printTestText(device);
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private void printTestText(BluetoothDevice device) {
        EscCommand esc = new EscCommand();
        esc.addText("This is test print\n");
        esc.addText("This is test hehe\n");
        esc.addText("This is test haha\n");
        esc.addText("This is test xixi\n");
        esc.addPrintAndFeedLines((byte) 8);
        esc.addCutPaper();
        //esc.addCleanCache();
        bluetoothPrint.sendPrintCommand(device, esc.getByteArrayCommand());
    }

}
