package edu.flash3388.flashlib.robot.scheduling.actions;

import edu.flash3388.flashlib.robot.scheduling.Action;
import edu.flash3388.flashlib.robot.scheduling.Scheduler;
import edu.flash3388.flashlib.time.JavaMillisClock;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntSupplier;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

public class SelectableActionTest {

    @Test
    public void initialize_actionSelectedBySupplier_actionStarts() throws Exception {
        List<Action> actions = new ArrayList<>();
        actions.add(mock(Action.class));
        actions.add(mock(Action.class));
        actions.add(mock(Action.class));

        IntSupplier supplier = ()-> 0;

        SelectableAction selectableAction = new SelectableAction(new Scheduler(), new JavaMillisClock(), supplier, actions);
        selectableAction.initialize();

        verify(actions.get(0), times(1)).start();

        for (int i = 1; i < actions.size(); i++) {
            verify(actions.get(i), times(0)).start();
        }
    }

    @Test
    public void isFinished_delegatedActionIsNotRunning_returnTrue() throws Exception {
        List<Action> actions = new ArrayList<>();
        actions.add(mock(Action.class));
        actions.add(mock(Action.class));
        actions.add(mock(Action.class));

        IntSupplier supplier = ()-> 0;

        SelectableAction selectableAction = new SelectableAction(new Scheduler(), new JavaMillisClock(), supplier, actions);
        selectableAction.initialize();

        Action runningAction = actions.get(0);
        when(runningAction.isRunning()).thenReturn(false);

        assertTrue(selectableAction.isFinished());
    }

    @Test
    public void isFinished_delegatedActionIsRunning_returnFalse() throws Exception {
        List<Action> actions = new ArrayList<>();
        actions.add(mock(Action.class));
        actions.add(mock(Action.class));
        actions.add(mock(Action.class));

        IntSupplier supplier = ()-> 0;

        SelectableAction selectableAction = new SelectableAction(new Scheduler(), new JavaMillisClock(), supplier, actions);
        selectableAction.initialize();

        Action runningAction = actions.get(0);
        when(runningAction.isRunning()).thenReturn(true);

        assertFalse(selectableAction.isFinished());
    }

    @Test
    public void end_delegatedActionIsRunning_cancelsAction() throws Exception {
        List<Action> actions = new ArrayList<>();
        actions.add(mock(Action.class));
        actions.add(mock(Action.class));
        actions.add(mock(Action.class));

        IntSupplier supplier = ()-> 0;

        SelectableAction selectableAction = new SelectableAction(new Scheduler(), new JavaMillisClock(), supplier, actions);
        selectableAction.initialize();

        Action runningAction = actions.get(0);
        when(runningAction.isRunning()).thenReturn(true);

        selectableAction.end();

        verify(runningAction, times(1)).cancel();
    }
}