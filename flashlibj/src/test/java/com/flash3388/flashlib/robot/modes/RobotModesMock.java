package com.flash3388.flashlib.robot.modes;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public final class RobotModesMock {

    private RobotModesMock() {
    }

    public static RobotMode mockNonDisabledMode() {
        RobotMode robotMode = mock(RobotMode.class);
        when(robotMode.getName()).thenReturn("not disabled");
        when(robotMode.getKey()).thenReturn(1);

        return robotMode;
    }
}
