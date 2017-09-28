package edu.flash3388.flashlib.robot.systems;

import edu.flash3388.flashlib.math.Mathf;
import edu.flash3388.flashlib.robot.FlashRobotUtil;
import edu.flash3388.flashlib.robot.Subsystem;
import edu.flash3388.flashlib.robot.devices.FlashSpeedController;
import edu.flash3388.flashlib.robot.devices.ModableMotor;
import edu.flash3388.flashlib.robot.hid.HID;
import edu.flash3388.flashlib.robot.hid.Stick;

/**
 * This class offers a wide range of motion algorithms for drive trains. There
 * are several popular control methods available:
 * <ul>
 * 	<li>Tank Drive</li>
 * 	<li>Arcade Drive</li>
 * 	<li>Omni Drive</li>
 * </ul>
 * Those are in addition to any new control algorithms added for possible use. This class
 * implements both {@link TankDriveSystem} and {@link HolonomicDriveSystem}. To allow for dynamic
 * use, it is the drive train is divided to four sides: front, rear, right and left. Each side
 * can hold any amount of motors.
 *
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public class FlashDrive extends Subsystem implements TankDriveSystem, HolonomicDriveSystem, ModableMotor, VoltageScalable{

	/**
	 * Represents sides of the drive system.
	 * 
	 * @author Tom Tzook
	 * @since FlashLib 1.0.0
	 */
	public static enum MotorSide{
		Front, Rear, Right, Left
	}
	
	private FlashSpeedController right_controllers;
	private FlashSpeedController left_controllers;
	private FlashSpeedController front_controllers;
	private FlashSpeedController rear_controllers;
	
	private double speed_limit = 1.0;
	private double minSpeed = 0.0;
	private double default_speed = 0.5;
	private boolean voltageScaling = false;
	
	/**
	 * Creates an instance of the FlashDrive class. Allows for an unlimited amount of motors on the sides 
	 * of the robot (right and left).
	 * 
	 * @param right An instance of the Controllers class representing the motor controllers on the right side.
	 * @param left An instance of the Controllers class representing the motor controllers on the left side.
	 */
	public FlashDrive(FlashSpeedController right, FlashSpeedController left){
		this(right, left, null, null);
	}
	
	/**
	 * Creates an instance of the FlashDrive class. Allows for an unlimited amount of motors on all sides 
	 * of the robot (right, left, front and back).
	 * 
	 * @param right An instance of the Controllers class representing the motor controllers on the right side.
	 * @param left An instance of the Controllers class representing the motor controllers on the left side.
	 * @param front An instance of the Controllers class representing the motor controllers on the front side.
	 * @param back An instance of the Controllers class representing the motor controllers on the rear side.
	 * @throws IllegalArgumentException If all parameters are null.
	 */
	public FlashDrive(FlashSpeedController right, FlashSpeedController left, FlashSpeedController front, FlashSpeedController back){
		super("FlashDrive");
		if(right == null && left == null && front == null && back == null) 
			throw new IllegalArgumentException("At least one side must have wheels");
		
		right_controllers = right;
		left_controllers = left;
		front_controllers = front;
		rear_controllers = back;
		
		enableBrakeMode(false);
	}
	
	/**
	 * Sets whether all the motors in a side are inverted or not (forward is backward and other wise).
	 * 
	 * @param s The position of the motors on the robot as an instance of the MotorSide class.
	 * @param inverted A boolean representing whether the motors are inverted or not.
	 */
	public void setInverted(MotorSide s, boolean inverted){
		switch(s){
			case Left:
				left_controllers.setInverted(inverted);
				break;
			case Right:
				right_controllers.setInverted(inverted);
				break;
			case Front:
				front_controllers.setInverted(inverted);
				break;
			case Rear:
				rear_controllers.setInverted(inverted);
				break;
		}
	}
	/**
	 * Gets the speed controllers used for a given side of the drive system.
	 * @param s side of the motors
	 * @return the speed controller object for that side.
	 */
	public FlashSpeedController getController(MotorSide s){
		switch(s){
			case Left:
				return left_controllers;
			case Right:
				return right_controllers;
			case Front:
				return front_controllers;
			case Rear:
				return rear_controllers;
			default: return null;
		}
	}
	
	/**
	 * Sets the speed limit of the system. If the set speed for a motor exceeds this value, it is decreased to that value.
	 * @param limit speed limit [0...1]
	 */
	public void setSpeedLimit(double limit){
		speed_limit = Math.abs(limit);
	}
	/**
	 * Gets the speed limit of the system. If the set speed for a motor exceeds this value, it is decreased to that value.
	 * @return speed limit [0...1]
	 */
	public double getSpeedLimit(){
		return speed_limit;
	}
	/**
	 * Sets the minimum speed of the system. If the set speed for a motor does not exceeds this value, 
	 * it is decreased to 0.
	 * @param limit speed limit [0...1]
	 */
	public void setMinSpeed(double limit){
		minSpeed = Math.abs(limit);
	}
	/**
	 * Gets the minimum speed of the system. If the set speed for a motor does not exceeds this value, 
	 * it is decreased to 0.
	 * @return speed limit [0...1]
	 */
	public double getMinSpeed(){
		return minSpeed;
	}
	
	/**
	 * Sets the default speed to move the system. Used when calling drive methods with no parameters.
	 * @param speed the default speed
	 */
	public void setDefaultSpeed(double speed){
		speed = Math.abs(speed);
	}
	
	/**
	 * Tank drive implements a dual joystick drive. Given right and left speed values, the code sets the values to move 
	 * each side separately.
	 * 
	 * @param right The speed value of the right side of motors 1 to -1.
	 * @param left The speed value of the left side of motors 1 to -1.
	 */
	public void tankDrive(double right, double left){
		setMotors(0, right, left, 0);
	}
	
	/**
	 * Tank drive implements a dual joystick drive. Given right and left speed values, the code sets the values to move 
	 * each side separately. Allows to decrease the values of the speeds by choosing to square them. Given a value SPEED
	 * when squared is true, the resulting value is SPEED * SPEED.
	 * 
	 * @param right The speed value of the right side of motors.
	 * @param left The speed value of the left side of motors.
	 * @param squared If true, the speed will be multiplied by it self for smaller values.
	 */
	public void tankDrive(double right, double left, boolean squared){
		if(!squared) tankDrive(right, left);
		else tankDrive(right * right, left * left);
	}
	
	/**
	 * Tank drive implements a dual joystick drive. Given right and left joysticks, the code sets the Y axis values 
	 * to move each side separately.
	 * 
	 * @param stick_right The joystick for moving the right side.
	 * @param stick_left The joystick for moving the left side.
	 */
	public void tankDrive(Stick stick_right, Stick stick_left){
		tankDrive(-stick_right.getY(), stick_left.getY());
	}
	
	/**
	 * Tank drive implements a dual joystick drive. Given right and left joysticks, the code sets the Y axis values 
	 * to move each side separately. Allows to decrease the values of the speeds by choosing to square them. Given a 
	 * value SPEED when squared is true, the resulting value is SPEED * SPEED.
	 * 
	 * @param stick_right The joystick for moving the right side.
	 * @param stick_left The joystick for moving the left side.
	 * @param squared If true, the speed will be multiplied by it self for smaller values.
	 */
	public void tankDrive(Stick stick_right, Stick stick_left, boolean squared){
		tankDrive(-stick_right.getY(), stick_left.getY(), squared);
	}
	
	/**
	 * Tank drive implements a dual joystick drive. Given right and left joysticks, the code sets the values 
	 * of a given axis to move each side separately. 
	 * 
	 * @param stick_right The joystick for moving the right side.
	 * @param right_axis The axis on the right side joystick.
	 * @param stick_left The joystick for moving the left side.
	 * @param left_axis The axis on the left side joystick.
	 */
	public void tankDrive(HID stick_right, int right_axis, HID stick_left, int left_axis){
		tankDrive(-stick_right.getRawAxis(right_axis), stick_left.getRawAxis(left_axis));
	}
	
	/**
	 * Tank drive implements a dual joystick drive. Given right and left joysticks, the code sets the values 
	 * of a given axis to move each side separately. Allows to decrease the values of the speeds by choosing to square 
	 * them. Given a value SPEED when squared is true, the resulting value is SPEED * SPEED.
	 * 
	 * @param stick_right The joystick for moving the right side.
	 * @param right_axis The axis on the right side joystick.
	 * @param stick_left The joystick for moving the left side.
	 * @param left_axis The axis on the left side joystick.
	 * @param squared If true, the speed will be multiplied by it self for smaller values.
	 */
	public void tankDrive(HID stick_right, int right_axis, HID stick_left, int left_axis, boolean squared){
		tankDrive(-stick_right.getRawAxis(right_axis), -stick_left.getRawAxis(left_axis), squared);
	}
	
	/**
	 * Arcade drive implements a single joystick tank drive. Given move and rotate speed values, the code sets the values 
	 * to move the tank drive. The move value is responsible for moving the robot forward and backward while the 
	 * rotate value is responsible for the robot rotation. 
	 * 
	 * @param moveValue The value to move forward or backward 1 to -1.
	 * @param rotateValue The value to rotate right or left 1 to -1.
	 */
	@Override
	public void arcadeDrive(double moveValue, double rotateValue){
		double[] values = calculate_arcadeDrive(moveValue, rotateValue);
		setMotors(0, values[0], values[1], 0);
	}
	/**
	 * Arcade drive implements a single joystick tank drive. Given move and rotate speed values, the code sets the values 
	 * to move the tank drive. The move value is responsible for moving the robot forward and backward while the 
	 * rotate value is responsible for the robot rotation. When both values are not zero then the value taken is the
	 * absolute bigger. Allows to decrease the values of the speeds by choosing to square them. Given a value SPEED 
	 * when squared is true, the resulting value is SPEED * SPEED.
	 * 
	 * @param moveValue The value to move forward or backward.
	 * @param rotateValue The value to rotate right or left.
	 * @param squared If true, the speed will be multiplied by it self for smaller values.
	 */
	public void arcadeDrive(double moveValue, double rotateValue, boolean squared){
		if(!squared) arcadeDrive(moveValue, rotateValue);
		else arcadeDrive(moveValue * moveValue, rotateValue * rotateValue);
	}
	
	/**
	 * Arcade drive implements a single joystick drive. Given a joystick, the code sets the values of the Y axis 
	 * as move value and X axis as the rotate value. The move value is responsible for moving the robot forward and 
	 * backward while the rotate value is responsible for the robot rotation. When both values are not zero then the 
	 * value taken is the absolute bigger.
	 * 
	 * @param stick The joystick to use for Arcade single stick driving. The
     *        Y axis will be selected for forwards and backwards and the X axis will
     *        be selected for rotation rate.
	 */
	public void arcadeDrive(Stick stick){
		arcadeDrive(-stick.getY(), stick.getX());
	}
	/**
	 * Arcade drive implements a single joystick drive. Given a joystick, the code sets the values of the Y axis 
	 * as move value and X axis as the rotate value. The move value is responsible for moving the robot forward and 
	 * backward while the rotate value is responsible for the robot rotation. When both values are not zero then the 
	 * value taken is the absolute bigger.
	 * 
	 * @param mstick The joystick to use for Arcade single stick driving. The
     *        Y axis will be selected for forwards and backwards
     * @param rstick The joystick to use for Arcade single stick driving. The X axis will
     *        be selected for rotation rate.
	 */
	public void arcadeDrive(Stick mstick, Stick rstick){
		arcadeDrive(-mstick.getY(), rstick.getX());
	}
	
	/**
	 * Arcade drive implements a single joystick drive. Given a joystick, the code sets the values of the Y axis 
	 * as move value and X axis as the rotate value. The move value is responsible for moving the robot forward and 
	 * backward while the rotate value is responsible for the robot rotation. When both values are not zero then the 
	 * value taken is the absolute bigger. Allows to decrease the values of the speeds by choosing to square them. 
	 * Given a value SPEED when squared is true, the resulting value is SPEED * SPEED.
	 * 
	 * @param stick The joystick to use for Arcade single stick driving. The
     *        Y axis will be selected for forwards and backwards and the X axis will
     *        be selected for rotation rate.
	 * @param squared If true, the speed will be multiplied by it self for smaller values.
	 */
	public void arcadeDrive(Stick stick, boolean squared){
		arcadeDrive(-stick.getY(), stick.getX(), squared);
	}
	/**
	 * Arcade drive implements a single joystick drive. Given a joystick, the code sets the values of the Y axis 
	 * as move value and X axis as the rotate value. The move value is responsible for moving the robot forward and 
	 * backward while the rotate value is responsible for the robot rotation. When both values are not zero then the 
	 * value taken is the absolute bigger. Allows to decrease the values of the speeds by choosing to square them. 
	 * Given a value SPEED when squared is true, the resulting value is SPEED * SPEED.
	 * 
	 * @param mstick The joystick to use for Arcade single stick driving. The
     *        Y axis will be selected for forwards and backwards
     * @param rstick The joystick to use for Arcade single stick driving. The X axis will
     *        be selected for rotation rate.
	 * @param squared If true, the speed will be multiplied by it self for smaller values.
	 */
	public void arcadeDrive(Stick mstick, Stick rstick, boolean squared){
		arcadeDrive(-mstick.getY(), rstick.getX(), squared);
	}
	
	/**
	 * Arcade drive implements a single joystick drive. Given a joystick, the code sets the values of a given move axis 
	 * as move value and a given rotate axis as the rotate value. The move value is responsible for moving the robot 
	 * forward and backward while the rotate value is responsible for the robot rotation. When both values are not 
	 * zero then the value taken is the absolute bigger.
	 * 
	 * @param stick The joystick to use for Arcade single stick driving. The
     *        move axis will be selected for forwards and backwards and the rotate axis will
     *        be selected for rotation rate.
	 * @param move_axis The axis number on the joystick to be selected for forwards and backwards.
	 * @param rotate_axis The axis number on the joystick to be selected for rotation rate.
	 */
	public void arcadeDrive(HID stick, int move_axis, int rotate_axis){
		arcadeDrive(-stick.getRawAxis(move_axis), stick.getRawAxis(rotate_axis));
	}
	
	/**
	 * Arcade drive implements a single joystick drive. Given a joystick, the code sets the values of a given move axis 
	 * as move value and a given rotate axis as the rotate value. The move value is responsible for moving the robot 
	 * forward and backward while the rotate value is responsible for the robot rotation. When both values are not 
	 * zero then the value taken is the absolute bigger. Allows to decrease the values of the speeds by choosing to 
	 * square them. Given a value SPEED when squared is true, the resulting value is SPEED * SPEED.
	 * 
	 * @param stick The joystick to use for Arcade single stick driving. The
     *        move axis will be selected for forwards and backwards and the rotate axis will
     *        be selected for rotation rate.
	 * @param move_axis The axis number on the joystick to be selected for forwards and backwards.
	 * @param rotate_axis The axis number on the joystick to be selected for rotation rate.
	 * @param squared If true, the speed will be multiplied by it self for smaller values.
	 */
	public void arcadeDrive(HID stick, int move_axis, int rotate_axis, boolean squared){
		arcadeDrive(-stick.getRawAxis(move_axis), stick.getRawAxis(rotate_axis), squared);
	}
	
	/**
	 * Omni drive implements a single joystick drive where there are wheels on the sides of the robot to move forward
	 * and backwards and there are also wheels in the front and back to move right and left without rotating. Given
	 * a Y and X value the drive sets the Y value to move the wheels on the sides of the robot(right and left) and
	 * the X value to move the wheels in the front and back.
	 * 
	 * @param y The speed to move the motors on the side of the robot.
	 * @param x The speed to move the motors on the front and back of the robot.
	 */
	@Override
	public void omniDrive(double y, double x){
		setMotors(x, y, y, x);
	}
	
	/**
	 * Omni drive implements a single joystick drive where there are wheels on the sides of the robot to move forward
	 * and backwards and there are also wheels in the front and back to move right and left without rotating. Given
	 * a Y and X value the drive sets the Y value to move the wheels on the sides of the robot(right and left) and
	 * the X value to move the wheels in the front and back. Allows to decrease the values of the speeds by choosing to 
	 * square them. Given a value SPEED when squared is true, the resulting value is SPEED * SPEED.
	 * 
	 * @param y The speed to move the motors on the side of the robot.
	 * @param x The speed to move the motors on the front and back of the robot.
	 * @param squared If true, the speed will be multiplied by it self for smaller values.
	 */
	public void omniDrive(double y, double x, boolean squared){
		if(!squared) omniDrive(y, x);
		else omniDrive(y * y, x * x);
	}
	
	/**
	 * Omni drive implements a single joystick drive where there are wheels on the sides of the robot to move forward
	 * and backwards and there are also wheels in the front and back to move right and left without rotating. Given
	 * a joystick the drive sets the Y axis value to move the wheels on the sides of the robot(right and left) and
	 * the X axis value to move the wheels in the front and back.
	 * 
	 * @param stick The joystick to use for omni single stick driving. The
     *        Y axis will be selected for forwards and backwards and the X axis will
     *        be selected for right and left.
	 */
	public void omniDrive(Stick stick){
		omniDrive(-stick.getY(), stick.getX());
	}
	
	/**
	 * Omni drive implements a single joystick drive where there are wheels on the sides of the robot to move forward
	 * and backwards and there are also wheels in the front and back to move right and left without rotating. Given
	 * a joystick the drive sets the Y axis value to move the wheels on the sides of the robot(right and left) and
	 * the X axis value to move the wheels in the front and back. Allows to decrease the values of the speeds by choosing to 
	 * square them. Given a value SPEED when squared is true, the resulting value is SPEED * SPEED.
	 * 
	 * @param stick The joystick to use for omni single stick driving. The
     *        Y axis will be selected for forwards and backwards and the X axis will
     *        be selected for right and left.
	 * @param squared If true, the speed will be multiplied by it self for smaller values.
	 */
	public void omniDrive(Stick stick, boolean squared){
		omniDrive(-stick.getY(), stick.getX(), squared);
	}
	
	/**
	 * Omni drive implements a single joystick drive where there are wheels on the sides of the robot to move forward
	 * and backwards and there are also wheels in the front and back to move right and left without rotating. Given
	 * a joystick the drive sets the move axis value to move the wheels on the sides of the robot(right and left) and
	 * the side axis value to move the wheels in the front and back.
	 * 
	 * @param stick The joystick to use for omni single stick driving. The
     *        move axis will be selected for forwards and backwards and the side axis will
     *        be selected for right and left.
	 * @param move_axis The move axis will be selected for forwards and backward.
	 * @param side_axis The side axis will be selected for right and left.
	 */
	public void omniDrive(HID stick, int move_axis, int side_axis){
		omniDrive(-stick.getRawAxis(move_axis), stick.getRawAxis(side_axis));
	}
	
	/**
	 * Omni drive implements a single joystick drive where there are wheels on the sides of the robot to move forward
	 * and backwards and there are also wheels in the front and back to move right and left without rotating. Given
	 * a joystick the drive sets the move axis value to move the wheels on the sides of the robot(right and left) and
	 * the side axis value to move the wheels in the front and back. Allows to decrease the values of the speeds by 
	 * choosing to square them. Given a value SPEED when squared is true, the resulting value is SPEED * SPEED.
	 * 
	 * @param stick The joystick to use for omni single stick driving. The
     *        move axis will be selected for forwards and backwards and the side axis will
     *        be selected for right and left.
	 * @param move_axis The move axis will be selected for forwards and backward.
	 * @param side_axis The side axis will be selected for right and left.
	 * @param squared If true, the speed will be multiplied by it self for smaller values.
	 */
	public void omniDrive(HID stick, int move_axis, int side_axis, boolean squared){
		omniDrive(-stick.getRawAxis(move_axis), stick.getRawAxis(side_axis), squared);
	}
	
	/**
	 * Omni drive implements a single joystick drive where there are wheels on the sides of the robot to move forward
	 * and backwards and there are also wheels in the front and back to move right and left without rotating. Given
	 * a Y and X value the drive sets the Y value to move the wheels on the sides of the robot(right and left) and
	 * the X value to move the wheels in the front and back.
	 * 
	 * <p>
	 * Vectored tank drive is an experimental omni control which uses a motion vector just like Mecanum drive.
	 * The control algorithm derives from arcade drive.
	 * </p>
	 * 
	 * @param y y-axis value of the vector
	 * @param x x-axis value of the vector
	 * @param rotation rotation value
	 * 
	 * @see #arcadeDrive(double, double)
	 */
	public void vectoredOmniDrive_cartesian(double y, double x, double rotation){
		double[] values = calculate_vectoredOmniDrive_cartesian(y, x, rotation);
		setMotors(values[0], values[1], values[2], values[3]);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void moveY(double speed) {
		setMotors(0.0, speed, speed, 0.0);
	}
	
	/**
	 * Sets the right left motors with speeds to move forward.
	 * @param r value for right [0...1]
	 * @param l value for left [0...1]
	 */
	public void forward(double r, double l){
		setMotors(0, r, l, 0);
	}
	/**
	 * Moves the system forward at the default speed.
	 */
	public void forward(){
		forward(default_speed);
	}
	/**
	 * Sets the right left motors with speeds to move backwards.
	 * @param r value for right [0...1]
	 * @param l value for left [0...1]
	 */
	public void backward(double r, double l){
		setMotors(0, -r, -l, 0);
	}
	/**
	 * Moves the system backwards at the default speed.
	 */
	public void backward(){
		backward(default_speed);
	}
	
	@Override
	public void moveX(double speed) {
		setMotors(speed, 0.0, 0.0, speed);
	}
	/**
	 * Sets the front and rear motors with speeds to move right.
	 * @param r value for rear [0...1]
	 * @param f value for front [0...1]
	 */
	public void right(double f, double r){
		setMotors(f, 0, 0, r);
	}
	/**
	 * Moves the system right at the default speed.
	 */
	public void right(){
		right(default_speed);
	}
	
	/**
	 * Sets the front and rear motors with speeds to move left.
	 * @param r value for rear [0...1]
	 * @param f value for front [0...1]
	 */
	public void left(double f, double r){
		setMotors(-f, 0, 0, -r);
	}
	/**
	 * Moves the system left at the default speed.
	 */
	public void left(){
		left(default_speed);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override 
	public void rotate(double speed){
		setMotors(-speed, -speed, speed, speed);
	}

	/**
	 * Rotates the system right at the default speed.
	 */
	public void rotateRight(){
		rotateRight(default_speed);
	}
	/**
	 * Rotates the system left at the default speed.
	 */
	public void rotateLeft(){
		rotateLeft(default_speed);
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * Moves the drive system as an omni drive system.
	 * </p>
	 * @see #vectoredOmniDrive_cartesian(double, double, double)
	 */
	@Override
	public void holonomicCartesian(double y, double x, double rotation) {
		//implement for omni drive
		vectoredOmniDrive_cartesian(y, x, rotation);
	}
	
	/**
	 * Sets values for all speed controllers. Limits them first according to set parameters.
	 * 
	 * @param f value for forward motors
	 * @param r value for right motors
	 * @param l value for left motors
	 * @param b value for back motors
	 */
	public void setMotors(double f, double r, double l, double b){
		f = limit(f);
		r = limit(r);
		l = limit(l);
		b = limit(b);
		
		if(right_controllers != null) 
			right_controllers.set(r);
		if(left_controllers != null) 
			left_controllers.set(l);
		if(front_controllers != null) 
			front_controllers.set(f); 
		if(rear_controllers != null) 
			rear_controllers.set(b); 
	}
	/**
	 * {@inheritDoc}
	 */
	@Override 
	public void stop(){
		if(right_controllers != null) 
			right_controllers.stop();
		if(left_controllers != null) 
			left_controllers.stop();
		if(front_controllers != null) 
			front_controllers.stop(); 
		if(rear_controllers != null) 
			rear_controllers.stop(); 
	}
	
	private double limit(double speed){
		if(voltageScaling)
			speed = FlashRobotUtil.scaleVoltageBus(speed);
		
		if(Math.abs(speed) < minSpeed)
			return 0.0;
			
		if(speed_limit != 1.0)
			speed = Mathf.constrain(speed * speed_limit, -speed_limit, speed_limit);
		return speed;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void enableBrakeMode(boolean mode) {
		if(front_controllers != null && front_controllers instanceof ModableMotor)
			((ModableMotor)front_controllers).enableBrakeMode(mode);
		if(left_controllers != null && left_controllers instanceof ModableMotor)
			((ModableMotor)left_controllers).enableBrakeMode(mode);
		if(right_controllers != null && right_controllers instanceof ModableMotor)
			((ModableMotor)right_controllers).enableBrakeMode(mode);
		if(rear_controllers != null && rear_controllers instanceof ModableMotor)
			((ModableMotor)rear_controllers).enableBrakeMode(mode);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean inBrakeMode() {
		return (front_controllers == null || (front_controllers instanceof ModableMotor && 
												((ModableMotor)front_controllers).inBrakeMode())) && 
				(left_controllers == null || left_controllers instanceof ModableMotor && 
												((ModableMotor)left_controllers).inBrakeMode()) &&
				(right_controllers == null || right_controllers instanceof ModableMotor && 
												((ModableMotor)right_controllers).inBrakeMode()) && 
				(rear_controllers == null || rear_controllers instanceof ModableMotor && 
												((ModableMotor)rear_controllers).inBrakeMode());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void enableVoltageScaling(boolean en) {
		voltageScaling = en;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isVoltageScaling() {
		return voltageScaling;
	}
	
	/**
	 * Arcade drive implements a single joystick tank drive. Given move and rotate speed values, the code sets the values 
	 * to move the tank drive. The move value is responsible for moving the robot forward and backward while the 
	 * rotate value is responsible for the robot rotation. 
	 * 
	 * <p>
	 * This method calculates the outputs to the motors and returns them as an array. They array will contain
	 * values for 2 sides in the following order: right, left.
	 * </p>
	 * 
	 * @param moveValue The value to move forward or backward 1 to -1.
	 * @param rotateValue The value to rotate right or left 1 to -1.
	 * 
	 * @return returns an array of 2 with the motor output values in this order: right, left.
	 */
	public static double[] calculate_arcadeDrive(double moveValue, double rotateValue){
		double rSpeed = 0.0, lSpeed = 0.0;
		
		if (moveValue > 0.0) {
			if (rotateValue > 0.0) {
				lSpeed = moveValue - rotateValue;
				rSpeed = Math.max(moveValue, rotateValue);
			} else {
				lSpeed = Math.max(moveValue, -rotateValue);
				rSpeed = moveValue + rotateValue;
			}
	    } else {
			if (rotateValue > 0.0) {
				lSpeed = -Math.max(-moveValue, rotateValue);
				rSpeed = moveValue + rotateValue;
			} else {
				lSpeed = moveValue - rotateValue;
				rSpeed = -Math.max(-moveValue, -rotateValue);
			}
	    }
		
		return new double[] {rSpeed, lSpeed};
	}
	/**
	 * Omni drive implements a single joystick drive where there are wheels on the sides of the robot to move forward
	 * and backwards and there are also wheels in the front and back to move right and left without rotating. Given
	 * a Y and X value the drive sets the Y value to move the wheels on the sides of the robot(right and left) and
	 * the X value to move the wheels in the front and back.
	 * 
	 * <p>
	 * Vectored tank drive is an experimental omni control which uses a motion vector just like Mecanum drive.
	 * The control algorithm derives from arcade drive.
	 * </p>
	 * 
	 * <p>
	 * This method calculates the outputs to the motors and returns them as an array. They array will contain
	 * values for 4 sides in the following order: front, right, left, back.
	 * </p>
	 * 
	 * @param y y-axis value of the vector
	 * @param x x-axis value of the vector
	 * @param rotation rotation value
	 * 
	 * @return returns an array of 4 with the motor output values in this order: front, right, left, back.
	 * 
	 * @see #arcadeDrive(double, double)
	 */
	public static double[] calculate_vectoredOmniDrive_cartesian(double y, double x, double rotation){
		double right = 0.0, left = 0.0, front = 0.0, rear = 0.0;
		
		if (y > 0.0) {
  	      	if (rotation > 0.0) {
  	    	  left = y - rotation;
  	    	  right = Math.max(y, rotation);
  	      	} else {
  	    	  left = Math.max(y, -rotation);
  	    	  right = y + rotation;
  	      	}
  	    } else {
  	    	if (rotation > 0.0) {
  	    	    left = -Math.max(-y, rotation);
  	    	    right = y + rotation;
  	    	} else {
  	    		left = y - rotation;
  	    		right = -Math.max(-y, -rotation);
  	    	}
  	    }
			
		
		if (x > 0.0) {
			if (rotation > 0.0) {
  	    	  rear = x - rotation;
  	    	  front = Math.max(x, rotation);
  	      	} else {
  	    	  rear = Math.max(x, -rotation);
  	    	  front = x + rotation;
  	      	}
  	    }else{
	    	if (rotation > 0.0) {
	    		rear = -Math.max(-x, rotation);
  	    	    front = x + rotation;
  	    	} else {
  	    		rear = x - rotation;
  	    		front = -Math.max(-x, -rotation);
  	    	}
  	    }
		
		return new double[] {front, right, left, rear};
	}
}