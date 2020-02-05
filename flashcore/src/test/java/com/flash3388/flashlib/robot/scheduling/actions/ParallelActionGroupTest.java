package com.flash3388.flashlib.robot.scheduling.actions;

import com.flash3388.flashlib.robot.ClockMock;
import com.flash3388.flashlib.robot.RunningRobotMock;
import com.flash3388.flashlib.time.Clock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class ParallelActionGroupTest {

    private Clock mClock;

    @BeforeEach
    public void setup() {
        mClock = ClockMock.mockInvalidTimeClock();

        RunningRobotMock.mockRobotWithDependencies();
        RunningRobotMock.mockRobotWithClock(mClock);
    }


    @Test
    public void execute_actionsCurrentlyRunning_executesThem() throws Exception {
        List<Action> actions = Arrays.asList(
                ActionsMock.actionMocker().build(),
                ActionsMock.actionMocker().build()
        );

        ParallelActionGroup actionGroup = new ParallelActionGroup()
                .add(actions);

        actionGroup.initialize();
        actionGroup.execute();

        actions.forEach((action) -> verify(action, times(1)).execute());
    }

    @Test
    public void execute_actionCurrentlyRunningIsFinished_endsIt() throws Exception {
        List<Action> actions = Collections.singletonList(
                ActionsMock.actionMocker().isFinished(true).build()
        );

        ParallelActionGroup actionGroup = new ParallelActionGroup()
                .add(actions);

        actionGroup.initialize();
        actionGroup.execute();

        verify(actions.get(0), times(1)).end(eq(false));
    }

    @Test
    public void interrupted_actionIsRunning_cancelsIt() throws Exception {
        List<Action> actions = Collections.singletonList(
                ActionsMock.actionMocker().build()
        );
        Collection<ActionContext> runningActions = new ArrayList<>(Collections.singleton(
            ActionsMock.contextMocker().runFinished(false).build()
        ));

        ParallelActionGroup actionGroup = new ParallelActionGroup(
                mClock, actions, runningActions);

        actionGroup.execute();
        actionGroup.end(true);

        verify(runningActions.iterator().next(), times(1)).runCanceled();
    }
}