package com.flash3388.flashlib.robot;

import com.flash3388.flashlib.hid.HidInterface;
import com.flash3388.flashlib.robot.modes.RobotModeSupplier;
import com.flash3388.flashlib.scheduling.Scheduler;
import com.flash3388.flashlib.time.Clock;
import org.slf4j.Logger;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public final class RunningRobotMock {

    private RunningRobotMock() {
    }

    @SuppressWarnings("unchecked")
    public static void mockRobotWithDependencies() {
        RobotControl robotControl = mock(RobotControl.class);
        when(robotControl.getClock()).thenReturn(mock(Clock.class));
        when(robotControl.getHidInterface()).thenReturn(mock(HidInterface.class));
        when(robotControl.getLogger()).thenReturn(mock(Logger.class));
        when(robotControl.getScheduler()).thenReturn(mock(Scheduler.class));
        when(robotControl.getModeSupplier()).thenReturn(mock(RobotModeSupplier.class));

        RunningRobot.setControlInstance(robotControl);
    }

    public static void mockRobotWithClock(Clock clock) {
        RobotControl robotControl = RunningRobot.getControl();
        when(robotControl.getClock()).thenReturn(clock);
    }
}
