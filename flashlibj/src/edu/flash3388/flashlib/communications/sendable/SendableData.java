package edu.flash3388.flashlib.communications.sendable;

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

    @Override
    public int hashCode() {
        return mId;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SendableData) {
            return ((SendableData) obj).mId == mId;
        }

        return false;
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
