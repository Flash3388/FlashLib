package edu.flash3388.flashlib.robot.systems;

import edu.flash3388.flashlib.robot.Action;
import edu.flash3388.flashlib.robot.FlashRobotUtil;
import edu.flash3388.flashlib.robot.devices.FlashSpeedController;
import edu.flash3388.flashlib.robot.devices.ModableMotor;
import edu.flash3388.flashlib.robot.devices.MultiSpeedController;
import edu.flash3388.flashlib.robot.Subsystem;

/**
 * A generic single motor system. Implements XAxisMovable, YAxisMovable, Rotatable, ModableMotor, and VoltageScalable.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public class SingleMotorSystem extends Subsystem implements XAxisMovable, YAxisMovable, Rotatable, VoltageScalable, ModableMotor{
	
	private FlashSpeedController controller;
	private double default_speed_forward = 0.5, default_speed_backward = 0.5;
	private boolean scaleVoltage = false;
	private boolean brakemode = false;
	
	/**
	 * Creates a generic motor system for an array of motors.
	 * 
	 * @param controllers array of speed controllers
	 */
	public SingleMotorSystem(FlashSpeedController... controllers){
		this(null, controllers);
	}
	/**
	 * Creates a generic motor system for an array of motors.
	 * 
	 * @param controllers array of speed controllers
	 * @param defaultAction default system action
	 */
	public SingleMotorSystem(Action defaultAction, FlashSpeedController... controllers){
		this(new MultiSpeedController(controllers), defaultAction);
	}
	/**
	 * Creates a generic motor system for a motor.
	 * 
	 * @param controller a speed controller
	 */
	public SingleMotorSystem(FlashSpeedController controller){
		this(controller, null);
	}
	/**
	 * Creates a generic motor system for a motor.
	 * 
	 * @param controller a speed controller
	 * @param defaultAction default system action
	 */
	public SingleMotorSystem(FlashSpeedController controller, Action defaultAction){
		super("");
		this.controller = controller;
		setDefaultAction(defaultAction);
		enableBrakeMode(false);
	}
	
	/**
	 * Sets the reversing of directions by the motor controller.
	 * @param inverted true to reverse directions, false otherwise
	 */
	public void setInverted(boolean inverted){
		controller.setInverted(inverted);
	}
	/**
	 * Gets whether or not the directions of the motor are inverted
	 * @return true if the motor is inverted, false otherwise.
	 */
	public boolean isInverted(){
		return controller.isInverted();
	}
	
	/**
	 * Gets the speed controller object of this system.
	 * @return speed controller object
	 */
	public FlashSpeedController getMotorController(){
		return controller;
	}
	/**
	 * Gets the currently set percent vbus in the motor controller.
	 * @return used percent vbus
	 */
	public double get(){
		return controller.get();
	}
	
	
	/**
	 * Sets the default speed to move the system. Used when calling {@link #forward()} and {@link #backward()}.
	 * @param speed the default speed
	 */
	public void setDefaultSpeed(double speed){
		setDefaultSpeed(speed, speed);
	}
	/**
	 * Sets the default speed to move the system. Used when calling {@link #forward()} and {@link #backward()}.
	 * @param forward the default forward speed
	 * @param backward the default backward speed
	 */
	public void setDefaultSpeed(double forward, double backward){
		if(forward < 0) forward *= -1;
		if(backward < 0) backward *= -1;
				
		default_speed_forward = forward;
		default_speed_backward = backward;
	}
	
	/**
	 * Sets the speed of the motor controller by this object. The speed is a percentage known as 
	 * percent voltage bus (vbus), which describes a percentage of the currently available voltage to
	 * be supplied to the motor. The sign of the speed describes the direction of rotation.
	 * <p>
	 * If the motor controller is set to inverted directions, the directions are switched.
	 * </p>
	 * 
	 * @param speed [-1 to 1] describing the percent vbus
	 */
	public void set(double speed){
		if(scaleVoltage)
			speed = FlashRobotUtil.scaleVoltageBus(speed);
		
		controller.set(speed);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void forward(double speed){
		if(speed < 0) speed *= -1;
		set(speed);
	}
	/**
	 * Moves the system forward at the default speed.
	 */
	public void forward(){
		forward(default_speed_forward);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void backward(double speed){
		if(speed < 0) speed *= -1;
		set(-speed);
	}
	/**
	 * Moves the system backwards at the default speed.
	 */
	public void backward(){
		backward(default_speed_backward);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void stop(){
		controller.set(0);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void rotate(double speed, boolean direction) {
		moveY(speed, direction);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void rotateRight(double speed) {
		forward(speed);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void rotateLeft(double speed) {
		backward(speed);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void moveX(double speed, boolean direction) {
		moveY(speed, direction);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void right(double speed) {
		forward(speed);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void left(double speed) {
		backward(speed);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void moveY(double speed, boolean direction) {
		if(direction) forward(speed);
		else backward(speed);
	}
	/**
	 * {@inheritDoc}
	 * <p>
	 * Returns this instance.
	 * </p>
	 */
	@Override
	public Subsystem getSystem() {
		return this;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void enableVoltageScaling(boolean en) {
		scaleVoltage = en;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isVoltageScaling() {
		return scaleVoltage;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void enableBrakeMode(boolean mode) {
		this.brakemode = mode;
		if(controller instanceof ModableMotor)
			((ModableMotor)controller).enableBrakeMode(mode);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean inBrakeMode() {
		return brakemode;
	}
}
