package edu.flash3388.flashlib.robot.hid;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an Xbox 360 controller for use for robot control.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public class XboxController implements HID {
	
	private static final int BUTTON_COUNT = 10;

	private HIDInterface mHidInterface;

	private int mChannel;

	private List<Button> mButtons;
	
	public final Button A;
    public final Button B;
    public final Button X;
    public final Button Y;
    public final Button LB;
    public final Button RB;
    public final Button Back;
    public final Button Start;
    public final Button LeftStickButton;
    public final Button RightStickButton;
    
	public final Stick LeftStick;
	public final Stick RightStick;
	
	public final DPad DPad;
	
	public final Axis RT;
	public final Axis LT;
	
	/**
	 * Creates a new Xbox controller.
	 *
     * @param hidInterface the hid interface
	 * @param channel the device index
	 */
	public XboxController(HIDInterface hidInterface, int channel){
		mHidInterface = hidInterface;
		
		mChannel = channel;
		
		LeftStick = new Stick(this, 0, 1);
		RightStick = new Stick(this, 4, 5);
		
		DPad = new DPad(this, 0);
		RT = new Axis(this, 3);
		LT = new Axis(this, 2);

		mButtons = new ArrayList<Button>(BUTTON_COUNT);

		for(int i = 0; i < BUTTON_COUNT; i++) {
			mButtons.add(new HIDButton(this, i + 1));
		}
		
		A = getButton(0);
	    B = getButton(1);
	    X = getButton(2);
	    Y = getButton(3);
	    LB = getButton(4);
	    RB = getButton(5);
	    Back = getButton(6);
	    Start = getButton(7);
	    LeftStickButton = getButton(8);
	    RightStickButton = getButton(9);
	}
	
	/**
	 * Gets the left stick of the controller represented in a class
	 * @return The left stick on the controller
	 */
	public Stick getLeftStick() { return LeftStick; } 
	
	/**
	 * Gets the right stick of the controller represented in a class
	 * @return The right stick on the controller
	 */
	public Stick getRightStick() { return RightStick; }
	
	/**
	 * Gets the DPad buttons represented in a class
	 * @return The DPad on the controller 
	 */
	public DPad getDPad() { return DPad; }
	
	/**
	 * Gets the trigger representing the Xbox left trigger (LT)
	 * @return left trigger
	 */
	public Axis getLeftTrigger() { return LT; }
	
	/**
	 * Gets the trigger representing the Xbox right trigger (RT)
	 * @return right trigger
	 */
	public Axis getRightTrigger() { return RT; }
	
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
	    if (button < 0 || button >= mButtons.size()) {
	        throw new NoSuchButtonException(mChannel, button);
        }

		return mButtons.get(button);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override 
	public int getButtonCount(){
		return BUTTON_COUNT;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Stick getStick(int index) {
		switch(index){
			case 0: return RightStick;
			case 1: return LeftStick;
			default: throw new NoSuchStickException(mChannel, index);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public POV getPOV() {
		return getDPad();
	}
}

