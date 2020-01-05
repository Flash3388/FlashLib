package com.flash3388.flashlib.robot.scheduling.actions;

import com.flash3388.flashlib.robot.ClockMock;
import com.flash3388.flashlib.robot.RunningRobotMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.flash3388.flashlib.robot.scheduling.actions.ActionsMock.mockActionIsFinishedMarkedTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class ParallelActionGroupTest {

    @BeforeEach
    public void setup() {
        RunningRobotMock.mockRobotWithDependencies();
        RunningRobotMock.mockRobotWithClock(ClockMock.mockInvalidTimeClock());
    }

    @Test
    public void initialize_withMultipleActions_startsAll() throws Exception {
        List<Action> actions = Arrays.asList(
                mock(Action.class),
                mock(Action.class)
        );

        ParallelActionGroup actionGroup = new ParallelActionGroup()
                .add(actions);

        actionGroup.initialize();

        actions.forEach((action) -> verify(action, times(1)).markStarted());
    }

    @Test
    public void execute_actionsCurrentlyRunning_executesThem() throws Exception {
        List<Action> actions = Arrays.asList(
                mock(Action.class),
                mock(Action.class)
        );

        ParallelActionGroup actionGroup = new ParallelActionGroup()
                .add(actions);

        actionGroup.initialize();
        actionGroup.execute();

        actions.forEach((action) -> verify(action, times(1)).execute());
    }

    @Test
    public void execute_actionCurrentlyRunningIsFinished_endsIt() throws Exception {
        List<Action> actions = Arrays.asList(
                mockActionIsFinishedMarkedTrue(),
                mock(Action.class)
        );

        ParallelActionGroup actionGroup = new ParallelActionGroup()
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

        ParallelActionGroup actionGroup = new ParallelActionGroup()
                .add(actions);

        actionGroup.initialize();
        actionGroup.interrupted();

        verify(actions.get(0), times(1)).markCanceled();
    }
}