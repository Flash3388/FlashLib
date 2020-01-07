package com.flash3388.flashlib.robot.hid;

import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.time.Time;

import java.util.Arrays;
import java.util.Collections;

/**
 * Represents a D-Pad from and XBox controller.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public class DPad extends Pov {

	private final Button mUp;
    private final Button mDown;
    private final Button mRight;
    private final Button mLeft;
    private final Button mFull;

	public DPad(Clock clock, Time buttonPressTime, HidInterface hidInterface, int channel, int pov){
		super(hidInterface, channel, pov);
		
		mUp = new PovButton(clock, buttonPressTime, hidInterface, channel, pov, PovRange.UP);
		mDown = new PovButton(clock, buttonPressTime, hidInterface, channel, pov, PovRange.DOWN);
		mRight = new PovButton(clock, buttonPressTime, hidInterface, channel, pov, PovRange.RIGHT);
		mLeft = new PovButton(clock, buttonPressTime, hidInterface, channel, pov, PovRange.LEFT);
		mFull = new PovButton(clock, buttonPressTime, hidInterface, channel, pov, PovRange.FULL);
	}

	public DPad(Clock clock, HidInterface hidInterface, int channel, int pov) {
	    this(clock, HardwareButton.DEFAULT_MAX_PRESS_TIME, hidInterface, channel, pov);
    }

	/**
	 * Gets the up DPad button object
	 * @return up button
	 */
	public Button getUp(){
		return mUp;
	}

	/**
	 * Gets the down DPad button object
	 * @return down button
	 */
	public Button getDown(){
		return mDown;
	}

	/**
	 * Gets the right DPad button object
	 * @return right button
	 */
	public Button getRight(){
		return mRight;
	}

	/**
	 * Gets the left DPad button object
	 * @return left button
	 */
	public Button getLeft(){
		return mLeft;
	}

	public Button getFull() {
		return mFull;
	}

    public Iterable<Button> buttons() {
        return Collections.unmodifiableCollection(Arrays.asList(mUp, mDown, mRight, mLeft, mFull));
    }
}
