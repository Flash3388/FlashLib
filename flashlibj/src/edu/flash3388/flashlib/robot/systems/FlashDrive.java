package edu.flash3388.flashlib.robot.systems;

import edu.flash3388.flashlib.robot.Action;
import edu.flash3388.flashlib.robot.System;
import edu.flash3388.flashlib.robot.SystemAction;
import edu.flash3388.flashlib.robot.devices.FlashSpeedController;
import edu.flash3388.flashlib.robot.hid.HID;
import edu.flash3388.flashlib.robot.hid.Stick;

/**
 * This class offers a wide range of both joystick-based driving solutions and autonomous drives.
 * The instance allows for an unlimited amount of motors on four sides of the robot.
 * The class contains the following drive methods:
 * 			Tank Drive
 * 			Arcade Drive
 * 			Squared Omni Drive
 * 
 * The autonomous methods allow for all-direction joystick-free movement.
 * 
 * @author Tom Tzook
 */
public class FlashDrive extends System implements TankDriveSystem, HolonomicDriveSystem{
	
	public static class InterfaceAction extends Action{
		private DriveControlInterface driveInterface;
		private FlashDrive drive;
		
		public InterfaceAction(FlashDrive drive, DriveControlInterface inter){
			this.drive = drive;
			this.driveInterface = inter;
			this.driveInterface.setFlashDrive(drive);
			this.requires(drive);
		}

		@Override
		protected void execute() {
			driveInterface.drive();
		}
		@Override
		protected void end() {
			drive.stop();
		}
	}
	public static abstract class DriveControlInterface{
		protected FlashDrive drive;
		
		protected void setFlashDrive(FlashDrive drive){
			this.drive = drive;
		}
		
		protected abstract void drive();
	}
	
	public static class OmniControlInterface extends DriveControlInterface{
		private Stick stick;
		
		public OmniControlInterface(Stick stick){
			this.stick = stick;
		}
		
		protected void drive(){
			drive.omniDrive(stick);
		}
	}
	
	public static class TankControlInterface extends DriveControlInterface{
		private Stick stick_l, stick_r;
		
		public TankControlInterface(Stick stickL, Stick stickR){
			this.stick_l = stickL;
			this.stick_r = stickR;
		}
		
		protected void drive(){
			drive.tankDrive(stick_r, stick_l);
		}
	}
	
	public static class ArcadeControlInterface extends DriveControlInterface{
		private Stick stick;
		
		public ArcadeControlInterface(Stick stick){
			this.stick = stick;
		}
		
		protected void drive(){
			drive.arcadeDrive(stick);
		}
	}
	
	public static enum MotorSide{
		Front, Rear, Right, Left
	}
	
	public final Action FORWARD = new SystemAction(this, new Action(){
		@Override
		public void execute() {forward();}
		@Override
		public void end() { stop();}
	});
	public final Action BACKWARD = new SystemAction(this, new Action(){
		@Override
		public void execute() {backward();}
		@Override
		public void end() { stop();}
	});
	public final Action RIGHT = new SystemAction(this, new Action(){
		@Override
		public void execute() {right();}
		@Override
		public void end() { stop();}
	});
	public final Action LEFT = new SystemAction(this, new Action(){
		@Override
		public void execute() {left();}
		@Override
		public void end() { stop();}
	});
	public final Action ROTATE_RIGHT = new SystemAction(this, new Action(){
		@Override
		public void execute() {rotateRight();}
		@Override
		public void end() { stop();}
	});
	public final Action ROTATE_LEFT = new SystemAction(this, new Action(){
		@Override
		public void execute() {rotateLeft();}
		@Override
		public void end() { stop();}
	});
	public final Action BRAKE = new SystemAction(this, new Action(){
		@Override
		public void initialize() { stop();}
		@Override
		public void execute() {}
		@Override
		public void end() {}
		@Override
		public boolean isFinished() {return true;}
	});
	
	private FlashSpeedController right_controllers;
	private FlashSpeedController left_controllers;
	private FlashSpeedController front_controllers;
	private FlashSpeedController rear_controllers;
	
	private InterfaceAction interface_action;
	
	private double speed_limit = 1;
	private double minSpeed = 0.0;
	private double default_speed = 0.5;
	
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
		super(null);
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
	
	public FlashSpeedController getControllers(MotorSide s){
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
	
	public void setSpeedLimit(double limit){
		speed_limit = Math.abs(limit);
	}
	public double getSpeedLimit(){
		return speed_limit;
	}
	
	public void setMinSpeed(double limit){
		minSpeed = Math.abs(limit);
	}
	public double getMinSpeed(){
		return minSpeed;
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
	 * Arcade drive implements a single joystick drive. Given move and rotate speed values, the code sets the values 
	 * to move the robot. The move value is responsible for moving the robot forward and backward while the 
	 * rotate value is responsible for the robot rotation. 
	 * 
	 * @param moveValue The value to move forward or backward 1 to -1.
	 * @param rotateValue The value to rotate right or left 1 to -1.
	 */
	@Override
	public void arcadeDrive(double moveValue, double rotateValue){
		double rSpeed = 0, lSpeed = 0;
		
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
		
		setMotors(0, rSpeed, lSpeed, 0);
	}
	
	/**
	 * Arcade drive implements a single joystick drive. Given move and rotate speed values, the code sets the values 
	 * to move each side separately. The move value is responsible for moving the robot forward and backward while the 
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
	
	public void vectoredTankDrive(double moveValue, double rotateValue){
		double right = moveValue, left = moveValue;
		
		if(rotateValue > 0){
			rotateValue = Math.abs(rotateValue);
			right -= rotateValue;
			left += rotateValue;
		}
		else if(rotateValue < 0){
			rotateValue = Math.abs(rotateValue);
			right += rotateValue;
			left -= rotateValue;
		}
		
		setMotors(0, right, left, 0);
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
	
	public void vectoredOmniDrive_Cartesian(double y, double x, double rotate){
		double right = 0, left = 0, front = 0, rear = 0;
		
		if (y > 0.0) {
  	      	if (rotate > 0.0) {
  	    	  left = y - rotate;
  	    	  right = Math.max(y, rotate);
  	      	} else {
  	    	  left = Math.max(y, -rotate);
  	    	  right = y + rotate;
  	      	}
  	    } else {
  	    	if (rotate > 0.0) {
  	    	    left = -Math.max(-y, rotate);
  	    	    right = y + rotate;
  	    	} else {
  	    		left = y - rotate;
  	    		right = -Math.max(-y, -rotate);
  	    	}
  	    }
			
		
		if (x > 0.0) {
			if (rotate > 0.0) {
  	    	  rear = x - rotate;
  	    	  front = Math.max(x, rotate);
  	      	} else {
  	    	  rear = Math.max(x, -rotate);
  	    	  front = x + rotate;
  	      	}
  	    }else{
	    	if (rotate > 0.0) {
	    		rear = -Math.max(-x, rotate);
  	    	    front = x + rotate;
  	    	} else {
  	    		rear = x - rotate;
  	    		front = -Math.max(-x, -rotate);
  	    	}
  	    }
		
		setMotors(front, right, left, rear);
	}
	
	@Override
	public void driveY(double speed, boolean direction){
		if(direction) forward(speed);
		else backward(speed);
	}
	
	public void forward(double r, double l){
		setMotors(0, r, l, 0);
	}
	@Override 
	public void forward(double speed){
		forward(speed, speed);
	}
	public void forward(){
		forward(default_speed);
	}
	
	public void backward(double r, double l){
		setMotors(0, -r, -l, 0);
	}
	@Override 
	public void backward(double speed){
		backward(speed, speed);
	}
	public void backward(){
		backward(default_speed);
	}
	
	@Override
	public void driveX(double speed, boolean direction){
		if(direction) right(speed);
		else left(speed);
	}
	
	public void right(double f, double r){
		setMotors(f, 0, 0, r);
	}
	@Override 
	public void right(double speed){
		right(speed, speed);
	}
	public void right(){
		right(default_speed);
	}
	
	public void left(double f, double r){
		setMotors(-f, 0, 0, -r);
	}
	@Override 
	public void left(double speed){
		left(speed, speed);
	}
	public void left(){
		left(default_speed);
	}
	/**
	 * Implements a joystick-free rotation movement at a given speed in a given direction. If side motors are defined then they
	 * will be used as well.
	 * 
	 * @param speed The speed in which to rotate, between 0 and 1.
	 * @param direction The direction in which to rotate. 1 for right, -1 for left
	 */
	@Override 
	public void rotate(double speed, boolean direction){
		if(!direction) 
			speed = -speed;
		
		setMotors(-speed, -speed, speed, speed);
	}
	
	/*public void rotate(int angle){
		rotate(angle, speed_limit);
	}
	public void rotate(int angle, double speed){
		speed = Math.abs(limit(speed));
		new AbsoluteRotateAction(this, gyro, angle, speed).start();
	}
	public void relativeRotate(int angle){
		relativeRotate(angle, speed_limit);
	}
	public void relativeRotate(int angle, double speed){
		speed = Math.abs(limit(speed));
		new RelativeRotateAction(this, gyro, angle, speed).start();
	}*/
	
	/**
	 * Implements a joystick-free right rotation movement at a given speed. If side motors are defined then they
	 * will be used as well.
	 * 
	 * @param speed The speed in which to rotate right, between 0 and 1.
	 */
	@Override 
	public void rotateRight(double speed){
		rotate(speed, true);
	}
	public void rotateRight(){
		rotateRight(default_speed);
	}
	/**
	 * Implements a joystick-free left rotation movement at a given speed. If side motors are defined then they
	 * will be used as well.
	 * 
	 * @param speed The speed in which to rotate left, between 0 and 1.
	 */
	@Override 
	public void rotateLeft(double speed){
		rotate(speed, false); 
	}
	public void rotateLeft(){
		rotateLeft(default_speed);
	}
	
	@Override
	public void holonomicCartesian(double x, double y, double rotation) {
		//implement for omni drive
	}
	@Override
	public void holonomicPolar(double magnitude, double direction, double rotation) {
		//implement for omni drive
	}
	
	
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
	 * Sets all the motors to speed 0, thus stopping the motors.
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
		if(Math.abs(speed) < minSpeed)
			return 0.0;
		
		speed *= speed_limit;
		if(speed > speed_limit) speed = speed_limit;
		else if(speed < -speed_limit) speed = -speed_limit;
		return speed;
	}
	
	public void startControlInterface(){
		if(interface_action == null) return;
		if(interface_action.isRunning()) interface_action.cancel();
		interface_action.start();
	}
	public void startControlInterface(DriveControlInterface controlInterface){
		if(interface_action != null && interface_action.isRunning()) 
			interface_action.cancel();
		setControlInterface(controlInterface);
		interface_action.start();
	}
	public void setControlInterface(DriveControlInterface controlInterface){
		controlInterface.setFlashDrive(this);
		interface_action = new InterfaceAction(this, controlInterface);
	}
	public void setDefaultInterface(DriveControlInterface controlInterface){
		setControlInterface(controlInterface);
		setDefaultAction(interface_action);
	}

	@Override
	public System getSystem() {
		return this;
	}

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
}