package com.flash3388.flashlib.robot.systems2.actions;

import com.flash3388.flashlib.robot.systems.drive.OmniDrive;
import com.flash3388.flashlib.robot.systems.drive.OmniDriveSpeed;
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
    public void execute() {
        mDrive.omniDrive(mSpeed.get());
    }

    @Override
    public void end(boolean wasInterrupted) {
        mDrive.stop();
    }
}
