package com.flash3388.flashlib.robot.motion.actions;

import com.flash3388.flashlib.robot.scheduling.Action;
import com.flash3388.flashlib.robot.scheduling.Subsystem;
import com.flash3388.flashlib.robot.motion.Rotatable;

import java.util.function.DoubleSupplier;

public class RotateAction extends Action {

	private final Rotatable mRotatable;
	private final DoubleSupplier mSpeedSource;
	
	public RotateAction(Rotatable rotatable, DoubleSupplier speedSource) {
		this.mRotatable = rotatable;
		this.mSpeedSource = speedSource;
		
		if(rotatable instanceof Subsystem) {
			requires((Subsystem) rotatable);
		}
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