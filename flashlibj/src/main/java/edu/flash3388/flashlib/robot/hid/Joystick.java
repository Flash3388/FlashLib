package edu.flash3388.flashlib.robot.hid;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple joystick device used for robot control. 
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public class Joystick implements HID {

	private static final int X = 0;
	private static final int Y = 1;
	private static final int Z = 2;
	private static final int THROTTLE = 3;

	private HIDInterface mHidInterface;

	private int mChannel;

	private POV mPov;
	private Stick mStick;
	private List<Button> mButtons;
	
	/**
	 * Creates a new joystick device at an index with a given amount of buttons.
	 *
     * @param hidInterface the hid interface
	 * @param channel the channel
	 * @param buttonCount the amount of buttons
	 */
	public Joystick(HIDInterface hidInterface, int channel, int buttonCount){
		mHidInterface = hidInterface;

		mChannel = channel;

		mStick = new Stick(this, X, Y);

		mButtons = new ArrayList<Button>();
		for(int i = 0; i < buttonCount; i++) {
			mButtons.add(new HIDButton(this, i + 1));
		}

		mPov = new POV(this, 0);
	}
	
	/**
	 * Gets the x-axis value of the joystick
	 * @return the x axis value
	 */
	public double getX(){
		return getRawAxis(X);
	}

	/**
	 * Gets the y-axis value of the joystick
	 * @return the y axis value
	 */
	public double getY(){
		return getRawAxis(Y);
	}

	/**
	 * Gets the z-axis value of the joystick
	 * @return the z axis value
	 */
	public double getZ(){
		return getRawAxis(Z);
	}

	/**
	 * Gets the throttle axis value of the joystick
	 * @return the throttle axis value
	 */
	public double getThrottle(){
		return getRawAxis(THROTTLE);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getChannel(){
		return mChannel;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double getRawAxis(int axis){
		return mHidInterface.getHidAxis(mChannel, axis);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean getRawButton(int button){
		return mHidInterface.getHidButton(mChannel, button);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getRawPov(int pov){
		return mHidInterface.getHidPov(mChannel, pov);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Button getButton(int button) {
		return mButtons.get(button);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Since only one stick exists, indexes other than 0 returns null.
	 * </p>
	 */
	@Override
	public Stick getStick(int index) {
		switch(index){
			case 0: return mStick;
			default: throw new NoSuchStickException(mChannel, index);
		}
	}

	public Stick getStick() {
		return mStick;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getButtonCount(){
		return mButtons.size();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public POV getPOV(){
		return mPov;
	}
}
