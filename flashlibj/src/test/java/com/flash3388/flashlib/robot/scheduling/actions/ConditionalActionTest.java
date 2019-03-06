package com.flash3388.flashlib.robot.scheduling.actions;

import com.flash3388.flashlib.robot.scheduling.Action;
import com.flash3388.flashlib.robot.scheduling.Scheduler;
import com.flash3388.flashlib.time.JavaMillisClock;
import org.junit.Test;

import java.util.function.BooleanSupplier;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ConditionalActionTest {

    @Test
    public void initialize_supplierReturnsTrue_trueActionStarts() throws Exception {
        Action actionTrue = mock(Action.class);
        Action actionFalse = mock(Action.class);

        BooleanSupplier supplier = ()-> true;

        ConditionalAction conditionalAction = new ConditionalAction(new Scheduler(), new JavaMillisClock(), supplier, actionTrue, actionFalse);
        conditionalAction.initialize();

        verify(actionTrue, times(1)).start();
        verify(actionFalse, times(0)).start();
    }

    @Test
    public void initialize_supplierReturnsFalse_falseActionStarts() throws Exception {
        Action actionTrue = mock(Action.class);
        Action actionFalse = mock(Action.class);

        BooleanSupplier supplier = ()-> false;

        ConditionalAction conditionalAction = new ConditionalAction(new Scheduler(), new JavaMillisClock(), supplier, actionTrue, actionFalse);
        conditionalAction.initialize();

        verify(actionTrue, times(0)).start();
        verify(actionFalse, times(1)).start();
    }

    @Test
    public void isFinished_delegatedActionIsNotRunning_returnTrue() throws Exception {
        Action actionTrue = mock(Action.class);
        Action actionFalse = mock(Action.class);

        BooleanSupplier supplier = ()-> true;

        ConditionalAction conditionalAction = new ConditionalAction(new Scheduler(), new JavaMillisClock(), supplier, actionTrue, actionFalse);
        conditionalAction.initialize();

        Action runningAction = actionTrue;
        when(runningAction.isRunning()).thenReturn(false);

        assertTrue(conditionalAction.isFinished());
    }

    @Test
    public void isFinished_delegatedActionIsRunning_returnFalse() throws Exception {
        Action actionTrue = mock(Action.class);
        Action actionFalse = mock(Action.class);

        BooleanSupplier supplier = ()-> true;

        ConditionalAction conditionalAction = new ConditionalAction(new Scheduler(), new JavaMillisClock(), supplier, actionTrue, actionFalse);
        conditionalAction.initialize();

        Action runningAction = actionTrue;
        when(runningAction.isRunning()).thenReturn(true);

        assertFalse(conditionalAction.isFinished());
    }

    @Test
    public void end_delegatedActionIsRunning_cancelsAction() throws Exception {
        Action actionTrue = mock(Action.class);
        Action actionFalse = mock(Action.class);

        BooleanSupplier supplier = ()-> true;

        ConditionalAction conditionalAction = new ConditionalAction(new Scheduler(), new JavaMillisClock(), supplier, actionTrue, actionFalse);
        conditionalAction.initialize();

        Action runningAction = actionTrue;
        when(runningAction.isRunning()).thenReturn(true);

        conditionalAction.end();

        verify(runningAction, times(1)).cancel();
    }
}