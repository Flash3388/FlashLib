package com.flash3388.flashlib.robot.motion.actions;

import com.beans.util.function.Suppliers;
import com.flash3388.flashlib.robot.motion.HolonomicDrive;
import com.flash3388.flashlib.robot.motion.HolonomicDriveSpeed;
import com.flash3388.flashlib.scheduling.ActionControl;
import com.flash3388.flashlib.scheduling.FinishReason;
import com.flash3388.flashlib.scheduling.actions.ActionBase;
import com.jmath.vectors.Vector2;

import java.util.function.DoubleSupplier;
import java.util.function.Supplier;

public class HolonomicDriveAction extends ActionBase {
	
	private final HolonomicDrive mDriveInterface;
	private final Supplier<? extends HolonomicDriveSpeed> mSpeedSupplier;

    public HolonomicDriveAction(HolonomicDrive driveInterface, Supplier<? extends HolonomicDriveSpeed> speedSupplier) {
        mDriveInterface = driveInterface;
        mSpeedSupplier = speedSupplier;

        requires(driveInterface);
    }

    public HolonomicDriveAction(HolonomicDrive driveInterface, HolonomicDriveSpeed driveSpeed) {
        this(driveInterface, Suppliers.of(driveSpeed));
    }

    public HolonomicDriveAction(HolonomicDrive driveInterface, Supplier<? extends Vector2> vectorSupplier, DoubleSupplier rotate) {
        this(driveInterface, ()->new HolonomicDriveSpeed(vectorSupplier.get(), rotate.getAsDouble()));
    }

    public HolonomicDriveAction(HolonomicDrive driveInterface, Vector2 vector, double rotate) {
        this(driveInterface, new HolonomicDriveSpeed(vector, rotate));
    }

    public static HolonomicDriveAction cartesian(HolonomicDrive driveInterface, DoubleSupplier y, DoubleSupplier x, DoubleSupplier rotate) {
        return new HolonomicDriveAction(driveInterface, ()->new HolonomicDriveSpeed(new Vector2(y.getAsDouble(), x.getAsDouble()), rotate.getAsDouble()));
    }

	public static HolonomicDriveAction polar(HolonomicDrive driveInterface, DoubleSupplier magnitude, DoubleSupplier direction, DoubleSupplier rotate) {
		return new HolonomicDriveAction(driveInterface, ()->new HolonomicDriveSpeed(Vector2.polar(magnitude.getAsDouble(), direction.getAsDouble()), rotate.getAsDouble()));
	}

    @Override
    public void initialize(ActionControl control) {

    }

    @Override
	public void execute(ActionControl control) {
		mDriveInterface.holonomicDrive(mSpeedSupplier.get());
	}

	@Override
    public void end(FinishReason reason) {
		mDriveInterface.stop();
	}
}
