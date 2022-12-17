package com.flash3388.flashlib.net.robolink.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class PacketSerializer {

    public static class Data {
        public final PacketHeader header;
        public final byte[] content;

        private Data(PacketHeader header, byte[] content) {
            this.header = header;
            this.content = content;
        }
    }

    private final byte[] mBytesBuffer;

    public PacketSerializer() {
        mBytesBuffer = new byte[1024];
    }

    public Data read(ByteBuffer buffer, int size) throws IOException {
        buffer.get(mBytesBuffer, 0, size);

        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(mBytesBuffer);
             DataInputStream dataInputStream = new DataInputStream(inputStream)) {
            PacketHeader header = new PacketHeader(dataInputStream);
            byte[] content = new byte[header.getContentSize()];
            //noinspection ResultOfMethodCallIgnored
            dataInputStream.read(content);

            return new Data(header, content);
        }
    }

    public ByteBuffer write(PacketHeader header, byte[] content) throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             DataOutputStream dataOutputStream = new DataOutputStream(outputStream)) {
            header.writeTo(dataOutputStream);
            dataOutputStream.write(content);
            dataOutputStream.flush();

            return ByteBuffer.wrap(outputStream.toByteArray());
        }
    }
}
