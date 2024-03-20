package com.flash3388.flashlib.robot.hfcs.state;

import com.flash3388.flashlib.io.Serializable;
import com.flash3388.flashlib.robot.modes.RobotMode;
import com.flash3388.flashlib.time.Time;

import java.io.DataOutput;
import java.io.IOException;

public class RobotStateData implements Serializable {

    private final RobotMode mCurrentMode;
    private final Time mClockTime;

    public RobotStateData(RobotMode currentMode, Time clockTime) {
        mCurrentMode = currentMode;
        mClockTime = clockTime;
    }

    public RobotMode getCurrentMode() {
        return mCurrentMode;
    }

    public Time getClockTime() {
        return mClockTime;
    }

    @Override
    public void writeInto(DataOutput output) throws IOException {
        output.writeInt(mCurrentMode.getKey());
        output.writeUTF(mCurrentMode.getName());
        output.writeBoolean(mCurrentMode.isDisabled());
        output.writeLong(mClockTime.valueAsMillis());
    }
}
