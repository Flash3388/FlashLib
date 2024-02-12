package com.flash3388.flashlib.robot.hfcs.control;

import com.flash3388.flashlib.net.hfcs.HfcsInType;
import com.flash3388.flashlib.robot.modes.RobotMode;
import com.flash3388.flashlib.util.unique.InstanceId;

import java.io.DataInput;
import java.io.IOException;

public class ControlDataInType extends ControlDataType implements HfcsInType<TargetedControlData> {

    @Override
    public TargetedControlData readFrom(DataInput input) throws IOException {
        InstanceId targetId = InstanceId.createFrom(input);

        int modeKey = input.readInt();
        String modeName = input.readUTF();
        boolean modeDisabled = input.readBoolean();
        RobotMode mode = RobotMode.create(modeName, modeKey, modeDisabled);

        return new TargetedControlData(targetId, new RobotControlData(mode));
    }
}
