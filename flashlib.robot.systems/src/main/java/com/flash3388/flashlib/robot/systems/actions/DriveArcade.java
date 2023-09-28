package com.flash3388.flashlib.robot.systems.actions;

import com.flash3388.flashlib.robot.motion.ArcadeDriveSpeed;
import com.flash3388.flashlib.robot.motion.TankDrive;
import com.flash3388.flashlib.scheduling.ActionControl;
import com.flash3388.flashlib.scheduling.FinishReason;
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
    public void initialize(ActionControl control) {

    }

    @Override
    public void execute(ActionControl control) {
        mDrive.arcadeDrive(mSpeed.get());
    }

    @Override
    public void end(FinishReason reason) {
        mDrive.stop();
    }
}
