package com.flash3388.flashlib.robot.hid.generic;

import com.flash3388.flashlib.robot.hid.Button;
import com.flash3388.flashlib.robot.scheduling.triggers.SchedulerTrigger;

public abstract class GenericButtonBase extends SchedulerTrigger implements Button {

    public GenericButtonBase() {
        schedule(this);
    }
}
