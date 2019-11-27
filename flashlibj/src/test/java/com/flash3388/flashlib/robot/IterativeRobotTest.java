package com.flash3388.flashlib.robot;

import com.flash3388.flashlib.io.Closer;
import com.flash3388.flashlib.robot.hid.HidInterface;
import com.flash3388.flashlib.robot.modes.RobotMode;
import com.flash3388.flashlib.robot.modes.RobotModeSupplier;
import com.flash3388.flashlib.robot.scheduling.Scheduler;
import com.flash3388.flashlib.util.concurrent.ExecutorCloser;
import com.flash3388.flashlib.util.concurrent.Sleeper;
import com.flash3388.flashlib.time.Clock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.mockito.Mockito.*;

public class IterativeRobotTest {

    private ExecutorService mExecutorService;
    private Closer mCloser;

    private Logger mLogger;
    private Scheduler mScheduler;
    private IterativeRobot mIterativeRobot;

    @BeforeEach
    public void setUp() throws Exception {
        mCloser = Closer.empty();

        mExecutorService = Executors.newSingleThreadExecutor();
        mCloser.add(new ExecutorCloser(mExecutorService));

        mLogger = mock(Logger.class);
        mScheduler = mock(Scheduler.class);
        mIterativeRobot = spy(new FakeIterativeRobot(mScheduler, mLogger, mock(Sleeper.class)));
    }

    @AfterEach
    public void tearDown() throws Exception {
        mIterativeRobot.robotShutdown();
        mCloser.close();
    }

    @Test
    public void run_robotInDisabledMode_callsModeMethods() throws Exception {
        mockRobotInMode(RobotMode.DISABLED);
        CountDownLatch runLatch = setupIterationStopper(2);
        runRobotLoop();

        runLatch.await();

        InOrder inOrder = Mockito.inOrder(mIterativeRobot);
        inOrder.verify(mIterativeRobot, times(1)).disabledInit();
        inOrder.verify(mIterativeRobot, times(2)).disabledPeriodic();
    }

    @Test
    public void run_robotInOtherMode_callsModeMethods() throws Exception {
        RobotMode MODE = new RobotMode("mode", 1);

        mockRobotInMode(MODE);
        CountDownLatch runLatch = setupIterationStopper(2);
        runRobotLoop();

        runLatch.await();

        InOrder inOrder = Mockito.inOrder(mIterativeRobot);
        inOrder.verify(mIterativeRobot, times(1)).modeInit(eq(MODE));
        inOrder.verify(mIterativeRobot, times(2)).modePeriodic(eq(MODE));
    }

    @Test
    public void run_robotSwitchesMode_callsOriginalAndOtherModeMethods() throws Exception {
        RobotMode STARTING_MODE = new RobotMode("mode2", 2);
        RobotMode OTHER_MODE = new RobotMode("mode1", 1);

        mockRobotSwitchMode(STARTING_MODE, OTHER_MODE);
        CountDownLatch runLatch = setupIterationStopper(2);
        runRobotLoop();

        runLatch.await();

        InOrder inOrder = Mockito.inOrder(mIterativeRobot);
        inOrder.verify(mIterativeRobot, times(1)).modeInit(eq(STARTING_MODE));
        inOrder.verify(mIterativeRobot, times(1)).modePeriodic(eq(STARTING_MODE));
        inOrder.verify(mIterativeRobot, times(1)).modeInit(eq(OTHER_MODE));
        inOrder.verify(mIterativeRobot, times(1)).modePeriodic(eq(OTHER_MODE));
    }

    @Test
    public void run_robotNotDisabled_schedulerIsRan() throws Exception {
        RobotMode MODE = new RobotMode("mode", 2);

        mockRobotInMode(MODE);
        CountDownLatch runLatch = setupIterationStopper(1);
        runRobotLoop();

        runLatch.await();

        verify(mScheduler, times(1)).run(any(RobotMode.class));
    }

    private void mockRobotInMode(RobotMode mode) throws Exception {
        when(mIterativeRobot.getMode()).thenReturn(mode);
    }

    private void mockRobotSwitchMode(RobotMode originalMode, RobotMode newMode) throws Exception {
        when(mIterativeRobot.getMode())
                .thenReturn(originalMode)
                .thenReturn(newMode);
    }

    private CountDownLatch setupIterationStopper(int runCount) {
        CountDownLatch runsLatch = new CountDownLatch(runCount);

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                runsLatch.countDown();
                if (runsLatch.getCount() == 0) {
                    mIterativeRobot.stopRobotLoop();
                }
                return null;
            }
        }).when(mIterativeRobot).robotPeriodic();

        return runsLatch;
    }

    private void runRobotLoop() throws Exception {
        mExecutorService.execute(()-> {
            mIterativeRobot.robotMain();
        });
    }

    private static class FakeIterativeRobot extends IterativeRobot {

        private final Scheduler mScheduler;
        private final Logger mLogger;

        private FakeIterativeRobot(Scheduler scheduler, Logger logger, Sleeper sleeper) {
            super(sleeper);

            mScheduler = scheduler;
            mLogger = logger;
        }

        @Override
        public RobotModeSupplier getModeSupplier() {
            return null;
        }

        @Override
        public HidInterface getHidInterface() {
            return null;
        }

        @Override
        public Clock getClock() {
            return null;
        }

        @Override
        public Scheduler getScheduler() {
            return mScheduler;
        }

        @Override
        public Logger getLogger() {
            return mLogger;
        }

        @Override
        protected void robotInit() throws RobotInitializationException {

        }

        @Override
        protected void robotPeriodic() {

        }

        @Override
        protected void disabledInit() {

        }

        @Override
        protected void disabledPeriodic() {

        }

        @Override
        protected void modeInit(RobotMode mode) {

        }

        @Override
        protected void modePeriodic(RobotMode mode) {

        }
    }
}