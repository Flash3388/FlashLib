package edu.flash3388.flashlib.communications.sendable.manager.messages;

import edu.flash3388.flashlib.communications.message.Message;
import edu.flash3388.flashlib.communications.sendable.SendableData;
import edu.flash3388.flashlib.io.PrimitiveSerializer;
import edu.flash3388.flashlib.util.ArrayUtil;

import java.util.Arrays;

public class SendableManagerMessage implements Message {

    private final int mHeader;
    private final SendableData mFrom;
    private final SendableData mTo;
    private final PrimitiveSerializer mSerializer;

    public SendableManagerMessage(int header, SendableData from, SendableData to, PrimitiveSerializer serializer) {
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

    public static SendableManagerMessage fromMessage(Message message, PrimitiveSerializer serializer) {
        int header = message.getHeader();
        byte[] allData = message.getData();

        int sendableDataSerializedSize = SendableData.getSerializedSize();

        SendableData from = SendableData.fromBytes(allData, serializer);
        byte[] toData = Arrays.copyOfRange(allData, sendableDataSerializedSize, sendableDataSerializedSize);
        SendableData to = SendableData.fromBytes(toData, serializer);

        return new SendableManagerMessage(header, from, to, serializer);
    }
}
