package com.flash3388.flashlib.robot.scheduling;

import com.flash3388.flashlib.robot.scheduling.actions.Action;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.when;

public class SubsystemControlMock {

    private final Map<Subsystem, Action> mDefaultActionsOnSubsystems;

    public SubsystemControlMock(SubsystemControl subsystemControl) {
        mDefaultActionsOnSubsystems = new HashMap<>();

        when(subsystemControl.getDefaultActionsToStart()).thenReturn(mDefaultActionsOnSubsystems);
    }

    public void setDefaultAction(Subsystem subsystem, Action action) {
        mDefaultActionsOnSubsystems.put(subsystem, action);
    }
}
