package com.flash3388.flashlib.scheduling.impl.statemachines;

import com.flash3388.flashlib.scheduling.SchedulerMode;
import com.flash3388.flashlib.scheduling.statemachines.StateMachine;

public interface InternalStateMachine extends StateMachine {

    void update(SchedulerMode mode);
}
