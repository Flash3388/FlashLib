package com.flash3388.flashlib.hid.generic;

import com.flash3388.flashlib.hid.Button;
import com.flash3388.flashlib.scheduling.triggers.SchedulerTrigger;

public abstract class GenericButtonBase extends SchedulerTrigger implements Button {

    public GenericButtonBase() {
        scheduleAutoUpdate(this);
    }
}
