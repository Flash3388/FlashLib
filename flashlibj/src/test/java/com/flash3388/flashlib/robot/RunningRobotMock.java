package com.flash3388.flashlib.robot;

import com.flash3388.flashlib.robot.hid.HidInterface;
import com.flash3388.flashlib.robot.modes.RobotModeSupplier;
import com.flash3388.flashlib.robot.scheduling.Scheduler;
import com.flash3388.flashlib.time.Clock;
import org.slf4j.Logger;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public final class RunningRobotMock {

    private RunningRobotMock() {
    }

    public static void mockRobotWithDependencies() {
        Robot robot = mock(Robot.class);
        when(robot.getClock()).thenReturn(mock(Clock.class));
        when(robot.getHidInterface()).thenReturn(mock(HidInterface.class));
        when(robot.getLogger()).thenReturn(mock(Logger.class));
        when(robot.getScheduler()).thenReturn(mock(Scheduler.class));
        when(robot.getModeSupplier()).thenReturn(mock(RobotModeSupplier.class));

        RunningRobot.setInstance(robot);
    }

    public static void mockRobotWithClock(Clock clock) {
        Robot robot = RunningRobot.getInstance();
        when(robot.getClock()).thenReturn(clock);
    }
}
