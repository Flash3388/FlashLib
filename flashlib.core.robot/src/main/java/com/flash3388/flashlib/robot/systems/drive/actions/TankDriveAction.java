package com.flash3388.flashlib.robot.systems.drive.actions;

import com.beans.util.function.Suppliers;
import com.flash3388.flashlib.robot.systems.drive.TankDrive;
import com.flash3388.flashlib.robot.systems.drive.TankDriveSpeed;
import com.flash3388.flashlib.scheduling.ActionConfigurationEditor;
import com.flash3388.flashlib.scheduling.ActionControl;
import com.flash3388.flashlib.scheduling.ActionInterface;
import com.flash3388.flashlib.scheduling.FinishReason;

import java.util.function.DoubleSupplier;
import java.util.function.Supplier;

public class TankDriveAction implements ActionInterface {
	
	private final TankDrive mDriveInterface;
	private final Supplier<? extends TankDriveSpeed> mSpeedSupplier;

    public TankDriveAction(TankDrive driveInterface, Supplier<? extends TankDriveSpeed> speedSupplier) {
        mDriveInterface = driveInterface;
        mSpeedSupplier = speedSupplier;
    }

    public TankDriveAction(TankDrive driveInterface, TankDriveSpeed driveSpeed) {
        this(driveInterface, Suppliers.of(driveSpeed));
    }

	public TankDriveAction(TankDrive driveInterface, DoubleSupplier right, DoubleSupplier left) {
		this(driveInterface, ()->new TankDriveSpeed(right.getAsDouble(), left.getAsDouble()));
	}

    public TankDriveAction(TankDrive driveInterface, double right, double left) {
        this(driveInterface, new TankDriveSpeed(right, left));
    }

    public TankDriveAction(TankDrive driveInterface, double speed) {
        this(driveInterface, speed, speed);
    }

    @Override
    public void configure(ActionConfigurationEditor editor) {
        editor.addRequirements(mDriveInterface);
    }

    @Override
    public void execute(ActionControl control) {
        mDriveInterface.tankDrive(mSpeedSupplier.get());
    }

    @Override
    public void end(FinishReason reason) {
        mDriveInterface.stop();
    }
}
