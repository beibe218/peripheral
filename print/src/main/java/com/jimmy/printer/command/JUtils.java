package com.jimmy.printer.command;

import java.util.Arrays;

/**
 * 类描述：
 * 创建人：jimmy.yang
 * 创建时间：2018-9-17 17:44
 * Email: jimmy.yang@keimai.cn
 * 修改备注：
 */

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
