package com.flash3388.flashlib.robot.nint;

import com.flash3388.flashlib.scheduling.Requirement;
import com.flash3388.flashlib.scheduling.actions.Action;

public interface Piston extends Requirement {

    Action open();
    Action close();
    Action toggle();

    boolean isOpen();
}
