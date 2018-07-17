package edu.flash3388.flashlib.communications.sendable.manager;

import edu.flash3388.flashlib.communications.message.Message;
import edu.flash3388.flashlib.communications.message.StaticMessage;
import edu.flash3388.flashlib.communications.sendable.SendableData;
import edu.flash3388.flashlib.io.PrimitiveSerializer;
import edu.flash3388.flashlib.util.ArrayUtil;

import java.util.Arrays;

class SendableMessage implements Message {

    private PrimitiveSerializer mSerializer;

    private int mHeader;
    private SendableData mFrom;
    private SendableData mTo;
    private Message mSendableMessage;

    SendableMessage(int header, SendableData from, SendableData to, Message sendableMessage, PrimitiveSerializer serializer) {
        mHeader = header;
        mFrom = from;
        mTo = to;
        mSendableMessage = sendableMessage;
        mSerializer = serializer;
    }

    @Override
    public int getHeader() {
        return mHeader;
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

    static SendableMessage fromMessage(Message message, PrimitiveSerializer serializer) {
        int header = message.getHeader();
        byte[] allData = message.getData();

        int sendableDataSerializedSize = SendableData.getSerializedSize();

        SendableData from = SendableData.fromBytes(allData, serializer);
        byte[] toData = Arrays.copyOfRange(allData, sendableDataSerializedSize, sendableDataSerializedSize);
        SendableData to = SendableData.fromBytes(toData, serializer);

        int sendableMessageHeader = serializer.toInt(allData, sendableDataSerializedSize * 2);
        byte[] sendableMessageData = Arrays.copyOfRange(allData, sendableDataSerializedSize * 2 + 4, allData.length - sendableDataSerializedSize * 2 + 4);

        return new SendableMessage(header, from, to, new StaticMessage(sendableMessageHeader, sendableMessageData),
                serializer);
    }
}
