package com.flash3388.flashlib.robot.scheduling;

import com.beans.BooleanProperty;
import com.beans.properties.SimpleBooleanProperty;
import com.flash3388.flashlib.time.JavaMillisClock;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class ActionGroupTest {

    @Test
    public void execute_sequentialExecution_executesActionsByOrder() throws Exception {
        BooleanProperty isFirstActionRunning = new SimpleBooleanProperty(true);

        List<Action> actions = new ArrayList<>();
        actions.add(mockActionRunningBySupplier(isFirstActionRunning));
        actions.add(mockActionNotRunning());

        ActionGroup actionGroup = new ActionGroup(new Scheduler(), new JavaMillisClock(), ExecutionOrder.SEQUENTIAL, actions);
        actionGroup.initialize();

        actionGroup.execute();

        verify(actions.get(0), times(1)).startAction();
        verify(actions.get(1), times(0)).startAction();

        actionGroup.execute();

        verify(actions.get(1), times(0)).startAction();

        isFirstActionRunning.setAsBoolean(false);
        actionGroup.execute();
        actionGroup.execute();

        verify(actions.get(1), times(1)).startAction();

        assertTrue(actionGroup.isFinished());
    }

    @Test
    public void execute_parallelExecution_executesActionsInParallel() throws Exception {
        List<Action> actions = new ArrayList<>();
        actions.add(mockActionRunning());
        actions.add(mockActionRunning());

        ActionGroup actionGroup = new ActionGroup(new Scheduler(), new JavaMillisClock(), ExecutionOrder.PARALLEL, actions);
        actionGroup.initialize();

        actionGroup.execute();

        verify(actions.get(0), times(1)).startAction();
        verify(actions.get(1), times(0)).startAction();

        actionGroup.execute();
    }

    @Test
    public void whenInterrupted_actionWasInterrupted_callsInterruptionRunnable() throws Exception {
        final Runnable INTERRUPTION_TASK = mock(Runnable.class);

        List<Action> actions = new ArrayList<>();
        actions.add(mockActionRunning());
        actions.add(mockActionRunning());

        ActionGroup actionGroup = new ActionGroup(new Scheduler(), new JavaMillisClock(), ExecutionOrder.PARALLEL, actions);
        actionGroup.whenInterrupted(INTERRUPTION_TASK);

        actionGroup.startAction();
        actionGroup.initialize();
        actionGroup.interrupted();

        verify(INTERRUPTION_TASK, times(1)).run();
    }

    private Action mockActionNotRunning() {
        Action action = mock(Action.class);
        when(action.run()).thenReturn(false);

        return action;
    }

    private Action mockActionRunning() {
        Action action = mock(Action.class);
        when(action.run()).thenReturn(true);

        return action;
    }

    private Action mockActionRunningBySupplier(BooleanSupplier supplier) {
        Action action = mock(Action.class);
        when(action.run()).thenAnswer(new Answer<Boolean>() {
            @Override
            public Boolean answer(InvocationOnMock invocation) throws Throwable {
                return supplier.getAsBoolean();
            }
        });

        return action;
    }
}