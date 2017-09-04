package edu.flash3388.flashlib.robot.hid;

import edu.flash3388.flashlib.flashboard.HIDSendable;
import edu.flash3388.flashlib.robot.RobotFactory;

/**
 * A simple joystick device used for robot control. 
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public class Joystick extends HIDSendable implements HID, Runnable{

	private static Joystick head;
	private Joystick next;
	
	private static final int X = 0, Y = 1, Z = 2, THROTTLE = 3;

	private Button[] buttons;
	private int stick_num;
	private POV pov;
	private Stick stick;
	
	/**
	 * Creates a new joystick device at an index with a given amount of buttons.
	 * 
	 * @param name the name of the joystick
	 * @param stick the stick index
	 * @param buttonCount the amount of buttons
	 */
	public Joystick(String name, int stick, int buttonCount){
		super(name);
		stick_num = stick;
		
		this.stick = new Stick(this, X, Y);
		buttons = new Button[buttonCount];
		for(int i = 0; i < buttons.length; i++)
			buttons[i] = new HIDButton(this, i+1);
		pov = new POV(this, 0);
		
		next = head;
		head = this;
	}
	/**
	 * Creates a new joystick device at an index with a given amount of buttons.
	 * 
	 * @param stick the stick index
	 * @param buttonCount the amount of buttons
	 */
	public Joystick(int stick, int buttonCount){
		this("Joystick"+stick, stick, buttonCount);
	}
	
	/**
	 * Gets the x-axis value of the joystick
	 * @return the x axis value
	 */
	public double getX(){
		return stick.getX();
	}
	/**
	 * Gets the y-axis value of the joystick
	 * @return the y axis value
	 */
	public double getY(){
		return stick.getY();
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
		return stick_num;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public double getRawAxis(int axis){
		return RobotFactory.getHIDInterface().getHIDAxis(stick_num, axis);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean getRawButton(int button){
		return RobotFactory.getHIDInterface().getHIDButton(stick_num, button);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getRawPOV(int pov){
		return RobotFactory.getHIDInterface().getHIDPOV(stick_num, pov);
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
	 * <p>
	 * Since only one stick exists, indexes other than 0 returns null.
	 * </p>
	 */
	@Override
	public Stick getStick(int index) {
		switch(index){
			case 0: return stick;
			default: return null;
		}
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Stick getStick() {
		return stick;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getButtonCount(){
		return buttons.length;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public POV getPOV(){
		return pov;
	}
	
	/**
	 * Refreshes the value of the button wrappers. Used to determine whether or not to run 
	 * actions attached to those wrapped. 
	 */
	@Override
	public void run() {
		for(int i = 0; i < buttons.length; i++)
			buttons[i].run();
	}
	
	/**
	 * Refreshes all created joysticks.
	 */
	public static void refreshAll(){
		for(Joystick c = head; c != null; c = c.next)
			c.run();
	}
	
	@Override
	protected HID getHID() {
		return this;
	}
}
