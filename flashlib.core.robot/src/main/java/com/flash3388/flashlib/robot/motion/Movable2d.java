package com.flash3388.flashlib.robot.motion;

import com.flash3388.flashlib.scheduling.Requirement;
import com.jmath.vectors.Vector2;

public interface Movable2d extends Movable, Requirement {

    void move(Vector2 motionVector);

    @Override
    default void move(double speed) {
        move(Vector2.polar(speed, 0.0));
    }
}
