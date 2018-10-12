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

    public HidButton(HidInterface hidInterface, int channel, int button) {
        mHidInterface = hidInterface;
        mChannel = channel;
        mButton = button;
    }
	
	/**
	 * Gets the current button state
	 */
	@Override
	public boolean isDown() {
		return mHidInterface.getHidButton(mChannel, mButton);
	}
}
