package edu.flash3388.flashlib.robot.hid;

/**
 * An extension of {@link Button} for manually control.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.2
 */
public class ManualButton extends Button {

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

	public void setDown(boolean isDown) {
		mIsDown = isDown;
	}
}
