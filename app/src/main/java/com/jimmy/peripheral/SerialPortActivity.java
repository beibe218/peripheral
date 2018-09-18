package com.jimmy.peripheral;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;

import com.jimmy.serial.utils.SerialPort;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidParameterException;
import java.util.Timer;
import java.util.TimerTask;

public class SerialPortActivity extends AppCompatActivity {
    private boolean isConnected;
    private int mPathIndex;
    private Timer mTimer;

    private SerialPort mSerialPort;
    private InputStream mInputStream;
    private OutputStream mOutputStream;
    private Thread mReadThread;
    private EditText mReception;
    private Timer mHeartbeatTimer;

    private long mLastTimeMillis;

    private class ReadThread extends Thread {

        @Override
        public void run() {
            super.run();
            while (!isInterrupted()) {
                int size;
                try {
                    byte[] buffer = new byte[64];
                    if (mInputStream == null) return;
                    size = mInputStream.read(buffer);
                    if (size > 0) {
                        onDataReceived(buffer, size);
                        isConnected = true;
                        mLastTimeMillis = System.currentTimeMillis();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serial_port);
        mReception = findViewById(R.id.EditTextReception);
        openConnSerialPort();
        startConnStatusHeartbeat();
    }

    private void openConnSerialPort() {
        isConnected = false;
        mPathIndex = 0;
        final String[] pathArr = getResources().getStringArray(R.array.serial_port_path);
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (!isConnected && mPathIndex < pathArr.length - 1) {
                    connSerialPort(pathArr[mPathIndex]);
                    mPathIndex++;
                } else {
                    mTimer.cancel();
                }
            }
        }, 500, 500);
    }

    private void connSerialPort(String path) {
        try {
            mSerialPort = new SerialPort(new File(path), 9600, 0);
            mOutputStream = mSerialPort.getOutputStream();
            mInputStream = mSerialPort.getInputStream();

			/* Create a receiving thread */
            mReadThread = new ReadThread();
            mReadThread.start();
        } catch (SecurityException e) {
            //DisplayError(R.string.error_security);
        } catch (IOException e) {
            //DisplayError(R.string.error_unknown);
        } catch (InvalidParameterException e) {
            //DisplayError(R.string.error_configuration);
        }
    }

    protected void onDataReceived(final byte[] buffer, final int size) {
        runOnUiThread(new Runnable() {
            public void run() {
                if (mReception != null) {
                    String s = new String(buffer, 0, size);
                    Log.d("serialPort=", s);
                    mReception.append(s);
                }
            }
        });
    }

    private void startConnStatusHeartbeat() {
        mHeartbeatTimer = new Timer();
        mHeartbeatTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                long currentTimeMillis = System.currentTimeMillis();
                long intervalTimeMillis = currentTimeMillis - mLastTimeMillis;
                if (intervalTimeMillis > 1000) {
                    openConnSerialPort();
                }
            }
        }, 1000, 1000);
    }
}
