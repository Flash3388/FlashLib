package com.flash3388.flashlib.robot.systems2.actions;

import com.flash3388.flashlib.robot.systems.drive.HolonomicDrive;
import com.flash3388.flashlib.robot.systems.drive.HolonomicDriveSpeed;
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
    public void execute() {
        mDrive.holonomicDrive(mSpeed.get());
    }

    @Override
    public void end(boolean wasInterrupted) {
        mDrive.stop();
    }
}
