package edu.flash3388.flashlib.robot.hid;

/**
 * An extension of {@link Button} for human interface devices. Provides time buffering for activation types.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.2
 */
public class HIDButton extends Button {

	private final HIDInterface mHidInterface;
	private final int mChannel;
	private final int mButton;

    public HIDButton(HIDInterface hidInterface, int channel, int button) {
        mHidInterface = hidInterface;
        mChannel = channel;
        mButton = button;
    }


    public HIDInterface getHidInterface(){
        return mHidInterface;
    }

    public int getChannel() {
        return mChannel;
    }

    public int getButtonNumber() {
        return mButton;
    }
	
	/**
	 * Gets the current button state
	 */
	@Override
	public boolean isDown() {
		return mHidInterface.getHidButton(mChannel, mButton);
	}
}
