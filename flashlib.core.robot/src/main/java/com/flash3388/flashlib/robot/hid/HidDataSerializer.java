package com.flash3388.flashlib.robot.hid;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.ByteBuffer;

public class HidDataSerializer {

    private final ByteBuffer mBuffer;

    public HidDataSerializer() {
        mBuffer = ByteBuffer.allocate(1024);
    }

    public void loadInto(DataInput input, RawHidData data) throws IOException {
        // channel types
        mBuffer.clear();
        input.readFully(mBuffer.array(), 0, RawHidData.MAX_HID * Integer.BYTES);
        mBuffer.asIntBuffer().get(data.channelTypes, 0, RawHidData.MAX_HID);

        // channel content
        mBuffer.clear();
        input.readFully(mBuffer.array(), 0, RawHidData.MAX_HID * Integer.BYTES);
        mBuffer.asIntBuffer().get(data.channelContents, 0, RawHidData.MAX_HID);

        // axes
        mBuffer.clear();
        input.readFully(mBuffer.array(), 0, RawHidData.MAX_HID * RawHidData.MAX_AXES * Short.BYTES);
        mBuffer.asShortBuffer().get(data.axes, 0, RawHidData.MAX_HID * RawHidData.MAX_AXES);

        // buttons
        mBuffer.clear();
        input.readFully(mBuffer.array(), 0, RawHidData.MAX_HID * Short.BYTES);
        mBuffer.asShortBuffer().get(data.buttons, 0, RawHidData.MAX_HID);

        // povs
        mBuffer.clear();
        input.readFully(mBuffer.array(), 0, RawHidData.MAX_HID * RawHidData.MAX_POVS * Short.BYTES);
        mBuffer.asShortBuffer().get(data.povs, 0, RawHidData.MAX_HID * RawHidData.MAX_POVS);
    }

    public void writeFrom(DataOutput output, RawHidData data) throws IOException {
        mBuffer.clear();

        // channel types
        int pos = mBuffer.asIntBuffer().put(data.channelTypes)
                .position() * Integer.BYTES;
        mBuffer.flip();
        output.write(mBuffer.array(), 0, pos);

        // channel content
        mBuffer.clear();
        pos = mBuffer.asIntBuffer().put(data.channelContents)
                .position() * Integer.BYTES;
        mBuffer.flip();
        output.write(mBuffer.array(), 0, pos);

        // axes
        mBuffer.clear();
        pos = mBuffer.asShortBuffer().put(data.axes)
                .position() * Short.BYTES;
        mBuffer.flip();
        output.write(mBuffer.array(), 0, pos);

        // buttons
        mBuffer.clear();
        pos = mBuffer.asShortBuffer().put(data.buttons)
                .position() * Short.BYTES;
        mBuffer.flip();
        output.write(mBuffer.array(), 0, pos);

        // povs
        mBuffer.clear();
        pos = mBuffer.asShortBuffer().put(data.povs)
                .position() * Short.BYTES;
        mBuffer.flip();
        output.write(mBuffer.array(), 0, pos);
    }
}
