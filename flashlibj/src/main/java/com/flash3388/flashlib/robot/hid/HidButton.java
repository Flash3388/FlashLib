package com.flash3388.flashlib.robot.hid;

import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.time.Time;

/**
 * An extension of {@link Button} for human interface devices. Provides time buffering for activation types.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.2
 */
public class HidButton extends HardwareButton {

	private final HidInterface mHidInterface;
	private final int mChannel;
	private final int mButton;

	private boolean mIsInverted;

    public HidButton(Clock clock, Time maxPressTime, HidInterface hidInterface, int channel, int button) {
        super(clock, maxPressTime);

        mHidInterface = hidInterface;
        mChannel = channel;
        mButton = button;

        mIsInverted = false;
    }

    public HidButton(Clock clock, HidInterface hidInterface, int channel, int button) {
        this(clock, DEFAULT_MAX_PRESS_TIME, hidInterface, channel, button);
    }
	
	/**
	 * Gets the current button state
	 */
	@Override
	public boolean isDown() {
		return mHidInterface.getHidButton(mChannel, mButton) ^ mIsInverted;
	}

    @Override
    public void setInverted(boolean inverted) {
        mIsInverted = inverted;
    }

    @Override
    public boolean isInverted() {
        return mIsInverted;
    }
}
