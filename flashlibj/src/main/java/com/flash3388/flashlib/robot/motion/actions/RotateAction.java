package com.flash3388.flashlib.robot.motion.actions;

import com.flash3388.flashlib.robot.motion.Rotatable;
import com.flash3388.flashlib.robot.scheduling.actions.Action;

import java.util.function.DoubleSupplier;

public class RotateAction extends Action {

	private final Rotatable mRotatable;
	private final DoubleSupplier mSpeedSource;
	
	public RotateAction(Rotatable rotatable, DoubleSupplier speedSource) {
		this.mRotatable = rotatable;
		this.mSpeedSource = speedSource;
	}
	
	@Override
	protected void execute() {
		mRotatable.rotate(mSpeedSource.getAsDouble());
	}

	@Override
	protected void end() {
		mRotatable.stop();
	}
}
