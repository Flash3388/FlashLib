package edu.flash3388.flashlib.robot.hid;

import edu.flash3388.flashlib.robot.RobotFactory;
import edu.flash3388.flashlib.robot.ScheduledTask;
import edu.flash3388.flashlib.robot.flashboard.HIDSendable;

/**
 * Represents an Xbox 360 controller for use in FRC robot code
 * This is a wrapper class for an Xbox 360 controller
 * @author Tom Tzook
 */
public class XboxController extends HIDSendable implements HID, ScheduledTask{
	
	private static String[] buttonNames = {("A"),("B"),("X"),("Y"),("LB"),("RB"),("Back"),("Start"),("LStick"),("RStick")};
	
	private int channel;
	private Button[] buttons = new Button[10];
	
	/**
	 * The A button on the controller.
	 */
	public final Button A;
	/**
	 * The B button on the controller.
	 */
    public final Button B;
    /**
	 * The X button on the controller.
	 */
    public final Button X;
    /**
	 * The Y button on the controller.
	 */
    public final Button Y;
    /**
	 * The LB button on the controller.
	 */
    public final Button LB;
    /**
	 * The RB button on the controller.
	 */
    public final Button RB;
    /**
	 * The Back button on the controller.
	 */
    public final Button Back;
    /**
	 * The Start button on the controller.
	 */
    public final Button Start;
    /**
	 * The left stick button on the controller.
	 */
    public final Button LeftStickButton;
    /**
	 * The right stick button on the controller.
	 */
    public final Button RightStickButton;
	
	/**
	 * The left stick of the controller
	 */
	public final Stick LeftStick;
	/**
	 * The right stick of the controller
	 */
	public final Stick RightStick;
	/**
	 * The DPad buttons represented in an instance of <code>POV<code> class
	 */
	public final DPad DPad;
	/**
	 * The triggers (RT, LT) represented in an instance of <code>Triggers<code> class
	 */
	public final Triggers Triggers;
	
	private XboxController next;
	
	private static XboxController head;
	
	public XboxController(int channel){
		this(channel, "XBox "+ channel);
	}
	public XboxController(int channel, String name){
		super(name);
		this.channel = channel;
		
		LeftStick = RobotFactory.createStick(channel, 0, 1);
		RightStick = RobotFactory.createStick(channel, 4, 5);
		
		DPad = RobotFactory.createDpad(channel);
		Triggers = new Triggers(channel, 2, 3);
		
		for(int i = 0; i < buttons.length; i++)
    		buttons[i] = RobotFactory.createButton(buttonNames[i], channel, i+1);
		
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
	 * Gets the triggers represented in a class (RT, LT)
	 * @return The triggers on the controller
	 */
	public Triggers getTriggers() { return Triggers; }
	
	@Override
	public double getRawAxis(int axis){
		return RobotFactory.getStickAxis(channel, axis);
	}
	@Override
	public boolean getRawButton(int button){
		return RobotFactory.getStickButton(channel, (byte)button);
	}
	@Override
	public Button getButton(int button) {
		return buttons[button - 1];
	}
	@Override 
	public int getButtonCount(){
		return 10;
	}
	@Override
	public Stick getStick(int index) {
		switch(index){
			case 0: return RightStick;
			case 1: return LeftStick;
			default: return null;
		}
	}
	@Override
	public Stick getStick() {
		return getStick(0);
	}
	@Override
	public DPad getPOV() {
		return getDPad();
	}
	
	public void addListener(ButtonListener listener){
		for(Button b : buttons)
			b.addListener(listener);
	}
	
	public void refresh(){
		for(Button b : buttons)
			b.set(getRawButton(b.getNumber()));
		DPad.refresh();
		Triggers.refresh();
	}
	public static void refreshAll(){
		for(XboxController c = head; c != null; c = c.next)
			c.refresh();
	}
	@Override
	public boolean run() {
		refresh();
		return true;
	}
	@Override
	protected HID getHID() {
		return this;
	}
}

