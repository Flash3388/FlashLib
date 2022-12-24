package com.flash3388.flashlib.robot.hfcs.control;

import com.flash3388.flashlib.net.hfcs.OutData;
import com.flash3388.flashlib.robot.modes.RobotMode;

import java.io.DataOutput;
import java.io.IOException;

public class ControlData implements OutData {

    private final RobotMode mMode;

    public ControlData(RobotMode mode) {
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
