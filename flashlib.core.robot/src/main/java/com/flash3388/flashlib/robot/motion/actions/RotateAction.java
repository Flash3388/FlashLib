package com.flash3388.flashlib.robot.motion.actions;

import com.beans.util.function.Suppliers;
import com.flash3388.flashlib.control.Direction;
import com.flash3388.flashlib.robot.motion.Rotatable;
import com.flash3388.flashlib.scheduling.ActionControl;
import com.flash3388.flashlib.scheduling.FinishReason;
import com.flash3388.flashlib.scheduling.actions.ActionBase;

import java.util.function.DoubleSupplier;

public class RotateAction extends ActionBase {

	private final Rotatable mRotatable;
	private final DoubleSupplier mSpeedSource;
	
	public RotateAction(Rotatable rotatable, DoubleSupplier speedSource) {
		this.mRotatable = rotatable;
		this.mSpeedSource = speedSource;

		requires(rotatable);
	}

    public RotateAction(Rotatable rotatable, double speed) {
	    this(rotatable, Suppliers.of(speed));
    }

    public RotateAction(Rotatable rotatable, double speed, Direction direction) {
        this(rotatable, speed * direction.sign());
    }

	@Override
	public void initialize(ActionControl control) {

	}

	@Override
	public void execute(ActionControl control) {
		mRotatable.rotate(mSpeedSource.getAsDouble());
	}

	@Override
	public void end(FinishReason reason) {
		mRotatable.stop();
	}
}
