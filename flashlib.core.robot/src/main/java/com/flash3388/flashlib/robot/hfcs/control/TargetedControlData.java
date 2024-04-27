package com.flash3388.flashlib.robot.hfcs.control;

import com.flash3388.flashlib.io.Serializable;
import com.flash3388.flashlib.util.unique.InstanceId;

import java.io.DataOutput;
import java.io.IOException;

public class TargetedControlData implements Serializable {

    private final InstanceId mTargetId;
    private final RobotControlData mData;

    public TargetedControlData(InstanceId targetId, RobotControlData data) {
        mTargetId = targetId;
        mData = data;
    }

    public InstanceId getTargetId() {
        return mTargetId;
    }

    public RobotControlData getData() {
        return mData;
    }

    @Override
    public void writeInto(DataOutput output) throws IOException {
        mTargetId.writeTo(output);
        mData.writeInto(output);
    }
}
