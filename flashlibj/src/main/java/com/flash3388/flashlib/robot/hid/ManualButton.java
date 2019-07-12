package com.flash3388.flashlib.robot.hid;

import com.beans.BooleanProperty;

/**
 * An extension of {@link Button} for manually control.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.2
 */
public class ManualButton extends SoftwareButton implements BooleanProperty {

	private boolean mIsDown;
	private boolean mIsInverted;

    @Override
	public void setInverted(boolean isInverted) {
		mIsInverted = isInverted;
	}

	@Override
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

    @Override
    public void setAsBoolean(boolean value) {
        setDown(value);
    }

    @Override
    public Boolean get() {
        return isDown();
    }

    @Override
    public void set(Boolean value) {
	    setDown(value != null ? value : false);
    }
}
