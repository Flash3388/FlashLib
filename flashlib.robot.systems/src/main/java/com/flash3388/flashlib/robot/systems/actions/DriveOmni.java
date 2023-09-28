package com.flash3388.flashlib.robot.systems.actions;

import com.flash3388.flashlib.robot.motion.OmniDrive;
import com.flash3388.flashlib.robot.motion.OmniDriveSpeed;
import com.flash3388.flashlib.scheduling.ActionControl;
import com.flash3388.flashlib.scheduling.FinishReason;
import com.flash3388.flashlib.scheduling.actions.ActionBase;

import java.util.function.Supplier;

public class DriveOmni extends ActionBase {

    private final OmniDrive mDrive;
    private final Supplier<OmniDriveSpeed> mSpeed;

    public DriveOmni(OmniDrive drive, Supplier<OmniDriveSpeed> speed) {
        mDrive = drive;
        mSpeed = speed;
    }

    @Override
    public void initialize(ActionControl control) {

    }

    @Override
    public void execute(ActionControl control) {
        mDrive.omniDrive(mSpeed.get());
    }

    @Override
    public void end(FinishReason reason) {
        mDrive.stop();
    }
}
