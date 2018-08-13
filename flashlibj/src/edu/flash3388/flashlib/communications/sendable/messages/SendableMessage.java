package edu.flash3388.flashlib.communications.sendable.messages;

import edu.flash3388.flashlib.communications.message.Message;
import edu.flash3388.flashlib.communications.message.StaticMessage;
import edu.flash3388.flashlib.communications.sendable.SendableData;
import edu.flash3388.flashlib.io.PrimitiveSerializer;
import edu.flash3388.flashlib.util.ArrayUtil;

import java.util.Arrays;

public class SendableMessage implements Message {

    public static final int HEADER = 1000;

    private PrimitiveSerializer mSerializer;

    private SendableData mFrom;
    private SendableData mTo;
    private Message mSendableMessage;

    public SendableMessage(SendableData from, SendableData to, Message sendableMessage, PrimitiveSerializer serializer) {
        mFrom = from;
        mTo = to;
        mSendableMessage = sendableMessage;
        mSerializer = serializer;
    }

    @Override
    public int getHeader() {
        return HEADER;
    }

    @Override
    public byte[] getData() {
        byte[] fromData = mFrom.toBytes(mSerializer);
        byte[] toData = mTo.toBytes(mSerializer);
        byte[] sendableMessageHeader = mSerializer.toBytes(mSendableMessage.getHeader());
        byte[] sendableMessageData = mSendableMessage.getData();

        return ArrayUtil.combine(fromData, toData, sendableMessageHeader, sendableMessageData);
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

    public static SendableMessage fromMessage(Message message, PrimitiveSerializer serializer) {
        byte[] allData = message.getData();

        int sendableDataSerializedSize = SendableData.getSerializedSize();

        SendableData from = SendableData.fromBytes(allData, serializer);
        byte[] toData = Arrays.copyOfRange(allData, sendableDataSerializedSize, sendableDataSerializedSize);
        SendableData to = SendableData.fromBytes(toData, serializer);

        int sendableMessageHeader = serializer.toInt(allData, sendableDataSerializedSize * 2);
        byte[] sendableMessageData = Arrays.copyOfRange(allData, sendableDataSerializedSize * 2 + 4, allData.length - sendableDataSerializedSize * 2 + 4);

        return new SendableMessage(from, to, new StaticMessage(sendableMessageHeader, sendableMessageData), serializer);
    }
}
