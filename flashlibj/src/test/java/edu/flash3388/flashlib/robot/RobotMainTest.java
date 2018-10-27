package edu.flash3388.flashlib.robot;

import org.junit.Test;

import java.util.logging.Logger;

import static org.mockito.Mockito.*;

public class RobotMainTest {

    @Test
    public void start_robotInitializationError_robotStartNotCalled() throws Exception {
        RobotBase robotBase = spy(FakeRobotBase.class);
        doThrow(new RobotInitializationException()).when(robotBase).robotInit();

        RobotMain.start(robotBase, mock(Logger.class));

        verify(robotBase, times(0)).robotMain();
    }

    @Test
    public void start_robotStartError_robotStopCalled() throws Exception {
        RobotBase robotBase = spy(FakeRobotBase.class);
        doThrow(new RuntimeException()).when(robotBase).robotMain();

        RobotMain.start(robotBase, mock(Logger.class));

        verify(robotBase, times(1)).robotShutdown();
    }
}