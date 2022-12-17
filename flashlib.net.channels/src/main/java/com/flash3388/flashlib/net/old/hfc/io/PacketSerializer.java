package com.flash3388.flashlib.net.old.hfc.io;

import com.flash3388.flashlib.net.hfc.Packet;
import com.flash3388.flashlib.net.hfc.PacketInfo;
import com.flash3388.flashlib.net.hfc.PacketType;
import com.flash3388.flashlib.net.old.hfc.data.KnownPacketTypes;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.NoSuchElementException;

public class PacketSerializer {

    public static class Data {
        public final PacketHeader header;
        public final Packet packet;

        private Data(PacketHeader header, Packet packet) {
            this.header = header;
            this.packet = packet;
        }
    }

    private final KnownPacketTypes mPacketTypes;
    private final byte[] mBytesBuffer;

    public PacketSerializer(KnownPacketTypes packetTypes) {
        mPacketTypes = packetTypes;
        mBytesBuffer = new byte[1024];
    }

    public Data read(ByteBuffer buffer, int size) throws IOException {
        buffer.get(mBytesBuffer, 0, size);

        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(mBytesBuffer);
            DataInputStream dataInputStream = new DataInputStream(inputStream)) {
            PacketHeader header = new PacketHeader(dataInputStream);
            try {
                PacketType type = mPacketTypes.get(header.getContentType());
                Packet packet = type.create();
                packet.readFrom(dataInputStream);

                return new Data(header, packet);
            }  catch (NoSuchElementException e) {
                throw new IOException("unknown type: " + header.getContentType());
            }
        }
    }

    public ByteBuffer write(PacketHeader header, Packet packet) throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             DataOutputStream dataOutputStream = new DataOutputStream(outputStream)) {
            header.writeTo(dataOutputStream);
            packet.writeInto(dataOutputStream);

            dataOutputStream.flush();

            return ByteBuffer.wrap(outputStream.toByteArray());
        }
    }
}
