package com.flash3388.flashlib.robot.systems.actions;

import com.flash3388.flashlib.robot.motion.HolonomicDrive;
import com.flash3388.flashlib.robot.motion.HolonomicDriveSpeed;
import com.flash3388.flashlib.scheduling.ActionControl;
import com.flash3388.flashlib.scheduling.FinishReason;
import com.flash3388.flashlib.scheduling.actions.ActionBase;

import java.util.function.Supplier;

public class DriveHolonomic extends ActionBase {

    private final HolonomicDrive mDrive;
    private final Supplier<HolonomicDriveSpeed> mSpeed;

    public DriveHolonomic(HolonomicDrive drive, Supplier<HolonomicDriveSpeed> speed) {
        mDrive = drive;
        mSpeed = speed;
    }

    @Override
    public void execute(ActionControl control) {
        mDrive.holonomicDrive(mSpeed.get());
    }

    @Override
    public void end(FinishReason reason) {
        mDrive.stop();
    }
}
