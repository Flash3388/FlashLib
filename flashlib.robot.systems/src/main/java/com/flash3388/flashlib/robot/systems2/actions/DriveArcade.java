package com.flash3388.flashlib.robot.systems2.actions;

import com.flash3388.flashlib.robot.systems.drive.ArcadeDriveSpeed;
import com.flash3388.flashlib.robot.systems.drive.TankDrive;
import com.flash3388.flashlib.scheduling.actions.ActionBase;

import java.util.function.Supplier;

public class DriveArcade extends ActionBase {

    private final TankDrive mDrive;
    private final Supplier<ArcadeDriveSpeed> mSpeed;

    public DriveArcade(TankDrive drive, Supplier<ArcadeDriveSpeed> speed) {
        mDrive = drive;
        mSpeed = speed;
    }

    @Override
    public void execute() {
        mDrive.arcadeDrive(mSpeed.get());
    }

    @Override
    public void end(boolean wasInterrupted) {
        mDrive.stop();
    }
}
