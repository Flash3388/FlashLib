package com.flash3388.flashlib.scheduling.actions;

import com.flash3388.flashlib.robot.ClockMock;
import com.flash3388.flashlib.robot.RunningRobotMock;
import com.flash3388.flashlib.scheduling.Scheduler;
import com.flash3388.flashlib.time.Clock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class SequentialActionGroupTest {

    private Clock mClock;

    @BeforeEach
    public void setup() {
        mClock = ClockMock.mockInvalidTimeClock();

        RunningRobotMock.mockRobotWithDependencies();
        RunningRobotMock.mockRobotWithClock(mClock);
    }

    @Test
    public void execute_noActionCurrentlyRunning_startsNextAction() throws Exception {
        List<Action> actions = Collections.singletonList(
                ActionsMock.actionMocker().build()
        );

        ActionContext context = mock(ActionContext.class);
        Deque<ActionContext> contexts = new ArrayDeque<>(Collections.singleton(
                context
        ));
        SequentialActionGroup actionGroup = new SequentialActionGroup(
                mock(Scheduler.class), mClock, actions, contexts);

        actionGroup.execute();

        verify(context, times(1)).prepareForRun();
    }

    @Test
    public void execute_actionCurrentlyRunning_executesIt() throws Exception {
        List<Action> actions = Arrays.asList(
                ActionsMock.actionMocker().build(),
                ActionsMock.actionMocker().build()
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
                ActionsMock.actionMocker()
                        .mockIsFinished(true)
                        .build(),
                ActionsMock.actionMocker().build()
        );

        SequentialActionGroup actionGroup = new SequentialActionGroup()
                .add(actions);

        actionGroup.initialize();
        actionGroup.execute();

        verify(actions.get(0), times(1)).end(eq(false));
    }

    @Test
    public void interrupted_actionIsRunning_cancelsIt() throws Exception {
        List<Action> actions = new ArrayList<>();

        ActionContext context = ActionsMock.contextMocker()
                .runFinished(false)
                .build();
        Deque<ActionContext> contexts = new ArrayDeque<>(Collections.singletonList(
                context
        ));

        SequentialActionGroup actionGroup = new SequentialActionGroup(
                mock(Scheduler.class), mClock, actions, contexts);

        actionGroup.execute();
        actionGroup.end(true);

        verify(context, times(1)).runCanceled();
    }
}