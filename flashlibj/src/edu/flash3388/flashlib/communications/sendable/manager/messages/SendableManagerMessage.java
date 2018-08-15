package edu.flash3388.flashlib.communications.sendable.manager.messages;

import edu.flash3388.flashlib.communications.message.Message;
import edu.flash3388.flashlib.communications.sendable.SendableData;
import edu.flash3388.flashlib.io.packing.DataBufferPacker;
import edu.flash3388.flashlib.io.packing.DataBufferUnpacker;
import edu.flash3388.flashlib.io.packing.Packing;

import java.io.IOException;

public class SendableManagerMessage implements Message {

    private final int mHeader;
    private final SendableData mFrom;
    private final SendableData mTo;

    public SendableManagerMessage(int header, SendableData from, SendableData to) {
        mHeader = header;
        mFrom = from;
        mTo = to;
    }

    @Override
    public int getHeader() {
        return mHeader;
    }

    @Override
    public byte[] getData() {
        try {
            DataBufferPacker packer = Packing.newBufferPacker(SendableData.getSerializedSize() * 2);
            mFrom.pack(packer);
            mTo.pack(packer);

            packer.close();
            return packer.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public SendableData getFrom() {
        return mFrom;
    }

    public SendableData getTo() {
        return mTo;
    }

    public static SendableManagerMessage fromMessage(Message message) {
        int header = message.getHeader();
        byte[] allData = message.getData();

        try {
            DataBufferUnpacker unpacker = Packing.newBufferUnpacker(allData);

            SendableData from = SendableData.unpackObject(unpacker);
            SendableData to = SendableData.unpackObject(unpacker);

            return new SendableManagerMessage(header, from, to);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
