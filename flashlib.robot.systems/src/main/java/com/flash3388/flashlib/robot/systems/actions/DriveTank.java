package com.flash3388.flashlib.robot.systems.actions;

import com.flash3388.flashlib.robot.motion.TankDrive;
import com.flash3388.flashlib.robot.motion.TankDriveSpeed;
import com.flash3388.flashlib.scheduling.ActionControl;
import com.flash3388.flashlib.scheduling.FinishReason;
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
    public void initialize(ActionControl control) {

    }

    @Override
    public void execute(ActionControl control) {
        mDrive.tankDrive(mSpeed.get());
    }

    @Override
    public void end(FinishReason reason) {
        mDrive.stop();
    }
}
