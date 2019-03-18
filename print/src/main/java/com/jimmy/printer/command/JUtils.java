package com.jimmy.printer.command;

import java.util.Arrays;

public class JUtils {

    public static byte[][] split_bytes(byte[] bytes, int copies) {

        double split_length = Double.parseDouble(copies + "");

        int array_length = (int) Math.ceil(bytes.length / split_length);
        byte[][] result = new byte[array_length][];

        int from, to;

        for (int i = 0; i < array_length; i++) {

            from = (int) (i * split_length);
            to = (int) (from + split_length);

            if (to > bytes.length)
                to = bytes.length;

            result[i] = Arrays.copyOfRange(bytes, from, to);
        }

        return result;
    }
}
