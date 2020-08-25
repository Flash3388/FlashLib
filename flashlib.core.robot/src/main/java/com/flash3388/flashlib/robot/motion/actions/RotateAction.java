package com.flash3388.flashlib.robot.motion.actions;

import com.beans.util.function.Suppliers;
import com.flash3388.flashlib.robot.motion.Rotatable;
import com.flash3388.flashlib.scheduling.actions.ActionBase;

import java.util.function.DoubleSupplier;

public class RotateAction extends ActionBase {

	private final Rotatable mRotatable;
	private final DoubleSupplier mSpeedSource;
	
	public RotateAction(Rotatable rotatable, DoubleSupplier speedSource) {
		this.mRotatable = rotatable;
		this.mSpeedSource = speedSource;
	}

    public RotateAction(Rotatable rotatable, double speed) {
	    this(rotatable, Suppliers.of(speed));
    }
	
	@Override
	public void execute() {
		mRotatable.rotate(mSpeedSource.getAsDouble());
	}

	@Override
	public void end(boolean wasInterrupted) {
		mRotatable.stop();
	}
}
