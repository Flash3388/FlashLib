package edu.flash3388.flashlib.robot.systems;

import edu.flash3388.flashlib.robot.Action;
import edu.flash3388.flashlib.robot.Direction;
import edu.flash3388.flashlib.robot.System;
import edu.flash3388.flashlib.robot.SystemAction;
import edu.flash3388.flashlib.robot.devices.FlashSpeedController;
import edu.flash3388.flashlib.robot.devices.Gyro;
import edu.flash3388.flashlib.robot.hid.Stick;
import edu.wpi.first.wpilibj.Joystick;

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
public class FlashDrive extends System implements TankDriveSystem{
	
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
	
	/**
	 * This class allows for representing the sides where motors are placed.
	 * 
	 * @author Tom Tzook
	 */
	public static class MotorSide {
		public final int value;
		static final int LEFT = 0;
		static final int RIGHT = 1;
		static final int FRONT = 2;
		static final int REAR = 3;
		
		/**
		 * Represents the left side of the robot.
		 */
		public static final MotorSide Left = new MotorSide(LEFT);

		/**
		 * Represents the right side of the robot.
		 */
		public static final MotorSide Right = new MotorSide(RIGHT);

		/**
		 * Represents the front side of the robot.
		 */
		public static final MotorSide Front = new MotorSide(FRONT);

		/**
		 * Represents the rear side of the robot.
		 */
		public static final MotorSide Rear = new MotorSide(REAR);
		
		private MotorSide(int v){ value = v;}
		
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
	private Gyro gyro;
	
	private boolean brake = false;
	private double speed_limit = 1;
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
	
	
	public void setGyro(Gyro gyro){
		this.gyro = gyro;
	}
	public double getAngle(){
		if(gyro == null)
			return -1;
		return gyro.getAngle();
	}
	
	/**
	 * Sets whether all the motors in a side are inverted or not (forward is backward and other wise).
	 * 
	 * @param s The position of the motors on the robot as an instance of the MotorSide class.
	 * @param inverted A boolean representing whether the motors are inverted or not.
	 */
	public void setInverted(MotorSide s, boolean inverted){
		switch(s.value){
			case MotorSide.LEFT:
				left_controllers.setInverted(inverted);
				break;
			case MotorSide.RIGHT:
				right_controllers.setInverted(inverted);
				break;
			case MotorSide.FRONT:
				front_controllers.setInverted(inverted);
				break;
			case MotorSide.REAR:
				rear_controllers.setInverted(inverted);
				break;
		}
	}
	
	public FlashSpeedController getControllers(MotorSide s){
		switch(s.value){
			case MotorSide.LEFT:
				return left_controllers;
			case MotorSide.RIGHT:
				return right_controllers;
			case MotorSide.FRONT:
				return front_controllers;
			case MotorSide.REAR:
				return rear_controllers;
			default: return null;
		}
	}
	
	public void enableBrake(boolean enable){
		brake = enable;
		if(brake) stop();
	}
	public void setSpeedLimit(double limit){
		speed_limit = Math.abs(limit);
	}
	public double getSpeedLimit(){
		return speed_limit;
	}
	
	/**
	 * Tank drive implements a dual joystick drive. Given right and left speed values, the code sets the values to move 
	 * each side separately.
	 * 
	 * @param right The speed value of the right side of motors 1 to -1.
	 * @param left The speed value of the left side of motors 1 to -1.
	 */
	public void tankDrive(double right, double left){
		if(brake) return;
		
		right = limit(right);
		left = limit(left);
		
		if(right_controllers != null) right_controllers.set(right);
		if(left_controllers != null) left_controllers.set(left);
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
		tankDrive(-stick_right.getY(), -stick_left.getY());
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
		tankDrive(-stick_right.getY(), -stick_left.getY(), squared);
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
	public void tankDrive(Joystick stick_right, int right_axis, Joystick stick_left, int left_axis){
		tankDrive(-stick_right.getRawAxis(right_axis), -stick_left.getRawAxis(left_axis));
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
	public void tankDrive(Joystick stick_right, int right_axis, Joystick stick_left, int left_axis, boolean squared){
		tankDrive(-stick_right.getRawAxis(right_axis), -stick_left.getRawAxis(left_axis), squared);
	}
	
	/**
	 * Arcade drive implements a single joystick drive. Given move and rotate speed values, the code sets the values 
	 * to move the robot. The move value is responsible for moving the robot forward and backward while the 
	 * rotate value is responsible for the robot rotation. When both values are not zero then the value taken is the
	 * absolute bigger.
	 * 
	 * @param moveValue The value to move forward or backward 1 to -1.
	 * @param rotateValue The value to rotate right or left 1 to -1.
	 */
	public void arcadeDrive(double moveValue, double rotateValue){
		if(brake) return;
		
		moveValue = limit(moveValue);
		rotateValue = limit(rotateValue);
		
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
		
		if(right_controllers != null) right_controllers.set(rSpeed);
		if(left_controllers != null) left_controllers.set(lSpeed);
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
		arcadeDrive(-stick.getY(), -stick.getX());
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
		arcadeDrive(-stick.getY(), -stick.getX(), squared);
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
	public void arcadeDrive(Joystick stick, int move_axis, int rotate_axis){
		arcadeDrive(-stick.getRawAxis(move_axis), -stick.getRawAxis(rotate_axis));
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
	public void arcadeDrive(Joystick stick, int move_axis, int rotate_axis, boolean squared){
		arcadeDrive(-stick.getRawAxis(move_axis), -stick.getRawAxis(rotate_axis), squared);
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
		if(brake) return;
		
		y = limit(y);
		x = limit(x);
		
		if(right_controllers != null) right_controllers.set(y);
		if(left_controllers != null) left_controllers.set(y);
		if(front_controllers != null) front_controllers.set(x);
		if(rear_controllers != null) rear_controllers.set(x);
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
		omniDrive(-stick.getY(), -stick.getX());
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
		omniDrive(-stick.getY(), -stick.getX(), squared);
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
	public void omniDrive(Joystick stick, int move_axis, int side_axis){
		omniDrive(-stick.getRawAxis(move_axis), -stick.getRawAxis(side_axis));
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
	public void omniDrive(Joystick stick, int move_axis, int side_axis, boolean squared){
		omniDrive(-stick.getRawAxis(move_axis), -stick.getRawAxis(side_axis), squared);
	}
	
	@Override
	public void driveY(double speed, int direction){
		if(direction > 0) forward(speed);
		else if(direction < 0) backward(speed);
	}
	
	@Override
	public void forward(double r, double l){
		if(brake) return;
		
		r = limit(r);
		l = limit(l);
		
		if(right_controllers != null) right_controllers.set(r);
		if(left_controllers != null) left_controllers.set(l);
	}
	@Override 
	public void forward(double speed){
		forward(speed, speed);
	}
	public void forward(){
		forward(default_speed);
	}
	
	@Override
	public void backward(double r, double l){
		if(brake) return;
		
		r = limit(r);
		l = limit(l);
		
		if(right_controllers != null) right_controllers.set(r * Direction.BACKWARD);
		if(left_controllers != null) left_controllers.set(l * Direction.BACKWARD);
	}
	@Override 
	public void backward(double speed){
		backward(speed, speed);
	}
	public void backward(){
		backward(default_speed);
	}
	
	@Override
	public void driveX(double speed, int direction){
		if(direction > 0) right(speed);
		else if(direction < 0) left(speed);
	}
	
	/**
	 * Implements a joystick-free right movement at a given speed. If no motors are capable of moving
	 * right then nothing will happen.
	 * 
	 * @param speed The speed in which to move right, between 0 and 1.
	 */
	public void right(double f, double r){
		if(brake) return;
		
		f = limit(f);
		r = limit(r);
		
		if(front_controllers != null) front_controllers.set(f * Direction.FORWARD);
		if(rear_controllers != null) rear_controllers.set(r * Direction.FORWARD);
	}
	@Override 
	public void right(double speed){
		right(speed, speed);
	}
	public void right(){
		right(default_speed);
	}
	/**
	 * Implements a joystick-free left movement at a given speed. If no motors are capable of moving
	 * left then nothing will happen.
	 * 
	 * @param speed The speed in which to move left, between 0 and 1.
	 */
	public void left(double f, double r){
		if(brake) return;
		
		f = limit(f);
		r = limit(r);
		
		if(front_controllers != null) front_controllers.set(f * Direction.BACKWARD);
		if(rear_controllers != null) rear_controllers.set(r * Direction.BACKWARD);
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
	public void rotate(double speed, int direction){
		if(brake) return;
		
		speed = limit(speed);
		direction = (direction != 1 && direction != -1)? direction/Math.abs(direction) : direction;
		
		if(right_controllers != null) right_controllers.set(speed * direction * Direction.BACKWARD);
		if(left_controllers != null) left_controllers.set(speed * direction * Direction.FORWARD);
		
		if(front_controllers != null) front_controllers.set(speed * direction * Direction.BACKWARD); 
		if(rear_controllers != null) rear_controllers.set(speed * direction * Direction.FORWARD); 
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
		if(brake) return;
		
		speed = limit(speed);
		
		if(right_controllers != null) right_controllers.set(speed * Direction.BACKWARD);
		if(left_controllers != null) left_controllers.set(speed * Direction.FORWARD);
		if(front_controllers != null) front_controllers.set(speed * Direction.BACKWARD); 
		if(rear_controllers != null) rear_controllers.set(speed * Direction.FORWARD); 
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
		if(brake) return;
		
		speed = limit(speed);
		
		if(right_controllers != null) right_controllers.set(speed * Direction.FORWARD);
		if(left_controllers != null) left_controllers.set(speed * Direction.BACKWARD);
		if(front_controllers != null) front_controllers.set(speed * Direction.FORWARD); 
		if(rear_controllers != null) rear_controllers.set(speed * Direction.BACKWARD); 
	}
	public void rotateLeft(){
		rotateLeft(default_speed);
	}
	/**
	 * Sets all the motors to speed 0, thus stopping the motors.
	 */
	@Override 
	public void stop(){
		if(right_controllers != null) right_controllers.stop();
		if(left_controllers != null) left_controllers.stop();
		if(front_controllers != null) front_controllers.stop(); 
		if(rear_controllers != null) rear_controllers.stop(); 
	}
	
	

	public void setDefaultAction(Action action){
		super.setDefaultAction(action);
	}
	
	private double limit(double speed){
		//return (speed < -1 || speed > 1)? speed/Math.abs(speed) : speed;
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
	protected void initDefaultAction() {}

	@Override
	public System getSystem() {
		return this;
	}

	@Override
	public void enableBrakeMode(boolean mode) {
		if(front_controllers != null)
			front_controllers.enableBrakeMode(mode);
		if(left_controllers != null)
			left_controllers.enableBrakeMode(mode);
		if(right_controllers != null)
			right_controllers.enableBrakeMode(mode);
		if(rear_controllers != null)
			rear_controllers.enableBrakeMode(mode);
	}
	@Override
	public boolean inBrakeMode() {
		return (front_controllers == null || front_controllers.inBrakeMode()) && 
				(left_controllers == null || left_controllers.inBrakeMode()) &&
				(right_controllers == null || right_controllers.inBrakeMode()) && 
				(rear_controllers == null || rear_controllers.inBrakeMode());
	}
}