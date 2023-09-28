package com.flash3388.flashlib.robot.systems.actions;

import com.flash3388.flashlib.robot.motion.MecanumDrive;
import com.flash3388.flashlib.robot.motion.MecanumDriveSpeed;
import com.flash3388.flashlib.scheduling.ActionControl;
import com.flash3388.flashlib.scheduling.FinishReason;
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
    public void initialize(ActionControl control) {

    }

    @Override
    public void execute(ActionControl control) {
        mDrive.mecanumDrive(mSpeed.get());
    }

    @Override
    public void end(FinishReason reason) {
        mDrive.stop();
    }
}
