package com.jimmy.serial;

import com.jimmy.serial.utils.SerialPort;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

import static com.jimmy.serial.SerialPortWrite.WriteType.TYPE_CHARGE;
import static com.jimmy.serial.SerialPortWrite.WriteType.TYPE_CLEAR;
import static com.jimmy.serial.SerialPortWrite.WriteType.TYPE_TOTAL;

public class SerialPortWrite {
    private SerialPort serialPort;
    private OutputStream os;

    public void open(String path, int baudRate) {
        try {
            serialPort = new SerialPort(new File(path), baudRate, 0);
            os = serialPort.getOutputStream();
        } catch (SecurityException e) {
            //DisplayError(R.string.error_security);
        } catch (IOException e) {
            //DisplayError(R.string.error_unknown);
        } catch (InvalidParameterException e) {
            //DisplayError(R.string.error_configuration);
        }
    }

    public void write(String price, WriteType type) {
        try {
            if (os == null) return;
            int[] command = getCommand(price, type);
            byte[] bytes = new byte[command.length];
            for (int i = 0; i < command.length; i++) {
                bytes[i] = (byte) command[i];
            }
            os.write(bytes);
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            if (os != null) {
                os.close();
            }
            if (serialPort != null) {
                serialPort.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int[] getCommand(String price, WriteType type) {
        List<Integer> commands = new ArrayList<>();
        //找零
        if (type == TYPE_CHARGE) {
            commands.add(0X1B);
            commands.add(0X73);
            commands.add(0X34);
            commands.add(0X1B);
            commands.add(0X51);
            commands.add(0X41);
            //总计
        } else if (type == TYPE_TOTAL) {
            commands.add(0X1B);
            commands.add(0X73);
            commands.add(0X32);
            commands.add(0X1B);
            commands.add(0X51);
            commands.add(0X41);
            //全暗
        } else if (type == TYPE_CLEAR) {
            commands.add(0X1B);
            commands.add(0X73);
            commands.add(0X30);
            commands.add(0X1B);
            commands.add(0X51);
            commands.add(0X41);
            for (int i = 0; i < 7; i++) {
                commands.add(0X20);
            }
            commands.add(0x0D);
            int[] intArray = new int[commands.size()];
            for (int i = 0; i < intArray.length; i++) {
                intArray[i] = commands.get(i);
            }

            return intArray;
        }

        String str = price;
        for (int i = 0; i < str.length(); i++) {
            String temp = str.substring(i, i + 1);
            if (temp.equals(".")) {
                commands.add(0X2E);
            }
            if (temp.equals("0")) {
                commands.add(0X30);
            }
            if (temp.equals("1")) {
                commands.add(0X31);
            }
            if (temp.equals("2")) {
                commands.add(0X32);
            }
            if (temp.equals("3")) {
                commands.add(0X33);
            }
            if (temp.equals("4")) {
                commands.add(0X34);
            }
            if (temp.equals("5")) {
                commands.add(0X35);
            }
            if (temp.equals("6")) {
                commands.add(0X36);
            }
            if (temp.equals("7")) {
                commands.add(0X37);
            }
            if (temp.equals("8")) {
                commands.add(0X38);
            }
            if (temp.equals("9")) {
                commands.add(0X39);
            }

        }
        for (int i = 0; i < 8 - str.length(); i++) {
            commands.add(0X20);
        }
        commands.add(0X0D);

        int[] intArray = new int[commands.size()];
        for (int i = 0; i < intArray.length; i++) {
            intArray[i] = commands.get(i);
        }

        return intArray;
    }

    public enum WriteType {
        TYPE_CHARGE,
        TYPE_TOTAL,
        TYPE_CLEAR,
    }
}
