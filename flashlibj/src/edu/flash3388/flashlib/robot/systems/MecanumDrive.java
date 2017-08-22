package edu.flash3388.flashlib.robot.systems;

import edu.flash3388.flashlib.math.Mathf;
import edu.flash3388.flashlib.robot.FlashRoboUtil;
import edu.flash3388.flashlib.robot.PidController;
import edu.flash3388.flashlib.robot.PidSource;
import edu.flash3388.flashlib.robot.SubSystem;
import edu.flash3388.flashlib.robot.devices.FlashSpeedController;
import edu.flash3388.flashlib.robot.devices.Gyro;
import edu.flash3388.flashlib.robot.devices.ModableMotor;
import edu.flash3388.flashlib.robot.hid.Stick;
import edu.flash3388.flashlib.util.beans.DoubleProperty;
import edu.flash3388.flashlib.util.beans.SimpleDoubleProperty;

/**
 * Implements the Mecanum drive system. Mecanum drive is a specialized holonomic system which uses 4
 * unique wheels for 360-vectored motion, making this system extremely maneuverable.
 * 
 * <p>
 * The Mecanum wheel is a design for a wheel which can move a vehicle in any direction. It is sometimes called the 
 * Ilon wheel after its Swedish inventor, Bengt Ilon, who came up with the idea in 1973 when he was an engineer with 
 * the Swedish company Mecanum AB.
 * </p>
 * 
 * <p>
 * Mecanum drive is a type of holonomic drive base; meaning that it applies the force of the wheel at
 * a 45 angle to the robot instead of on one of its axes. By applying the force at an angle to the robot, you
 * can vary the magnitude of the force vectors to gain translational control of the robot; aka, the robot can
 * move in any direction while keeping the front of the robot in a constant compass direction. This differs
 * from the basic robot drive systems like arcade drive, tank drive, or shopping cart drive require you to
 * turn the front of the robot to travel in another direction.
 * </p>
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 * @see <a href="https://en.wikipedia.org/wiki/Mecanum_wheel">https://en.wikipedia.org/wiki/Mecanum_wheel</a>
 * @see <a href="http://thinktank.wpi.edu/resources/346/ControllingMecanumDrive.pdf">http://thinktank.wpi.edu/resources/346/ControllingMecanumDrive.pdf</a>
 */
public class MecanumDrive extends SubSystem implements HolonomicDriveSystem {

	/**
	 * Because the Mecanum drive is so sensitive to weight distributions and motor output differences, it
	 * is sometimes needed to stabilize it by modifying the output to compensate for those differences. 
	 * MecanumStabilizer is an interface for algorithms designed to do so.
	 * 
	 * @author Tom Tzook
	 * @since FlashLib 1.0.0
	 */
	public static interface MecanumStabilizer {
		
		/**
		 * Stabilizes the mecanum driving values. Implementation is user-dependent.
		 * 
		 * @param magnitude the given magnitude of the motion vector currently used by the mecanum drive [0...1]
		 * @param direction the given direction of the motion vector currently used by the mecanum drive [0...360]
		 * @param rotation the given rotation of the mecanum drive [-1...1]
		 * @return an array containing the magnitude, direction and rotation values to move the drive with, or
		 * 			null if unable to calculate
		 */
		double[] stabilize(double magnitude, double direction, double rotation);
	}
	/**
	 * Implementation of {@link MecanumStabilizer} using a PID controller and valid only when
	 * the user does not attempt rotation.
	 * 
	 * @author Tom Tzook
	 * @since FlashLib 1.0.1
	 */
	public static class PidMecanumStabilizer implements MecanumStabilizer{

		private PidController pidcontroller;
		private Gyro gyro;
		private DoubleProperty setPoint = new SimpleDoubleProperty();
		private double[] values = new double[3];
		
		public PidMecanumStabilizer(double kp, double ki, double kd, double kf, Gyro gyro){
			this.pidcontroller = new PidController(kp, ki, kd, kf);
			this.pidcontroller.setPIDSource(new PidSource.GyroPidSource(gyro));
			this.pidcontroller.setEnabled(true);
			this.gyro = gyro;
		}
		
		public PidController getPidController(){
			return pidcontroller;
		}
		
		@Override
		public double[] stabilize(double magnitude, double direction, double rotation) {
			if(rotation != 0)
				return null;
			if(values[0] != magnitude && values[1] != direction){
				values[0] = magnitude;
				values[1] = direction;
				setPoint.set(gyro.getAngle());
			}
			
			values[2] = pidcontroller.calculate();
			return values;
		}
	}

	private FlashSpeedController front_left;
	private FlashSpeedController rear_left;
	private FlashSpeedController front_right;
	private FlashSpeedController rear_right;

	private MecanumStabilizer stabilizer;
	private boolean stabilizing = false, voltageScaling = false;
	private double sensitivityLimit = 0;
	private double speed_limit = 1.0;
	private double minSpeed = 0.0;
	private int angleRound = 0;

	/**
	 * Creates a new mecanum drive.
	 * 
	 * @param right_front front right motor controller
	 * @param right_back rear right motor controller
	 * @param left_front front left motor controller
	 * @param left_back rear left motor controller
	 */
	public MecanumDrive(FlashSpeedController right_front, FlashSpeedController right_back, FlashSpeedController left_front,
			FlashSpeedController left_back) {
		super("");
		front_right = right_front;
		rear_right = right_back;
		front_left = left_front;
		rear_left = left_back;
		enableBrakeMode(false);
	}
	
	/**
	 * Gets the stabilizer used by this drive.
	 * @return the mecanum stabilizer
	 */
	public MecanumStabilizer getStabilizer() {
		return stabilizer;
	}
	/**
	 * Sets the mecanum stabilizer implementation to use when stabilizing this drive system
	 * @param stabilizer the stabilizer
	 */
	public void setStabilizer(MecanumStabilizer stabilizer){
		this.stabilizer = stabilizer;
	}
	
	/**
	 * Sets whether or not to use the stabilizer to compensate for instabilities in the drive system.
	 * @param en true to enable, false otherwise
	 */
	public void enableStabilizing(boolean en) {
		this.stabilizing = en;
	}
	/**
	 * Gets whether or not to use the stabilizer to compensate for instabilities in the drive system.
	 * @return true if enabled, false otherwise
	 */
	public boolean isStabilizing() {
		return stabilizing;
	}
	/**
	 * Sets the minimum speed of the system. If the set speed for a motor does not exceeds this value, 
	 * it is decreased to 0.
	 * @param s speed limit [0...1]
	 */
	public void setSensitivityLimit(double s) {
		this.sensitivityLimit = Math.abs(s);
	}
	/**
	 * Gets the minimum speed of the system. If the set speed for a motor does not exceeds this value, 
	 * it is decreased to 0.
	 * @return speed limit [0...1]
	 */
	public double getSensitivityLimit() {
		return sensitivityLimit;
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
	 * Gets the current angle round. Angle rounding rounds the given motion vector direction to a
	 * multiplier of the given rounding value.
	 * @return multiplier for rounding value.
	 */
	public int getAngleRounding() {
		return angleRound;
	}
	/**
	 * Sets the current angle round. Angle rounding rounds the given motion vector direction to a
	 * multiplier of the given rounding value.
	 * @param round multiplier for rounding value.
	 */
	public void setAngleRounding(int round) {
		angleRound = round;
	}
	
	/**
	 * Sets values for all speed controllers. Limits them first according to set parameters.
	 * 
	 * @param fr value for forward right
	 * @param rr value for rear right
	 * @param fl value for front left
	 * @param rl value for rear left
	 */
	public void setMotors(double fr, double rr, double fl, double rl){
		fr = limit(fr);
		rr = limit(rr);
		fl = limit(fl);
		rl = limit(rl);
		
		front_right.set(fr);
		front_left.set(fl);
		rear_right.set(rr);
		rear_left.set(rl);
	}
	
	/**
	 * Drives the mecanum system using a cartesian vector. Calculates the motor outputs and 
	 * uses the rotation value to set rotation of the system. 
	 * 
	 * @param x the x-axis value of the vector [-1...1]
	 * @param y the y-axis value of the vector [-1...1]
	 * @param rotation rotation value [-1...1], -1 for left, 1 for right
	 */
	public void mecanumDrive_cartesian(double x, double y, double rotation) {
		mecanumDrive_polar(Math.sqrt(x * x + y * y), Math.toDegrees(Math.atan2(x, y)), rotation);
	}
	/**
	 * Drives the mecanum system using a polar vector. Calculates the motor outputs and 
	 * uses the rotation value to set rotation of the system. If the magnitude is smaller than the
	 * minimum speed, it will be set to 0. If the rotation is smaller than the minimum speed, it will be set
	 * to 0.
	 * 
	 * @param magnitude the magnitude of the vector [0...1]
	 * @param direction the direction of the vector in degrees [0...360]
	 * @param rotation rotation value [-1...1], -1 for left, 1 for right
	 */
	public void mecanumDrive_polar(double magnitude, double direction, double rotation) {
		if (magnitude < sensitivityLimit)
			magnitude = 0.0;
		if (Math.abs(rotation) < sensitivityLimit)
			rotation = 0.0;

		if (angleRound != 0) 
			direction = roundAngle(direction);
		
		magnitude = magnitude * Math.sqrt(2.0);
		
		if (stabilizing && stabilizer != null){
			double[] result = stabilizer.stabilize(magnitude, direction, rotation);
			if(result != null && result.length > 0){
				magnitude = result[0];
				if(result.length > 1)
					direction = result[1];
				if(result.length > 2)
					rotation = result[2];
			}
		}
		
		double dirInRad = Math.toRadians(direction + 45.0);
		double cosD = Math.cos(dirInRad);
		double sinD = Math.sin(dirInRad);
		
		double wheelSpeeds[] = { 
				(sinD * magnitude + rotation), // front left
				(cosD * magnitude - rotation), // front right
				(cosD * magnitude + rotation), // rear left
				(sinD * magnitude - rotation),// rear right
		};

		normalize(wheelSpeeds);

		setMotors(-wheelSpeeds[1], -wheelSpeeds[3], wheelSpeeds[0], wheelSpeeds[2]);
	}
	/**
	 * Drives the mecanum system using a polar vector. Calculates the motor outputs and 
	 * uses the rotation value to set rotation of the system. If the magnitude is smaller than the
	 * minimum speed, it will be set to 0. If the rotation is smaller than the minimum speed, it will be set
	 * to 0. The given joystick provides magnitude and angle for the vector, but the rotation is 0.
	 * 
	 * @param stick controlling joystick
	 */
	public void mecanumDrive_polar(Stick stick) {
		mecanumDrive_polar(stick.getMagnitude(), stick.getAngle(), 0);
	}
	/**
	 * Drives the mecanum system using a polar vector. Calculates the motor outputs and 
	 * uses the rotation value to set rotation of the system. If the magnitude is smaller than the
	 * minimum speed, it will be set to 0. If the rotation is smaller than the minimum speed, it will be set
	 * to 0. The given joystick provides magnitude and angle for the vector, the second stick
	 * provides vale for rotation using the x-axis.
	 * 
	 * @param stick controlling joystick
	 * @param stickR rotation joystick
	 */
	public void mecanumDrive_polar(Stick stick, Stick stickR) {
		mecanumDrive_polar(stick.getMagnitude(), stick.getAngle(), stickR.getX());
	}
	
	/**
	 * Drives the mecanum system using a polar vector. Calculates the motor outputs and 
	 * uses the rotation value to set rotation of the system. If the magnitude is smaller than the
	 * minimum speed, it will be set to 0. If the rotation is smaller than the minimum speed, it will be set
	 * to 0. Uses a given gyro angle, to allow field-oriented drive.
	 * 
	 * <p>
	 * Field oriented drive allows drives to move the robot towards the same direction no matter its rotation.
	 * The forward is determined by the reset direction of the gyro.
	 * </p>
	 * 
	 * @param magnitude the magnitude of the vector [0...1]
	 * @param direction the direction of the vector in degrees [0...360]
	 * @param rotation rotation value [-1...1], -1 for left, 1 for right
	 * @param gyroAngle the current gyro angle in degrees
	 */
	public void mecanumDrive_polar(double magnitude, double direction, double rotation, double gyroAngle) {
		mecanumDrive_polar(magnitude, direction + gyroAngle, rotation);
	}
	/**
	 * Drives the mecanum system using a polar vector. Calculates the motor outputs and 
	 * uses the rotation value to set rotation of the system. If the magnitude is smaller than the
	 * minimum speed, it will be set to 0. If the rotation is smaller than the minimum speed, it will be set
	 * to 0. Uses a given gyro angle, to allow field-oriented drive.
	 * 
	 * <p>
	 * Field oriented drive allows drives to move the robot towards the same direction no matter its rotation.
	 * The forward is determined by the reset direction of the gyro.
	 * </p>
	 * 
	 * @param magnitude the magnitude of the vector [0...1]
	 * @param direction the direction of the vector in degrees [0...360]
	 * @param rotation rotation value [-1...1], -1 for left, 1 for right
	 * @param gyro the rotation gyro sensor
	 */
	public void mecanumDrive_polar(double magnitude, double direction, double rotation, Gyro gyro) {
		mecanumDrive_polar(magnitude, direction, rotation, gyro.getAngle());
	}
	/**
	 * Drives the mecanum system using a polar vector. Calculates the motor outputs and 
	 * uses the rotation value to set rotation of the system. If the magnitude is smaller than the
	 * minimum speed, it will be set to 0. If the rotation is smaller than the minimum speed, it will be set
	 * to 0. Uses a given gyro angle, to allow field-oriented drive. The given joystick provides magnitude and angle 
	 * for the vector, but the rotation is 0.
	 * 
	 * <p>
	 * Field oriented drive allows drives to move the robot towards the same direction no matter its rotation.
	 * The forward is determined by the reset direction of the gyro.
	 * </p>
	 * 
	 * @param stick controlling joystick
	 * @param gyro the rotation gyro sensor
	 */
	public void mecanumDrive_polar(Stick stick, Gyro gyro) {
		mecanumDrive_polar(stick.getMagnitude(), stick.getAngle(), 0, gyro);
	}
	/**
	 * Drives the mecanum system using a polar vector. Calculates the motor outputs and 
	 * uses the rotation value to set rotation of the system. If the magnitude is smaller than the
	 * minimum speed, it will be set to 0. If the rotation is smaller than the minimum speed, it will be set
	 * to 0. Uses a given gyro angle, to allow field-oriented drive. The given joystick provides magnitude and angle for the vector, the second stick
	 * provides vale for rotation using the x-axis.
	 * 
	 * <p>
	 * Field oriented drive allows drives to move the robot towards the same direction no matter its rotation.
	 * The forward is determined by the reset direction of the gyro.
	 * </p>
	 * 
	 * @param stick controlling joystick
	 * @param stickR rotation joystick
	 * @param gyro the rotation gyro sensor
	 */
	public void mecanumDrive_polar(Stick stick, Stick stickR, Gyro gyro) {
		mecanumDrive_polar(stick.getMagnitude(), stick.getAngle(), stickR.getX(), gyro);
	}
	/**
	 * Drives the mecanum system using a polar vector. Calculates the motor outputs and 
	 * uses the rotation value to set rotation of the system. If the magnitude is smaller than the
	 * minimum speed, it will be set to 0. If the rotation is smaller than the minimum speed, it will be set
	 * to 0. Uses a given gyro angle, to allow field-oriented drive. The given joystick provides magnitude and angle for the vector, the second stick
	 * provides vale for rotation using the x-axis.
	 * 
	 * <p>
	 * Field oriented drive allows drives to move the robot towards the same direction no matter its rotation.
	 * The forward is determined by the reset direction of the gyro.
	 * </p>
	 * 
	 * @param stick controlling joystick
	 * @param stickR rotation joystick
	 * @param gyroAngle the current gyro angle in degrees
	 */
	public void mecanumDrive_polar(Stick stick, Stick stickR, double gyroAngle) {
		mecanumDrive_polar(stick.getMagnitude(), stick.getAngle(), stickR.getX(), gyroAngle);
	}

	
	private double roundAngle(double angle) {
		return Mathf.roundToMultiplier(angle, angleRound);
	}
	private double limit(double speed){
		if(voltageScaling)
			speed = FlashRoboUtil.scaleVoltageBus(speed);
		
		if(Math.abs(speed) < minSpeed)
			return 0.0;
			
		if(speed_limit != 1.0)
			speed = Mathf.constrain(speed * speed_limit, -speed_limit, speed_limit);
		return speed;
	}
	private void normalize(double wheelSpeeds[]) {
		double maxMagnitude = Math.abs(wheelSpeeds[0]);
		for (int i = 1; i < 4; i++) {
			double temp = Math.abs(wheelSpeeds[i]);
			if (maxMagnitude < temp)
				maxMagnitude = temp;
		}
		if (maxMagnitude > 1.0) {
			for (int i = 0; i < 4; i++)
				wheelSpeeds[i] = wheelSpeeds[i] / maxMagnitude;
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void rotate(double speed, boolean direction) {
		mecanumDrive_polar(0, 0, direction? speed : -speed);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void rotateRight(double speed) {
		mecanumDrive_polar(0, 0, speed);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void rotateLeft(double speed) {
		mecanumDrive_polar(0, 0, -speed);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void driveY(double speed, boolean direction) {
		if (direction)
			forward(speed);
		else if (direction)
			backward(speed);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void forward(double speed) {
		mecanumDrive_polar(speed, 0, 0);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void backward(double speed) {
		mecanumDrive_polar(speed, 180, 0);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void driveX(double speed, boolean direction) {
		if (direction)
			right(speed);
		else if (direction)
			left(speed);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void right(double speed) {
		mecanumDrive_polar(speed, 90, 0);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void left(double speed) {
		mecanumDrive_polar(speed, 270, 0);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void holonomicCartesian(double x, double y, double rotation) {
		mecanumDrive_cartesian(x, y, rotation);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void holonomicPolar(double magnitude, double direction, double rotation) {
		mecanumDrive_polar(magnitude, direction, rotation);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void stop() {
		front_left.set(0);
		front_right.set(0);
		rear_right.set(0);
		rear_left.set(0);
	}
	/**
	 * {@inheritDoc}
	 * <p>
	 * Returns this instance.
	 * </p>
	 */
	@Override
	public SubSystem getSystem() {
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void enableBrakeMode(boolean mode) {
		if(front_left instanceof ModableMotor)
			((ModableMotor)front_left).enableBrakeMode(mode);
		if(front_right instanceof ModableMotor)
			((ModableMotor)front_right).enableBrakeMode(mode);
		if(rear_right instanceof ModableMotor)
			((ModableMotor)rear_right).enableBrakeMode(mode);
		if(rear_left instanceof ModableMotor)
			((ModableMotor)rear_left).enableBrakeMode(mode);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean inBrakeMode() {
		return (front_left instanceof ModableMotor && ((ModableMotor)front_left).inBrakeMode()) && 
			   (front_right instanceof ModableMotor && ((ModableMotor)front_right).inBrakeMode()) &&
			   (rear_right instanceof ModableMotor && ((ModableMotor)rear_right).inBrakeMode()) && 
			   (rear_left instanceof ModableMotor && ((ModableMotor)rear_left).inBrakeMode());
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isVoltageScaling() {
		return voltageScaling;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void enableVoltageScaling(boolean en) {
		voltageScaling = en;
	}
}
