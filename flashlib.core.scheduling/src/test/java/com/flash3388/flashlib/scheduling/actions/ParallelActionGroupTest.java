package com.flash3388.flashlib.scheduling.actions;

import com.flash3388.flashlib.scheduling.Scheduler;
import com.flash3388.flashlib.scheduling.impl.SynchronousActionContext;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.time.ClockMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class ParallelActionGroupTest {

    private Scheduler mScheduler;
    private Clock mClock;
    private List<Action> mActions;
    private Collection<SynchronousActionContext> mRunningActions;

    @BeforeEach
    public void setup() {
        mScheduler = mock(Scheduler.class);
        mClock = ClockMock.mockInvalidTimeClock();
        mActions = new ArrayList<>();
        mRunningActions = new ArrayList<>();
    }

    @Test
    public void execute_actionsCurrentlyRunning_executesThem() throws Exception {
        List<Action> actions = Arrays.asList(
                ActionsMock.actionMocker().build(),
                ActionsMock.actionMocker().build()
        );

        mActions.addAll(actions);

        ParallelActionGroup actionGroup = new ParallelActionGroup(mScheduler, mClock, mActions, mRunningActions);

        actionGroup.initialize();
        actionGroup.execute();

        actions.forEach((action) -> verify(action, times(1)).execute());
    }

    @Test
    public void execute_actionCurrentlyRunningIsFinished_endsIt() throws Exception {
        Action action = ActionsMock.actionMocker()
                .mockIsFinished(true)
                .build();
        mActions.add(action);

        ParallelActionGroup actionGroup = new ParallelActionGroup(mScheduler, mClock, mActions, mRunningActions);

        actionGroup.initialize();
        actionGroup.execute();

        verify(action, times(1)).end(eq(false));
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
        mRunningActions.add(context);

        ParallelActionGroup actionGroup = new ParallelActionGroup(mScheduler, mClock, mActions, mRunningActions);

        actionGroup.execute();
        actionGroup.end(true);

        verify(context, times(1)).cancelAndFinish();
    }
}