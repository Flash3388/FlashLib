package com.flash3388.flashlib.robot.scheduling.actions;

import com.flash3388.flashlib.robot.ClockMock;
import com.flash3388.flashlib.robot.RunningRobotMock;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.flash3388.flashlib.robot.scheduling.actions.ActionsMock.mockActionIsFinishedMarkedTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class SequentialActionGroupTest {

    @Before
    public void setup() {
        RunningRobotMock.mockRobotWithDependencies();
        RunningRobotMock.mockRobotWithClock(ClockMock.mockInvalidTimeClock());
    }

    @Test
    public void initialize_withMultipleActions_startsFirstAction() throws Exception {
        List<Action> actions = Arrays.asList(
                mock(Action.class),
                mock(Action.class)
        );

        SequentialActionGroup actionGroup = new SequentialActionGroup()
                .add(actions);

        actionGroup.initialize();

        verify(actions.get(0), times(1)).markStarted();
        actions.stream().skip(1).forEach((action) -> {
            verify(action, never()).markStarted();
        });
    }

    @Test
    public void execute_noActionCurrentlyRunning_startsNextAction() throws Exception {
        List<Action> actions = Arrays.asList(
                mockActionIsFinishedMarkedTrue(),
                mock(Action.class)
        );

        SequentialActionGroup actionGroup = new SequentialActionGroup()
                .add(actions);

        actionGroup.initialize();
        actionGroup.execute();

        verify(actions.get(0), times(1)).markStarted();
    }

    @Test
    public void execute_actionCurrentlyRunning_executesIt() throws Exception {
        List<Action> actions = Arrays.asList(
                mock(Action.class),
                mock(Action.class)
        );

        SequentialActionGroup actionGroup = new SequentialActionGroup()
                .add(actions);

        actionGroup.initialize();
        actionGroup.execute();

        verify(actions.get(0), times(1)).execute();
    }

    @Test
    public void execute_actionCurrentlyRunningIsFinished_endsIt() throws Exception {
        List<Action> actions = Arrays.asList(
                mockActionIsFinishedMarkedTrue(),
                mock(Action.class)
        );

        SequentialActionGroup actionGroup = new SequentialActionGroup()
                .add(actions);

        actionGroup.initialize();
        actionGroup.execute();

        verify(actions.get(0), times(1)).end();
    }

    @Test
    public void interrupted_actionIsRunning_cancelsIt() throws Exception {
        List<Action> actions = Collections.singletonList(
                mock(Action.class)
        );

        SequentialActionGroup actionGroup = new SequentialActionGroup()
                .add(actions);

        actionGroup.initialize();
        actionGroup.interrupted();

        verify(actions.get(0), times(1)).markCanceled();
    }
}