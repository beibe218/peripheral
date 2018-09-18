package com.jimmy.printer.command;

import java.io.UnsupportedEncodingException;
import java.util.Vector;

public class EscCommand {

    private Vector<Byte> command;

    public EscCommand() {
        this.command = new Vector<>(4096, 1024);
    }

    private void addArrayToCommand(byte[] array) {
        for (byte anArray : array) {
            this.command.add(anArray);
        }
    }

    private void addStrToCommand(String str) {
        byte[] bs = null;
        if (!str.equals("")) {
            try {
                bs = str.getBytes("GB2312");
            } catch (UnsupportedEncodingException var4) {
                var4.printStackTrace();
            }

            if (bs != null) {
                for (byte b : bs) {
                    this.command.add(b);
                }
            }
        }
    }

    /**
     * 添加文本
     *
     * @param text 文本
     */
    public void addText(String text) {
        this.addStrToCommand(text);
    }

    /**
     * 添加空行
     *
     * @param n 行数
     */
    public void addPrintAndFeedLines(byte n) {
        byte[] command = new byte[]{27, 100, n};
        this.addArrayToCommand(command);
    }

    /**
     * 切纸命令
     */
    public void addCutPaper() {
        byte[] bytes = new byte[]{(byte) 29, (byte) 86, (byte) 0};
        this.addArrayToCommand(bytes);
    }

    public void addCleanCache() {
        byte[] bytes = {(byte) 27, (byte) 74, (byte) 0};
        this.addArrayToCommand(bytes);
    }

    //0 居左 1居中 2居右
    public void addSelectJustification(int just) {
        byte[] command = new byte[]{27, 97, (byte) just};
        this.addArrayToCommand(command);
    }

    /**
     * 获取打印命令
     *
     * @return byte[] 打印命令
     */
    public byte[] getByteArrayCommand() {
        return convertToByteArray(getCommand());
    }

    public Vector<Byte> getCommand() {
        return this.command;
    }

    private byte[] convertToByteArray(Vector<Byte> vector) {
        if (vector == null || vector.isEmpty())
            return new byte[0];

        Byte[] bytes = vector.toArray(new Byte[vector.size()]);
        return toPrimitive(bytes);
    }

    private byte[] toPrimitive(Byte[] array) {
        if (array == null) {
            return null;
        } else if (array.length == 0) {
            return new byte[0];
        } else {
            byte[] result = new byte[array.length];
            for (int i = 0; i < array.length; ++i) {
                result[i] = array[i];
            }
            return result;
        }
    }
}
