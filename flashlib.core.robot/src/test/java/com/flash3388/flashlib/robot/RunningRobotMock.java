package com.flash3388.flashlib.robot;

import com.flash3388.flashlib.hid.HidInterface;
import com.flash3388.flashlib.scheduling.Scheduler;
import com.flash3388.flashlib.time.Clock;
import org.slf4j.Logger;

import java.util.function.Supplier;

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
        when(robotControl.getModeSupplier()).thenReturn(mock(Supplier.class));

        RunningRobot.setInstance(robotControl);
    }

    public static void mockRobotWithClock(Clock clock) {
        RobotControl robotControl = RunningRobot.getInstance();
        when(robotControl.getClock()).thenReturn(clock);
    }
}
