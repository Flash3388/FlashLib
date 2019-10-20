package com.flash3388.flashlib.robot.scheduling;

import com.beans.BooleanProperty;
import com.beans.properties.SimpleBooleanProperty;
import com.flash3388.flashlib.time.SystemMillisClock;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BooleanSupplier;

import static com.flash3388.flashlib.robot.scheduling.ActionsMock.mockActionWithRequirement;
import static com.flash3388.flashlib.robot.scheduling.ActionsMock.mockNotRunningAction;
import static com.flash3388.flashlib.robot.scheduling.ActionsMock.mockRunningAction;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class ActionGroupTest {

    @Test
    public void execute_sequentialExecution_executesActionsByOrder() throws Exception {
        BooleanProperty isFirstActionRunning = new SimpleBooleanProperty(true);

        List<Action> actions = new ArrayList<>();
        actions.add(mockActionRunningBySupplier(isFirstActionRunning));
        actions.add(mockNotRunningAction());

        ActionGroup actionGroup = new ActionGroup(new Scheduler(), new SystemMillisClock(), ExecutionOrder.SEQUENTIAL, actions);
        actionGroup.initialize();

        actionGroup.execute();

        verify(actions.get(0), times(1)).markStarted();
        verify(actions.get(1), never()).markStarted();

        actionGroup.execute();

        verify(actions.get(1), never()).markStarted();

        isFirstActionRunning.setAsBoolean(false);
        actionGroup.execute();
        actionGroup.execute();

        verify(actions.get(1), times(1)).markStarted();

        assertTrue(actionGroup.isFinished());
    }

    @Test
    public void execute_parallelExecution_executesActionsInParallel() throws Exception {
        BooleanProperty isFirstActionRunning = new SimpleBooleanProperty(true);
        BooleanProperty isSecondActionRunning = new SimpleBooleanProperty(true);

        List<Action> actions = new ArrayList<>();
        actions.add(mockActionRunningBySupplier(isFirstActionRunning));
        actions.add(mockActionRunningBySupplier(isSecondActionRunning));

        ActionGroup actionGroup = new ActionGroup(new Scheduler(), new SystemMillisClock(), ExecutionOrder.PARALLEL, actions);
        actionGroup.initialize();

        actionGroup.execute();

        verify(actions.get(0), times(1)).markStarted();
        verify(actions.get(1), never()).markStarted();

        actionGroup.execute();

        verify(actions.get(0), times(1)).markStarted();
        verify(actions.get(1), times(1)).markStarted();

        isFirstActionRunning.setAsBoolean(false);

        actionGroup.execute();
        assertFalse(actionGroup.isFinished());

        isSecondActionRunning.setAsBoolean(false);

        actionGroup.execute();
        assertTrue(actionGroup.isFinished());
    }

    @Test
    public void whenInterrupted_actionWasInterrupted_callsInterruptionRunnable() throws Exception {
        final Runnable INTERRUPTION_TASK = mock(Runnable.class);

        List<Action> actions = new ArrayList<>();
        actions.add(mockRunningAction());
        actions.add(mockRunningAction());

        ActionGroup actionGroup = new ActionGroup(new Scheduler(), new SystemMillisClock(), ExecutionOrder.PARALLEL, actions);
        actionGroup.whenInterrupted(INTERRUPTION_TASK);

        actionGroup.markStarted();
        actionGroup.initialize();
        actionGroup.interrupted();

        verify(INTERRUPTION_TASK, times(1)).run();
    }

    @Test
    public void cancel_actionWasInterrupted_interruptsContainedActions() throws Exception {
        List<Action> actions = new ArrayList<>();
        actions.add(mockRunningAction());

        ActionGroup actionGroup = new ActionGroup(new Scheduler(), new SystemMillisClock(), ExecutionOrder.PARALLEL, actions);

        actionGroup.start();
        actionGroup.run();
        actionGroup.cancel();
        actionGroup.removed();

        for (Action action : actions) {
            verify(action, times(1)).markCanceled();
            verify(action, times(1)).removed();
        }
    }

    @Test
    public void start_withActions_hasRequirementsOfContainedActions() throws Exception {
        Set<Subsystem> requirements = new HashSet<>();
        requirements.add(mock(Subsystem.class));

        List<Action> actions = new ArrayList<>();
        actions.add(mockActionWithRequirement(requirements));

        ActionGroup actionGroup = new ActionGroup(new Scheduler(), new SystemMillisClock(), ExecutionOrder.PARALLEL, actions);
        actionGroup.start();

        assertThat(actionGroup.getRequirements(),hasItems(requirements.toArray(new Subsystem[0])));
    }

    @Test(expected = IllegalArgumentException.class)
    public void add_parallelActionsWithJointRequirements_throwsIllegalArgumentException() throws Exception {
        Subsystem subsystem = mock(Subsystem.class);

        List<Action> actions = new ArrayList<>();
        actions.add(mockActionWithRequirement(subsystem));

        ActionGroup actionGroup = new ActionGroup(new Scheduler(), new SystemMillisClock(), ExecutionOrder.PARALLEL, actions);
        actionGroup.add(mockActionWithRequirement(subsystem));
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