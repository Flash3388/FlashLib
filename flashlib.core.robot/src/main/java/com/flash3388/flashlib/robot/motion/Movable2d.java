package com.flash3388.flashlib.robot.motion;

import com.flash3388.flashlib.control.Stoppable;
import com.jmath.vectors.Vector2;

public interface Movable2d extends Stoppable {

    void move(Vector2 motionVector);
}
