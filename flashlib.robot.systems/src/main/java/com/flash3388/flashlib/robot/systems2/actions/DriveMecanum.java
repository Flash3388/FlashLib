package com.flash3388.flashlib.robot.systems2.actions;

import com.flash3388.flashlib.robot.systems.drive.MecanumDrive;
import com.flash3388.flashlib.robot.systems.drive.MecanumDriveSpeed;
import com.flash3388.flashlib.scheduling.actions.ActionBase;

import java.util.function.Supplier;

public class DriveMecanum extends ActionBase {

    private final MecanumDrive mDrive;
    private final Supplier<MecanumDriveSpeed> mSpeed;

    public DriveMecanum(MecanumDrive drive, Supplier<MecanumDriveSpeed> speed) {
        mDrive = drive;
        mSpeed = speed;
    }

    @Override
    public void execute() {
        mDrive.mecanumDrive(mSpeed.get());
    }

    @Override
    public void end(boolean wasInterrupted) {
        mDrive.stop();
    }
}
