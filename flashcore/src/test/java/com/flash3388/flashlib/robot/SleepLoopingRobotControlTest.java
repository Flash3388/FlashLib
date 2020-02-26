package com.flash3388.flashlib.robot;

import com.flash3388.flashlib.io.Closer;
import com.flash3388.flashlib.robot.hid.HidInterface;
import com.flash3388.flashlib.robot.io.IoInterface;
import com.flash3388.flashlib.robot.modes.RobotMode;
import com.flash3388.flashlib.robot.scheduling.Scheduler;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.time.Time;
import com.flash3388.flashlib.util.concurrent.ExecutorCloser;
import com.flash3388.flashlib.util.concurrent.Sleeper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.slf4j.Logger;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SleepLoopingRobotControlTest {

    private ExecutorService mExecutorService;
    private Closer mCloser;

    private Scheduler mScheduler;
    private SleepLoopingRobotControl mSleepLoopingRobot;
    private IterativeRobotControl mRobot;

    @BeforeEach
    public void setUp() throws Exception {
        mCloser = Closer.empty();

        mExecutorService = Executors.newSingleThreadExecutor();
        mCloser.add(new ExecutorCloser(mExecutorService));

        Logger logger = mock(Logger.class);
        mScheduler = mock(Scheduler.class);

        Clock clock = mock(Clock.class);
        when(clock.currentTime()).thenReturn(Time.milliseconds(1));

        mRobot = mock(IterativeRobotControl.class);
        mSleepLoopingRobot = spy(new FakeSleepLoopingRobotControl(mRobot, mScheduler, logger, mock(Sleeper.class), clock));
    }

    @AfterEach
    public void tearDown() throws Exception {
        mSleepLoopingRobot.robotShutdown();
        mCloser.close();
    }

    @Test
    public void run_robotInDisabledMode_callsModeMethods() throws Exception {
        mockRobotInMode(RobotMode.DISABLED);
        CountDownLatch runLatch = setupIterationStopper(2);
        runRobotLoop();

        runLatch.await();

        InOrder inOrder = Mockito.inOrder(mRobot);
        inOrder.verify(mRobot, times(1)).disabledInit();
        inOrder.verify(mRobot, times(2)).disabledPeriodic();
    }

    @Test
    public void run_robotInOtherMode_callsModeMethods() throws Exception {
        RobotMode MODE = RobotMode.create("mode", 1);

        mockRobotInMode(MODE);
        CountDownLatch runLatch = setupIterationStopper(2);
        runRobotLoop();

        runLatch.await();

        InOrder inOrder = Mockito.inOrder(mRobot);
        inOrder.verify(mRobot, times(1)).modeInit(eq(MODE));
        inOrder.verify(mRobot, times(2)).modePeriodic(eq(MODE));
    }

    @Test
    public void run_robotSwitchesMode_callsOriginalAndOtherModeMethods() throws Exception {
        RobotMode STARTING_MODE = RobotMode.create("mode2", 2);
        RobotMode OTHER_MODE = RobotMode.create("mode1", 1);

        mockRobotSwitchMode(STARTING_MODE, OTHER_MODE);
        CountDownLatch runLatch = setupIterationStopper(2);
        runRobotLoop();

        runLatch.await();

        InOrder inOrder = Mockito.inOrder(mRobot);
        inOrder.verify(mRobot, times(1)).modeInit(eq(STARTING_MODE));
        inOrder.verify(mRobot, times(1)).modePeriodic(eq(STARTING_MODE));
        inOrder.verify(mRobot, times(1)).modeInit(eq(OTHER_MODE));
        inOrder.verify(mRobot, times(1)).modePeriodic(eq(OTHER_MODE));
    }

    @Test
    public void run_robotNotDisabled_schedulerIsRan() throws Exception {
        RobotMode MODE = RobotMode.create("mode", 2);

        mockRobotInMode(MODE);
        CountDownLatch runLatch = setupIterationStopper(1);
        runRobotLoop();

        runLatch.await();

        verify(mScheduler, times(1)).run(any(RobotMode.class));
    }

    private void mockRobotInMode(RobotMode mode) throws Exception {
        when(mSleepLoopingRobot.getMode()).thenReturn(mode);
    }

    private void mockRobotSwitchMode(RobotMode originalMode, RobotMode newMode) throws Exception {
        when(mSleepLoopingRobot.getMode())
                .thenReturn(originalMode)
                .thenReturn(newMode);
    }

    private CountDownLatch setupIterationStopper(int runCount) {
        CountDownLatch runsLatch = new CountDownLatch(runCount);

        doAnswer(invocation -> {
            runsLatch.countDown();
            if (runsLatch.getCount() == 0) {
                mSleepLoopingRobot.stopRobotLoop();
            }
            return null;
        }).when(mRobot).robotPeriodic();

        return runsLatch;
    }

    private void runRobotLoop() throws Exception {
        mExecutorService.execute(()-> {
            try {
                mSleepLoopingRobot.robotInit();
                mSleepLoopingRobot.robotMain();
            } catch (RobotInitializationException e) {
                throw new Error(e);
            }
        });
    }

    private static class FakeSleepLoopingRobotControl extends SleepLoopingRobotControl {

        private final Scheduler mScheduler;
        private final Logger mLogger;
        private final Clock mClock;

        private FakeSleepLoopingRobotControl(IterativeRobotControl robot, Scheduler scheduler, Logger logger, Sleeper sleeper, Clock clock) {
            super((r)-> robot, sleeper);

            mScheduler = scheduler;
            mLogger = logger;
            mClock = clock;
        }

        @Override
        public Supplier<? extends RobotMode> getModeSupplier() {
            return null;
        }

        @Override
        public IoInterface getIoInterface() {
            return null;
        }

        @Override
        public HidInterface getHidInterface() {
            return null;
        }

        @Override
        public Clock getClock() {
            return mClock;
        }

        @Override
        public Scheduler getScheduler() {
            return mScheduler;
        }

        @Override
        public Logger getLogger() {
            return mLogger;
        }
    }
}