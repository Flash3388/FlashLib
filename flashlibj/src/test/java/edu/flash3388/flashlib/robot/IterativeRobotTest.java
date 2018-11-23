package edu.flash3388.flashlib.robot;

import edu.flash3388.flashlib.io.Closer;
import edu.flash3388.flashlib.robot.hid.HidInterface;
import edu.flash3388.flashlib.robot.modes.RobotMode;
import edu.flash3388.flashlib.robot.modes.RobotModeSupplier;
import edu.flash3388.flashlib.robot.scheduling.Scheduler;
import edu.flash3388.flashlib.util.concurrent.ExecutorTerminator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.mockito.Mockito.*;

public class IterativeRobotTest {

    private ExecutorService mExecutorService;
    private Closer mCloser;

    private Scheduler mScheduler;
    private IterativeRobot mIterativeRobot;

    @Before
    public void setUp() throws Exception {
        mCloser = Closer.empty();

        mExecutorService = Executors.newSingleThreadExecutor();
        mCloser.add(new ExecutorTerminator(mExecutorService));

        mScheduler = mock(Scheduler.class);
        mIterativeRobot = spy(new FakeIterativeRobot(mScheduler));
    }

    @After
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
        RobotMode MODE = new RobotMode(1);

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
        RobotMode STARTING_MODE = new RobotMode(2);
        RobotMode OTHER_MODE = new RobotMode(1);

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
    public void run_robotEntersMode_removesActionsFromScheduler() throws Exception {
        RobotMode STARTING_MODE = new RobotMode(2);
        RobotMode OTHER_MODE = new RobotMode(1);

        mockRobotSwitchMode(STARTING_MODE, OTHER_MODE);
        CountDownLatch runLatch = setupIterationStopper(2);
        runRobotLoop();

        runLatch.await();

        verify(mScheduler, times(2)).removeAllActions();
    }

    @Test
    public void run_robotNotDisabled_schedulerIsRan() throws Exception {
        RobotMode MODE = new RobotMode(2);

        mockRobotInMode(MODE);
        CountDownLatch runLatch = setupIterationStopper(1);
        runRobotLoop();

        runLatch.await();

        verify(mScheduler, times(1)).run();
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

        private FakeIterativeRobot(Scheduler scheduler) {
            super(scheduler);
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

        @Override
        public RobotModeSupplier getModeSupplier() {
            return null;
        }

        @Override
        public HidInterface getHidInterface() {
            return null;
        }
    }
}