package com.flash3388.flashlib.robot.hfcs.state;

import com.flash3388.flashlib.io.Serializable;

import java.io.DataOutput;
import java.io.IOException;

public class StateOutData implements Serializable {

    private final RobotStateData mData;

    public StateOutData(RobotStateData data) {
        mData = data;
    }

    @Override
    public void writeInto(DataOutput output) throws IOException {
        output.writeInt(mData.getCurrentMode().getKey());
        output.writeUTF(mData.getCurrentMode().getName());
        output.writeBoolean(mData.getCurrentMode().isDisabled());
        output.writeLong(mData.getClockTime().valueAsMillis());
    }
}
