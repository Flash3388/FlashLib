package edu.flash3388.flashlib.robot.systems;

import edu.flash3388.flashlib.math.Mathf;
import edu.flash3388.flashlib.robot.Direction;
import edu.flash3388.flashlib.robot.FlashRoboUtil;
import edu.flash3388.flashlib.robot.System;
import edu.flash3388.flashlib.robot.devices.FlashSpeedController;
import edu.flash3388.flashlib.robot.devices.Gyro;
import edu.flash3388.flashlib.robot.devices.ModableMotor;
import edu.flash3388.flashlib.robot.hid.Stick;
import edu.flash3388.flashlib.util.FlashUtil;

/**
 * Implements the Mecanum drive system. Mecanum drive is a specialized holonomic system which uses 4
 * unique wheels for 360-vectored motion, which makes this system an extremely maneuverable one.
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
public class MecanumDrive extends System implements HolonomicDriveSystem {

	/**
	 * Because the Mecanum drive is so sensitive to weight distributions and motor output differences, it
	 * is sometimes needed to stabilize it by modifying the output to compensate for those differences. 
	 * MecanumStabilizer provides several ways to do so.
	 * 
	 * @author Tom Tzook
	 * @since FlashLib 1.0.0
	 */
	public static class MecanumStabilizer {
		private static final double DEFAULT_STRENGTH_CHANGE = 0.1;
		private static final int CHANGED_NOTHING = 0, CHANGED_WEAKER = 1, CHANGED_STRONGER = 2, WHEEL_FRONT_LEFT = 0,
				WHEEL_FRONT_RIGHT = 1, WHEEL_REAR_LEFT = 2, WHEEL_REAR_RIGHT = 3;

		private double magnitude, direction, movmentAngle, offsetMargin = 8, lastRotDir, changed = 0, baseSpeed = 0.2;
		private Gyro gyro;
		private int[] weakerWheels = new int[2], strongerWheels = new int[2];
		private int changesCount;

		public MecanumStabilizer(Gyro gyro) {
			setGyro(gyro);
		}

		public MecanumStabilizer() {
		}

		private void feedNew(double magnitude, double direction) {
			this.magnitude = magnitude;
			this.direction = direction;
			lastRotDir = 0;
			changed = CHANGED_NOTHING;
			changesCount = 0;
			movmentAngle = Mathf.limitAngle(gyro.getAngle());
			FlashUtil.getLog().log("\nStabilizer: New Feed - (" + magnitude + ", " + direction + ") - " + movmentAngle);
		}

		private boolean isDirectionSideways(double direction) {
			return (direction >= 45 && direction <= 135) || (direction >= 225 && direction <= 315);
		}

		private int getRotationDirection(double currentAngle) {
			return currentAngle > movmentAngle ? Direction.RIGHT : Direction.LEFT;
		}

		public void stabilize(double magnitude, double direction, double[] wheelSpeeds) {
			if (gyro == null)
				return;
			if (magnitude != this.magnitude || direction != this.direction)
				feedNew(magnitude, direction);
			double currentAngle = Mathf.limitAngle(gyro.getAngle());
			// FlashUtil.getLog().log("Stabilizer: CurrentAngle: "+currentAngle);
			if (currentAngle > movmentAngle - offsetMargin && currentAngle < movmentAngle + offsetMargin) {
				// FlashUtil.getLog().log("Stabilizer: Offset minimal");
				return;
			}
			// FlashUtil.getLog().log("Stabilizer: Offset Bad");
			double strengthChange = DEFAULT_STRENGTH_CHANGE;
			int rotationDir = getRotationDirection(currentAngle);
			// FlashUtil.getLog().log("Stabilizer: Rotation Dir: "+rotationDir);
			if (isDirectionSideways(direction)) {
				FlashUtil.getLog().log("Stabilizer: Sidways: TRUE");
				if (rotationDir < 0) {
					weakerWheels[0] = WHEEL_FRONT_LEFT;
					weakerWheels[1] = WHEEL_FRONT_RIGHT;
					strongerWheels[0] = WHEEL_REAR_LEFT;
					strongerWheels[1] = WHEEL_REAR_RIGHT;
				} else {
					strongerWheels[0] = WHEEL_FRONT_LEFT;
					strongerWheels[1] = WHEEL_FRONT_RIGHT;
					weakerWheels[0] = WHEEL_REAR_LEFT;
					weakerWheels[1] = WHEEL_REAR_RIGHT;
				}
			} else {
				FlashUtil.getLog().log("Stabilizer: Sideways: FALSE");
				if (rotationDir < 0) {
					weakerWheels[0] = WHEEL_FRONT_LEFT;
					weakerWheels[1] = WHEEL_REAR_LEFT;
					strongerWheels[0] = WHEEL_FRONT_RIGHT;
					strongerWheels[1] = WHEEL_REAR_RIGHT;
				} else {
					strongerWheels[0] = WHEEL_FRONT_LEFT;
					strongerWheels[1] = WHEEL_REAR_LEFT;
					weakerWheels[0] = WHEEL_FRONT_RIGHT;
					weakerWheels[1] = WHEEL_REAR_RIGHT;
				}
			}

			if (lastRotDir == 0)
				lastRotDir = rotationDir;
			else if (rotationDir != lastRotDir) {

			}

			if (changed != CHANGED_WEAKER) {
				FlashUtil.getLog().log("Stabilizer: WEAKER CHANGE");
				if (wheelSpeeds[weakerWheels[0]] != 0)
					wheelSpeeds[weakerWheels[0]] += strengthChange;
				if (wheelSpeeds[weakerWheels[1]] != 0)
					wheelSpeeds[weakerWheels[1]] += strengthChange;
				changesCount++;
				if (changesCount > 3) {
					changed = CHANGED_WEAKER;
					changesCount = 0;
				}
			} else if (changed != CHANGED_STRONGER) {
				FlashUtil.getLog().log("Stabilizer: STRONGER CHANGE");
				if (wheelSpeeds[strongerWheels[0]] != 0)
					wheelSpeeds[strongerWheels[0]] -= strengthChange;
				if (wheelSpeeds[strongerWheels[1]] != 0)
					wheelSpeeds[strongerWheels[1]] -= strengthChange;
				changesCount++;
				if (changesCount > 3) {
					changed = CHANGED_STRONGER;
					changesCount = 0;
				}
			}
		}
		public double stabilizeByRotation(double magnitude, double direction){
			if (gyro == null)
				return 0;
			if (magnitude != this.magnitude || direction != this.direction){
				feedNew(magnitude, direction);
				FlashUtil.getLog().log("Feed new");
			}
			double currentAngle = Mathf.limitAngle(gyro.getAngle());
			if (currentAngle > movmentAngle - offsetMargin && currentAngle < movmentAngle + offsetMargin) {
				return 0;
			}
			int rotationDir = -getRotationDirection(currentAngle);
			double speed = 2 * magnitude * (Math.abs(currentAngle - movmentAngle) / 100.0);
			FlashUtil.getLog().log("Offset>>> speed: "+speed+" dir: "+rotationDir);
			return speed * rotationDir;
		}
		public double stabilizeVector(double magnitude, double direction){
			if (gyro == null)
				return 0;
			if (magnitude != this.magnitude || direction != this.direction){
				feedNew(magnitude, direction);
				FlashUtil.getLog().log("Feed new");
			}
			double currentAngle = Mathf.limitAngle(gyro.getAngle());
			if (currentAngle > movmentAngle - offsetMargin && currentAngle < movmentAngle + offsetMargin) {
				return 0;
			}
			int rotationDir = -getRotationDirection(currentAngle);
			double offset = Mathf.limitAngle(currentAngle - movmentAngle);
			FlashUtil.getLog().log("Offset! >>Mag: "+offset+" Dir: "+rotationDir);
			return offset * rotationDir;
		}

		public void setOffsetMargin(double offset) {
			offsetMargin = offset;
		}
		public double getOffsetMargin() {
			return offsetMargin;
		}
		
		public void setGyro(Gyro gyro) {
			this.gyro = gyro;
		}
		public Gyro getGyro() {
			return gyro;
		}
		
		public void setBaseSpeed(double speed){
			baseSpeed = speed;
		}
		public double getBaseSpeed(){
			return baseSpeed;
		}
	}

	private FlashSpeedController front_left;
	private FlashSpeedController rear_left;
	private FlashSpeedController front_right;
	private FlashSpeedController rear_right;

	private MecanumStabilizer stabilizer = new MecanumStabilizer();
	private boolean stabilizing = false, scaleVoltage = false;
	private double sensitivityLimit = 0;
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
		super(null);
		front_right = right_front;
		rear_right = right_back;
		front_left = left_front;
		rear_left = left_back;
		enableBrakeMode(false);
	}

	private double scaleForVoltage(double s) {
		return scaleVoltage? FlashRoboUtil.scaleVoltageBus(s) : s;
	}
	private double roundAngle(double angle) {
		return Mathf.roundToMultiplier(angle, angleRound);
	}
	
	/**
	 * Gets the stabilizer used by this drive.
	 * @return the mecanum stabilizer
	 */
	public MecanumStabilizer getStabilzer() {
		return stabilizer;
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
		this.sensitivityLimit = s;
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
			magnitude = 0;
		if (Math.abs(rotation) < sensitivityLimit)
			rotation = 0;

		if (angleRound != 0) 
			direction = roundAngle(direction);
		
		magnitude = limit(magnitude) * Math.sqrt(2.0);
		
		double dirInRad = Math.toRadians(direction + 45.0);
		double cosD = Math.cos(dirInRad);
		double sinD = Math.sin(dirInRad);

		if (stabilizing && rotation == 0)
			rotation = stabilizer.stabilizeByRotation(magnitude, direction);
		
		double wheelSpeeds[] = { 
				(sinD * magnitude + rotation), // front left
				(cosD * magnitude - rotation), // front right
				(cosD * magnitude + rotation), // rear left
				(sinD * magnitude - rotation),// rear right
		};

		normalize(wheelSpeeds);

		front_left.set(wheelSpeeds[0]);
		front_right.set(-wheelSpeeds[1]);
		rear_left.set(wheelSpeeds[2]);
		rear_right.set(-wheelSpeeds[3]);
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

	private double limit(double num) {
		return Mathf.limit(scaleForVoltage(num), -1, 1);
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
	public System getSystem() {
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
		return scaleVoltage;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void enableVoltageScaling(boolean en) {
		scaleVoltage = en;
	}
}
