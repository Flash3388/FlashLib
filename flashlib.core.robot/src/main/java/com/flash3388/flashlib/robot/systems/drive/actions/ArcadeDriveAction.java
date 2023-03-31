package com.flash3388.flashlib.robot.systems.drive.actions;

import com.beans.util.function.Suppliers;
import com.flash3388.flashlib.robot.systems.drive.ArcadeDriveSpeed;
import com.flash3388.flashlib.robot.systems.drive.TankDrive;
import com.flash3388.flashlib.scheduling.ActionConfigurationEditor;
import com.flash3388.flashlib.scheduling.ActionControl;
import com.flash3388.flashlib.scheduling.ActionInterface;
import com.flash3388.flashlib.scheduling.FinishReason;

import java.util.function.DoubleSupplier;
import java.util.function.Supplier;


public class ArcadeDriveAction implements ActionInterface {
	
	private final TankDrive mDriveInterface;
	private final Supplier<? extends ArcadeDriveSpeed> mSpeedSupplier;

    public ArcadeDriveAction(TankDrive driveInterface, Supplier<? extends ArcadeDriveSpeed> speedSupplier) {
        mDriveInterface = driveInterface;
        mSpeedSupplier = speedSupplier;
    }

	public ArcadeDriveAction(TankDrive driveInterface, DoubleSupplier move, DoubleSupplier rotate) {
		this(driveInterface, ()->new ArcadeDriveSpeed(move.getAsDouble(), rotate.getAsDouble()));
	}

    public ArcadeDriveAction(TankDrive driveInterface, ArcadeDriveSpeed driveSpeed) {
        this(driveInterface, Suppliers.of(driveSpeed));
    }

    public ArcadeDriveAction(TankDrive driveInterface, double move, double rotate) {
        this(driveInterface, new ArcadeDriveSpeed(move, rotate));
    }

	@Override
	public void configure(ActionConfigurationEditor editor) {
		editor.addRequirements(mDriveInterface);
	}

	@Override
	public void execute(ActionControl control) {
		mDriveInterface.arcadeDrive(mSpeedSupplier.get());
	}

	@Override
	public void end(FinishReason reason) {
		mDriveInterface.stop();
	}
}
