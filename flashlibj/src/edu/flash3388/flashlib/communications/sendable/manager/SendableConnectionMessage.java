package edu.flash3388.flashlib.communications.sendable.manager;

import edu.flash3388.flashlib.communications.message.Message;
import edu.flash3388.flashlib.communications.sendable.SendableData;
import edu.flash3388.flashlib.io.PrimitiveSerializer;
import edu.flash3388.flashlib.util.ArrayUtil;

import java.util.Arrays;

class SendableConnectionMessage implements Message {

    private int mHeader;
    private SendableData mFrom;
    private SendableData mTo;
    private PrimitiveSerializer mSerializer;

    SendableConnectionMessage(int header, SendableData from, SendableData to, PrimitiveSerializer serializer) {
        mHeader = header;
        mFrom = from;
        mTo = to;
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

        return ArrayUtil.combine(fromData, toData);
    }

    public SendableData getFrom() {
        return mFrom;
    }

    public SendableData getTo() {
        return mTo;
    }

    public static SendableConnectionMessage fromMessage(Message message, PrimitiveSerializer serializer) {
        int header = message.getHeader();
        byte[] data = message.getData();

        int dataLength = SendableData.getSerializedSize();
        SendableData from = SendableData.fromBytes(data, serializer);
        byte[] toData = Arrays.copyOfRange(data, dataLength, data.length - dataLength);
        SendableData to = SendableData.fromBytes(toData, serializer);

        return new SendableConnectionMessage(header, from, to, serializer);
    }
}
