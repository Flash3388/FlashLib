package com.flash3388.flashlib.net.messaging.io;

import com.castle.util.function.ThrowingConsumer;
import com.castle.util.function.ThrowingFunction;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;

public class IoUtils {

    public static byte[] serialize(ThrowingConsumer<DataOutput, IOException> func) throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             DataOutputStream dataOutputStream = new DataOutputStream(outputStream)) {
            func.accept(dataOutputStream);
            dataOutputStream.flush();

            return outputStream.toByteArray();
        }
    }

    public static  <R> R deserialize(byte[] bytes, ThrowingFunction<DataInput, R, IOException> func) throws IOException {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
             DataInputStream dataInputStream = new DataInputStream(inputStream)) {
            return func.apply(dataInputStream);
        }
    }
}
