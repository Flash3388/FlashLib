package com.flash3388.flashlib.robot.motion.supervised;

import com.flash3388.flashlib.control.Stoppable;

public interface Supervisable extends Stoppable {
    boolean isInBounds(double input);
}
