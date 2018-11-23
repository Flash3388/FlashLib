package edu.flash3388.flashlib.robot;

import edu.flash3388.flashlib.io.Closer;
import edu.flash3388.flashlib.robot.hid.HidInterface;
import edu.flash3388.flashlib.robot.modes.RobotMode;
import edu.flash3388.flashlib.robot.modes.RobotModeSupplier;
import edu.flash3388.flashlib.util.concurrent.ExecutorTerminator;
import edu.flash3388.flashlib.util.concurrent.Sleeper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.mockito.Mockito.*;

public class SimpleRobotTest {

    private ExecutorService mExecutorService;
    private Closer mCloser;

    private SimpleRobot mSimpleRobot;

    @Before
    public void setUp() throws Exception {
        mCloser = Closer.empty();

        mExecutorService = Executors.newSingleThreadExecutor();
        mCloser.add(new ExecutorTerminator(mExecutorService));

        mSimpleRobot = spy(new FakeSimpleRobot(mock(Sleeper.class)));
    }

    @After
    public void tearDown() throws Exception {
        mSimpleRobot.robotShutdown();
        mCloser.close();
    }

    @Test
    public void run_robotInDisabled_callsModeMethods() throws Exception {
        mockRobotInMode(RobotMode.DISABLED);
        CountDownLatch methodLatch = createLatchOnDisabled();

        runRobotLoop();
        methodLatch.await();

        verify(mSimpleRobot, times(1)).disabled();
    }

    @Test
    public void run_robotInMode_callsModeMethods() throws Exception {
        RobotMode MODE = new RobotMode(5);

        mockRobotInMode(MODE);
        CountDownLatch methodLatch = createLatchOnMode(MODE);

        runRobotLoop();
        methodLatch.await();

        verify(mSimpleRobot, times(1)).onMode(eq(MODE));
    }

    @Test
    public void run_robotSwitchesMode_callsOtherModeMethods() throws Exception {
        RobotMode ORIGINAL_MODE = new RobotMode(5);
        RobotMode NEW_MODE = new RobotMode(10);

        mockRobotSwitchMode(ORIGINAL_MODE, NEW_MODE);
        CountDownLatch methodLatch = createLatchOnMode(NEW_MODE);

        runRobotLoop();
        methodLatch.await();

        InOrder inOrder = inOrder(mSimpleRobot);
        inOrder.verify(mSimpleRobot).onMode(eq(ORIGINAL_MODE));
        inOrder.verify(mSimpleRobot).onMode(eq(NEW_MODE));
    }

    private void mockRobotInMode(RobotMode mode) throws Exception {
        when(mSimpleRobot.getMode()).thenReturn(mode);
    }

    private void mockRobotSwitchMode(RobotMode originalMode, RobotMode newMode) throws Exception {
        when(mSimpleRobot.getMode())
                .thenReturn(originalMode)
                .thenReturn(newMode);
    }

    private CountDownLatch createLatchOnDisabled() {
        final CountDownLatch methodLatch = new CountDownLatch(1);

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                methodLatch.countDown();
                return null;
            }
        }).when(mSimpleRobot).disabled();

        return methodLatch;
    }

    private CountDownLatch createLatchOnMode(RobotMode robotMode) {
        final CountDownLatch methodLatch = new CountDownLatch(1);

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                methodLatch.countDown();
                return null;
            }
        }).when(mSimpleRobot).onMode(eq(robotMode));

        return methodLatch;
    }

    private void runRobotLoop() throws Exception {
        mExecutorService.execute(()-> {
            mSimpleRobot.robotMain();
        });
    }

    private static class FakeSimpleRobot extends SimpleRobot {

        private FakeSimpleRobot(Sleeper sleeper) {
            super(sleeper);
        }

        @Override
        protected void robotInit() throws RobotInitializationException {

        }

        @Override
        protected void disabled() {

        }

        @Override
        protected void onMode(RobotMode mode) {

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