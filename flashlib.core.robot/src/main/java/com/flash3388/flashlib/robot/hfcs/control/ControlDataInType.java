package com.flash3388.flashlib.robot.hfcs.control;

import com.flash3388.flashlib.net.hfcs.InType;
import com.flash3388.flashlib.robot.modes.RobotMode;

import java.io.DataInput;
import java.io.IOException;

public class ControlDataInType extends ControlDataType implements InType<RobotControlData> {

    @Override
    public Class<RobotControlData> getClassType() {
        return RobotControlData.class;
    }

    @Override
    public RobotControlData readFrom(DataInput input) throws IOException {
        int modeKey = input.readInt();
        String modeName = input.readUTF();
        boolean modeDisabled = input.readBoolean();
        RobotMode mode = RobotMode.create(modeName, modeKey, modeDisabled);

        return new RobotControlData(mode);
    }
}
