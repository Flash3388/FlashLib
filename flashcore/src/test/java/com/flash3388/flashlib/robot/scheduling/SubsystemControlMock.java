package com.flash3388.flashlib.robot.scheduling;

import com.flash3388.flashlib.robot.scheduling.actions.Action;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.when;

public class SubsystemControlMock {

    private final Map<Subsystem, Action> mDefaultActionsOnSubsystems;

    public SubsystemControlMock(RequirementsControl requirementsControl) {
        mDefaultActionsOnSubsystems = new HashMap<>();

        when(requirementsControl.getDefaultActionsToStart())
                .thenReturn(mDefaultActionsOnSubsystems);
    }

    public void setDefaultAction(Subsystem subsystem, Action action) {
        mDefaultActionsOnSubsystems.put(subsystem, action);
    }
}
