package edu.flash3388.flashlib.communications.sendable;

import edu.flash3388.flashlib.communications.message.Message;
import edu.flash3388.flashlib.communications.message.StaticMessage;
import edu.flash3388.flashlib.io.PrimitiveSerializer;
import edu.flash3388.flashlib.util.ArrayUtil;

public class SendableData {

    private int mId;
    private int mType;

    public SendableData(int id, int type) {
        mId = id;
        mType = type;
    }

    public int getId() {
        return mId;
    }

    public int getType() {
        return mType;
    }

    public byte[] toBytes(PrimitiveSerializer serializer) {
        byte[] idData = serializer.toBytes(mId);
        byte[] typeData = serializer.toBytes(mType);

        return ArrayUtil.combine(idData, typeData);
    }

    public static SendableData fromBytes(byte[] bytes, PrimitiveSerializer serializer) {
        int id = serializer.toInt(bytes);
        int type = serializer.toInt(bytes, 4);

        return new SendableData(id, type);
    }

    public static int getSerializedSize() {
        return 8;
    }
}
