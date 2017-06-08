package edu.flash3388.flashlib.robot.systems;

import edu.flash3388.flashlib.math.Mathd;
import edu.flash3388.flashlib.robot.Direction;
import edu.flash3388.flashlib.robot.FlashRoboUtil;
import edu.flash3388.flashlib.robot.System;
import edu.flash3388.flashlib.robot.VoltageScalable;
import edu.flash3388.flashlib.robot.devices.FlashSpeedController;
import edu.flash3388.flashlib.robot.devices.Gyro;
import edu.flash3388.flashlib.robot.hid.Stick;
import edu.flash3388.flashlib.util.FlashUtil;

public class MecanumDrive extends System implements HolonomicDriveSystem, VoltageScalable {

	/*
	 * TODO: FINISH STABILIZER
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
			movmentAngle = Mathd.limitAngle(gyro.getAngle());
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
			double currentAngle = Mathd.limitAngle(gyro.getAngle());
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
			double currentAngle = Mathd.limitAngle(gyro.getAngle());
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
			double currentAngle = Mathd.limitAngle(gyro.getAngle());
			if (currentAngle > movmentAngle - offsetMargin && currentAngle < movmentAngle + offsetMargin) {
				return 0;
			}
			int rotationDir = -getRotationDirection(currentAngle);
			double offset = Mathd.limitAngle(currentAngle - movmentAngle);
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
	public MecanumStabilizer getStabilzer() {
		return stabilizer;
	}
	public void enableStabilizing(boolean en) {
		this.stabilizing = en;
	}
	public boolean isStabilizing() {
		return stabilizing;
	}
	public void setSensitivityLimit(double s) {
		this.sensitivityLimit = s;
	}
	public double getSensitivityLimit() {
		return sensitivityLimit;
	}
	public int getAngleRounding() {
		return angleRound;
	}
	public void setAngleRounding(int round) {
		angleRound = round;
	}
	@Override
	public boolean isVoltageScaling() {
		return scaleVoltage;
	}
	@Override
	public void enableVoltageScaling(boolean en) {
		scaleVoltage = en;
	}

	public void mecanumDrive_cartesian(double x, double y, double rotation) {
		mecanumDrive_polar(Math.sqrt(x * x + y * y), Math.toDegrees(Math.atan2(x, y)), rotation);
	}
	public void mecanumDrive_polar(double magnitude, double direction, double rotation) {
		if (magnitude < sensitivityLimit)
			magnitude = 0;
		if (Math.abs(rotation) < sensitivityLimit)
			rotation = 0;

		if (angleRound != 0) {
			direction = roundAngle(direction);
		}

		
		magnitude = limit(magnitude) * Math.sqrt(2.0);
		
		/*if (stabilizing && rotation == 0){
			direction += stabilizer.stabilizeVector(magnitude, direction);
		}*/
		
		double dirInRad = Math.toRadians(direction + 45.0);
		double cosD = Math.cos(dirInRad);
		double sinD = Math.sin(dirInRad);

		if (stabilizing && rotation == 0)
			rotation = stabilizer.stabilizeByRotation(magnitude, direction);
		
		double wheelSpeeds[] = { (sinD * magnitude + rotation), // front left
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
	public void mecanumDrive_polar(Stick stick) {
		mecanumDrive_polar(stick.getMagnitude(), stick.getAngle(), 0);
	}
	public void mecanumDrive_polar(Stick stick, Stick stickR) {
		mecanumDrive_polar(stick.getMagnitude(), stick.getAngle(), stickR.getX());
	}
	public void mecanumDrive_polar(double magnitude, double direction, double rotation, double gyroAngle) {
		mecanumDrive_polar(magnitude, direction + gyroAngle, rotation);
	}
	public void mecanumDrive_polar(double magnitude, double direction, double rotation, Gyro gyro) {
		mecanumDrive_polar(magnitude, direction, rotation, gyro.getAngle());
	}
	public void mecanumDrive_polar(Stick stick, Gyro gyro) {
		mecanumDrive_polar(stick.getMagnitude(), stick.getAngle(), 0, gyro);
	}
	public void mecanumDrive_polar(Stick stick, Stick stickR, Gyro gyro) {
		mecanumDrive_polar(stick.getMagnitude(), stick.getAngle(), stickR.getX(), gyro);
	}
	public void mecanumDrive_polar(Stick stick, Stick stickR, double gyroAngle) {
		mecanumDrive_polar(stick.getMagnitude(), stick.getAngle(), stickR.getX(), gyroAngle);
	}

	private double limit(double num) {
		if (num > 1.0)
			return 1.0;
		if (num < -1.0)
			return -1.0;
		return num;
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

	private double roundAngle(double angle) {
		return Mathd.roundToMultiplier(angle, angleRound);
	}
	@Override
	public void rotate(double speed, boolean direction) {
		mecanumDrive_polar(0, 0, direction? speed : -speed);
	}
	@Override
	public void rotateRight(double speed) {
		mecanumDrive_polar(0, 0, speed);
	}
	@Override
	public void rotateLeft(double speed) {
		mecanumDrive_polar(0, 0, -speed);
	}
	@Override
	public void driveY(double speed, boolean direction) {
		if (direction)
			forward(speed);
		else if (direction)
			backward(speed);
	}
	@Override
	public void forward(double speed) {
		mecanumDrive_polar(speed, 0, 0);
	}
	@Override
	public void backward(double speed) {
		mecanumDrive_polar(speed, 180, 0);
	}
	@Override
	public void driveX(double speed, boolean direction) {
		if (direction)
			right(speed);
		else if (direction)
			left(speed);
	}
	@Override
	public void right(double speed) {
		mecanumDrive_polar(speed, 90, 0);
	}
	@Override
	public void left(double speed) {
		mecanumDrive_polar(speed, 270, 0);
	}
	@Override
	public void holonomicCartesian(double x, double y, double rotation) {
		mecanumDrive_cartesian(x, y, rotation);
	}
	@Override
	public void holonomicPolar(double magnitude, double direction, double rotation) {
		mecanumDrive_polar(magnitude, direction, rotation);
	}
	@Override
	public void stop() {
		front_left.set(0);
		front_right.set(0);
		rear_right.set(0);
		rear_left.set(0);
	}
	@Override
	public System getSystem() {
		return this;
	}

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
	@Override
	public boolean inBrakeMode() {
		return (front_left instanceof ModableMotor && ((ModableMotor)front_left).inBrakeMode()) && 
			   (front_right instanceof ModableMotor && ((ModableMotor)front_right).inBrakeMode()) &&
			   (rear_right instanceof ModableMotor && ((ModableMotor)rear_right).inBrakeMode()) && 
			   (rear_left instanceof ModableMotor && ((ModableMotor)rear_left).inBrakeMode());
	}
}
