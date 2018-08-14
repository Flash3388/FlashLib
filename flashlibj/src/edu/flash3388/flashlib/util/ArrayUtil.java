package edu.flash3388.flashlib.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ArrayUtil {


    public static byte[] combine(byte[]... arrays) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        for (byte[] bytes : arrays) {
            try {
                byteArrayOutputStream.write(bytes);
            } catch (IOException e) {
                // won't be thrown from this implementation
            }
        }

        return byteArrayOutputStream.toByteArray();
    }
}
