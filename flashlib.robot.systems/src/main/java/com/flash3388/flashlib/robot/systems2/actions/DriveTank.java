package com.flash3388.flashlib.robot.systems2.actions;

import com.flash3388.flashlib.robot.systems.drive.TankDrive;
import com.flash3388.flashlib.robot.systems.drive.TankDriveSpeed;
import com.flash3388.flashlib.scheduling.actions.ActionBase;

import java.util.function.Supplier;

public class DriveTank extends ActionBase {

    private final TankDrive mDrive;
    private final Supplier<TankDriveSpeed> mSpeed;

    public DriveTank(TankDrive drive, Supplier<TankDriveSpeed> speed) {
        mDrive = drive;
        mSpeed = speed;
    }

    @Override
    public void execute() {
        mDrive.tankDrive(mSpeed.get());
    }

    @Override
    public void end(boolean wasInterrupted) {
        mDrive.stop();
    }
}
