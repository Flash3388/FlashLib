package com.flash3388.flashlib.robot.hfcs.state;

import com.flash3388.flashlib.net.hfcs.HfcsInType;
import com.flash3388.flashlib.robot.modes.RobotMode;
import com.flash3388.flashlib.time.Time;

import java.io.DataInput;
import java.io.IOException;

public class StateDataInType extends StateDataType implements HfcsInType<RobotStateData> {

    @Override
    public RobotStateData readFrom(DataInput input) throws IOException {
        int modeKey = input.readInt();
        String modeName = input.readUTF();
        boolean modeDisabled = input.readBoolean();
        RobotMode mode = RobotMode.create(modeName, modeKey, modeDisabled);

        long timeMillis = input.readLong();
        Time clockTime = Time.milliseconds(timeMillis);

        return new RobotStateData(mode, clockTime);
    }
}
