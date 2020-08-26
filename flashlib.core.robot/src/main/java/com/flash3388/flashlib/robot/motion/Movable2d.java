package com.flash3388.flashlib.robot.motion;

import com.flash3388.flashlib.control.Stoppable;
import com.flash3388.flashlib.scheduling.Requirement;
import com.jmath.vectors.Vector2;

public interface Movable2d extends Stoppable, Requirement {

    void move(Vector2 motionVector);
}
