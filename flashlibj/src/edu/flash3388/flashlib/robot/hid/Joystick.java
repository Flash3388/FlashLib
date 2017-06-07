package edu.flash3388.flashlib.robot.hid;

import edu.flash3388.flashlib.flashboard.HIDSendable;
import edu.flash3388.flashlib.robot.RobotFactory;
import edu.flash3388.flashlib.robot.ScheduledTask;

public class Joystick extends HIDSendable implements HID, ScheduledTask{

	private static Joystick head;
	private Joystick next;
	
	private static final int X = 0, Y = 1, Z = 2, THROTTLE = 3;

	private Button[] buttons;
	private int stick_num;
	private DPad pov;
	private Stick stick;
	
	public Joystick(String name, int stick, int buttonCount){
		super(name);
		stick_num = stick;
		
		this.stick = new Stick(stick, X, Y);
		buttons = new Button[buttonCount];
		for(int i = 0; i < buttons.length; i++)
			buttons[i] = new Button(stick, i+1);
		pov = new DPad(stick);
		
		next = head;
		head = this;
	}
	public Joystick(int stick, int buttonCount){
		this("Joystick"+stick, stick, buttonCount);
	}
	
	public double getX(){
		return stick.getX();
	}
	public double getY(){
		return stick.getY();
	}
	public double getZ(){
		return RobotFactory.getStickAxis(stick_num, Z);
	}
	public double getThrottle(){
		return RobotFactory.getStickAxis(stick_num, THROTTLE);
	}
	
	@Override
	public double getRawAxis(int axis){
		return RobotFactory.getStickAxis(stick_num, axis);
	}
	@Override
	public boolean getRawButton(int button){
		return RobotFactory.getStickButton(stick_num, (byte)button);
	}
	@Override
	public Button getButton(int button) {
		return buttons[button - 1];
	}
	@Override
	public Stick getStick(int index) {
		switch(index){
			case 0: return stick;
			default: return null;
		}
	}
	@Override
	public Stick getStick() {
		return stick;
	}
	@Override
	public int getButtonCount(){
		return buttons.length;
	}
	@Override
	public DPad getPOV(){
		return pov;
	}
	
	public void refresh(){
		for(Button b : buttons)
			b.refresh();
		pov.refresh();
	}
	@Override
	public boolean run() {
		refresh();
		return true;
	}
	public static void refreshAll(){
		for(Joystick c = head; c != null; c = c.next)
			c.refresh();
	}
	@Override
	protected HID getHID() {
		return this;
	}
}
