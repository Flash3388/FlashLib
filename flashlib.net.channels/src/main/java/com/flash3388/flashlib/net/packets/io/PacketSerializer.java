package com.flash3388.flashlib.net.packets.io;

import com.castle.util.closeables.Closeables;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class PacketSerializer {

    public static class Data implements Closeable {
        public final PacketHeader header;
        public final DataInputStream contentStream;

        private Data(PacketHeader header, DataInputStream contentStream) {
            this.header = header;
            this.contentStream = contentStream;
        }

        @Override
        public void close() {
            Closeables.silentClose(contentStream);
        }
    }

    private final byte[] mBytesBuffer;

    public PacketSerializer() {
        mBytesBuffer = new byte[1024];
    }

    public Data read(ByteBuffer buffer, int size) throws IOException {
        buffer.get(mBytesBuffer, 0, size);

        ByteArrayInputStream inputStream = new ByteArrayInputStream(mBytesBuffer);
        DataInputStream dataInputStream = new DataInputStream(inputStream);
        PacketHeader header = new PacketHeader(dataInputStream);
        return new Data(header, dataInputStream);
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
