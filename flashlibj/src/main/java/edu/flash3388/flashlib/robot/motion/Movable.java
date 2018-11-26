package edu.flash3388.flashlib.robot.motion;

public interface Movable extends Stoppable {

    void move(double speed);

    default void move(double speed, Direction direction) {
        move(Math.abs(speed) * direction.sign());
    }

    default void forward(double speed) {
        move(speed, Direction.FORWARD);
    }

    default void backward(double speed) {
        move(speed, Direction.BACKWARD);
    }
}
