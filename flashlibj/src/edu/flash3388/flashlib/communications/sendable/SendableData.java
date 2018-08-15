package edu.flash3388.flashlib.communications.sendable;

import edu.flash3388.flashlib.io.packing.DataPacker;
import edu.flash3388.flashlib.io.packing.DataUnpacker;
import edu.flash3388.flashlib.io.packing.Packable;

import java.io.IOException;

public class SendableData implements Packable {

    private int mId;
    private int mType;

    public SendableData(int id, int type) {
        mId = id;
        mType = type;
    }

    private SendableData() {
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

    @Override
    public void pack(DataPacker packer) throws IOException {
        packer.packInt(mId);
        packer.packInt(mType);
    }

    @Override
    public void unpack(DataUnpacker unpacker) throws IOException {
        mId = unpacker.unpackInt();
        mType = unpacker.unpackInt();
    }

    public static SendableData unpackObject(DataUnpacker unpacker) throws IOException {
        SendableData sendableData = new SendableData();
        sendableData.unpack(unpacker);
        return sendableData;
    }

    public static int getSerializedSize() {
        return 8;
    }
}
