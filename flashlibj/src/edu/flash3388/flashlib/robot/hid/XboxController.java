package edu.flash3388.flashlib.robot.hid;

import edu.flash3388.flashlib.flashboard.HIDSendable;
import edu.flash3388.flashlib.robot.RobotFactory;

/**
 * Represents an Xbox 360 controller for use for robot control.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public class XboxController extends HIDSendable implements HID, Runnable{
	
	//private static String[] buttonNames = {("A"),("B"),("X"),("Y"),("LB"),("RB"),("Back"),("Start"),("LStick"),("RStick")};
	
	private int channel;
	private Button[] buttons = new Button[10];
	
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
	
	public final Trigger RightTrigger;
	public final Trigger LeftTrigger;
	
	private XboxController next;
	
	private static XboxController head;
	
	/**
	 * Creates a new Xbox controller.
	 * 
	 * @param channel the device index
	 */
	public XboxController(int channel){
		this(channel, "XBox "+ channel);
	}
	/**
	 * Creates a new Xbox controller.
	 * 
	 * @param channel the device index
	 * @param name the name of the controller
	 */
	public XboxController(int channel, String name){
		super(name);
		this.channel = channel;
		
		LeftStick = new Stick(channel, 0, 1);
		RightStick = new Stick(channel, 4, 5);
		
		DPad = new DPad(channel, 0);
		RightTrigger = new Trigger(channel, 3);
		LeftTrigger = new Trigger(channel, 2);
		
		for(int i = 0; i < buttons.length; i++)
    		buttons[i] = new Button(channel, i+1);//buttonNames[i]
		
		A = buttons[0];
	    B = buttons[1];
	    X = buttons[2];
	    Y = buttons[3];
	    LB = buttons[4];
	    RB = buttons[5];
	    Back = buttons[6];
	    Start = buttons[7];
	    LeftStickButton = buttons[8];
	    RightStickButton = buttons[9];
	    
	    next = head;
	    head = this;
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
	public Trigger getLeftTrigger() { return LeftTrigger; }
	
	/**
	 * Gets the trigger representing the Xbox right trigger (RT)
	 * @return right trigger
	 */
	public Trigger getRightTrigger() { return RightTrigger; }
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public double getRawAxis(int axis){
		return RobotFactory.getImplementation().getHIDInterface().getHIDAxis(channel, axis);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean getRawButton(int button){
		return RobotFactory.getImplementation().getHIDInterface().getHIDButton(channel, button);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Button getButton(int button) {
		return buttons[button - 1];
	}
	/**
	 * {@inheritDoc}
	 */
	@Override 
	public int getButtonCount(){
		return 10;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Stick getStick(int index) {
		switch(index){
			case 0: return RightStick;
			case 1: return LeftStick;
			default: return null;
		}
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Stick getStick() {
		return getStick(0);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public POV getPOV() {
		return getDPad();
	}
	
	/**
	 * Refreshes the value of the button wrappers. Used to determine whether or not to run 
	 * actions attached to those wrapped. 
	 */
	public void refresh(){
		for(int i = 0; i < buttons.length; i++)
			buttons[i].refresh();
		DPad.refresh();
	}
	
	/**
	 * Refreshes all created xbox controllers.
	 */
	public static void refreshAll(){
		for(XboxController c = head; c != null; c = c.next)
			c.refresh();
	}
	
	@Override
	public void run() {
		refresh();
	}
	@Override
	protected HID getHID() {
		return this;
	}
}

