package edu.flash3388.flashlib.robot.hid;

import edu.flash3388.flashlib.util.beans.BooleanProperty;

/**
 * An extension of {@link Button} for manually control.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.2
 */
public class ManualButton extends Button implements BooleanProperty {

	private boolean mIsDown;
	private boolean mIsInverted;

	public void setInverted(boolean isInverted) {
		mIsInverted = isInverted;
	}

	public boolean isInverted() {
		return mIsInverted;
	}

	@Override
	public boolean isDown() {
		return mIsDown ^ mIsInverted;
	}

	@Override
	public void set(boolean isDown) {
		mIsDown = isDown;
	}
}
