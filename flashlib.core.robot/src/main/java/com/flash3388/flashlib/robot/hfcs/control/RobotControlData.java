package com.flash3388.flashlib.robot.hfcs.control;

import com.flash3388.flashlib.io.Serializable;
import com.flash3388.flashlib.robot.modes.RobotMode;

import java.io.DataOutput;
import java.io.IOException;

public class RobotControlData implements Serializable {

    private final RobotMode mMode;

    public RobotControlData(RobotMode mode) {
        mMode = mode;
    }

    public RobotMode getMode() {
        return mMode;
    }

    @Override
    public void writeInto(DataOutput output) throws IOException {
        output.writeInt(mMode.getKey());
        output.writeUTF(mMode.getName());
        output.writeBoolean(mMode.isDisabled());
    }
}
