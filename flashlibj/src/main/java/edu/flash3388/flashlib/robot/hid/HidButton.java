package edu.flash3388.flashlib.robot.hid;

/**
 * An extension of {@link Button} for human interface devices. Provides time buffering for activation types.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.2
 */
public class HidButton extends Button {

	private final HidInterface mHidInterface;
	private final int mChannel;
	private final int mButton;

	private boolean mIsInverted;

    public HidButton(HidInterface hidInterface, int channel, int button) {
        mHidInterface = hidInterface;
        mChannel = channel;
        mButton = button;

        mIsInverted = false;
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
