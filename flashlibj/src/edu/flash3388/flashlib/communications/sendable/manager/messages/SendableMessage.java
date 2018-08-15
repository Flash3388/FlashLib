package edu.flash3388.flashlib.communications.sendable.manager.messages;

import edu.flash3388.flashlib.communications.message.Message;
import edu.flash3388.flashlib.communications.message.StaticMessage;
import edu.flash3388.flashlib.communications.sendable.SendableData;
import edu.flash3388.flashlib.io.packing.DataBufferPacker;
import edu.flash3388.flashlib.io.packing.DataBufferUnpacker;
import edu.flash3388.flashlib.io.packing.Packing;

import java.io.IOException;

public class SendableMessage implements Message {

    public static final int HEADER = 1000;

    private final SendableData mFrom;
    private final SendableData mTo;
    private final Message mSendableMessage;

    public SendableMessage(SendableData from, SendableData to, Message sendableMessage) {
        mFrom = from;
        mTo = to;
        mSendableMessage = sendableMessage;
    }

    @Override
    public int getHeader() {
        return HEADER;
    }

    @Override
    public byte[] getData() {
        try {
            DataBufferPacker packer = Packing.newBufferPacker();

            mFrom.pack(packer);
            mTo.pack(packer);
            packer.packInt(mSendableMessage.getHeader());
            packer.packByteArray(mSendableMessage.getData());

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

    public Message getSendableMessage() {
        return mSendableMessage;
    }

    public static SendableMessage fromMessage(Message message) {
        byte[] allData = message.getData();

        try {
            DataBufferUnpacker unpacker = Packing.newBufferUnpacker(allData);

            SendableData from = SendableData.unpackObject(unpacker);
            SendableData to = SendableData.unpackObject(unpacker);
            int messageHeader = unpacker.unpackInt();
            byte[] messageData = unpacker.unpackByteArray();

            return new SendableMessage(from, to, new StaticMessage(messageHeader, messageData));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
