package com.flash3388.flashlib.robot.motion.actions;

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
	
	@Override
	public void execute() {
		mRotatable.rotate(mSpeedSource.getAsDouble());
	}

	@Override
	public void end(boolean wasInterrupted) {
		mRotatable.stop();
	}
}
