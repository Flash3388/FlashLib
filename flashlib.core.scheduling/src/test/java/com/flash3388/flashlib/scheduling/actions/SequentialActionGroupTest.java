package com.flash3388.flashlib.scheduling.actions;

import com.flash3388.flashlib.scheduling.Scheduler;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.time.ClockMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class SequentialActionGroupTest {

    private Scheduler mScheduler;
    private Clock mClock;
    private List<Action> mActions;
    private Queue<SynchronousActionContext> mActionQueue;

    @BeforeEach
    public void setup() {
        mScheduler = mock(Scheduler.class);
        mClock = ClockMock.mockInvalidTimeClock();
        mActions = new ArrayList<>();
        mActionQueue = new ArrayDeque<>();
    }

    @Test
    public void execute_noActionCurrentlyRunning_startsNextAction() throws Exception {
        Action action = ActionsMock.actionMocker()
                .build();
        SynchronousActionContext context = ActionsMock.synchronousActionContextMocker(action)
                .build();

        mActions.add(action);
        mActionQueue.add(context);

        SequentialActionGroup actionGroup = new SequentialActionGroup(mScheduler, mClock, mActions, mActionQueue);

        actionGroup.execute();

        verify(context, times(1)).startRun();
    }

    @Test
    public void execute_actionCurrentlyRunning_executesIt() throws Exception {
        List<Action> actions = Arrays.asList(
                ActionsMock.actionMocker().build(),
                ActionsMock.actionMocker().build()
        );

        mActions.addAll(actions);

        SequentialActionGroup actionGroup = new SequentialActionGroup(mScheduler, mClock, mActions, mActionQueue);

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

        mActions.addAll(actions);

        SequentialActionGroup actionGroup = new SequentialActionGroup(mScheduler, mClock, mActions, mActionQueue);

        actionGroup.initialize();
        actionGroup.execute();

        verify(actions.get(0), times(1)).end(eq(false));
    }

    @Test
    public void interrupted_actionIsRunning_cancelsIt() throws Exception {
        Action action = ActionsMock.actionMocker()
                .build();
        SynchronousActionContext context = ActionsMock.synchronousActionContextMocker(action)
                .mockRunning(true)
                .mockNextRunFinished(false)
                .build();

        mActions.add(action);
        mActionQueue.add(context);

        SequentialActionGroup actionGroup = new SequentialActionGroup(mScheduler, mClock, mActions, mActionQueue);

        actionGroup.execute();
        actionGroup.end(true);

        verify(context, times(1)).cancelAndFinish();
    }
}