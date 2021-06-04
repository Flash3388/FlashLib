package com.flash3388.flashlib.robot.systems;

import com.flash3388.flashlib.control.Direction;
import com.flash3388.flashlib.io.devices.SpeedController;

public class ConstantSpeedMotorSystem extends MotorSystem {

    private final double mSpeed;

    public ConstantSpeedMotorSystem(SpeedController controller, double speed) {
        super(controller);
        mSpeed = speed;
    }

    public ConstantSpeedMotorSystem(double speed, SpeedController... controllers) {
        super(controllers);
        mSpeed = speed;
    }

    public void move(Direction direction) {
        move(mSpeed, direction);
    }

    public void forward() {
        forward(mSpeed);
    }

    public void backward() {
        backward(mSpeed);
    }

    public void rotate(Direction direction) {
        rotate(mSpeed, direction);
    }

    public void rotateRight() {
        rotate(Direction.FORWARD);
    }

    public void rotateLeft() {
        rotate(Direction.BACKWARD);
    }
}
