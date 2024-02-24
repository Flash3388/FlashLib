package com.flash3388.flashlib.scheduling.impl;

import com.flash3388.flashlib.scheduling.ActionControl;
import com.flash3388.flashlib.scheduling.ExecutionContext;
import com.flash3388.flashlib.scheduling.ExecutionState;
import com.flash3388.flashlib.scheduling.FinishReason;
import com.flash3388.flashlib.scheduling.Requirement;
import com.flash3388.flashlib.scheduling.SchedulerMode;
import com.flash3388.flashlib.scheduling.SchedulerModeMock;
import com.flash3388.flashlib.scheduling.actions.Action;
import com.flash3388.flashlib.scheduling.actions.ActionConfiguration;
import com.flash3388.flashlib.scheduling.actions.ActionsMock;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.time.ClockMock;
import com.flash3388.flashlib.time.Time;
import org.hamcrest.collection.IsMapContaining;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.slf4j.Logger;

import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ExecutionContextImplTest {

    @Test
    public void execute_notInitialized_initializes() {
        Action action = ActionsMock.actionMocker()
                .build();
        ExecutionContext executionContext = new ExecutionContextImpl(
                0,
                action,
                action.getConfiguration(),
                mock(ObsrActionContext.class),
                ClockMock.mockClockWithTime(Time.seconds(0)),
                mock(Logger.class)
        );
        executionContext.start();

        ExecutionContext.ExecutionResult result = executionContext.execute();
        assertThat(result, equalTo(ExecutionContext.ExecutionResult.STILL_RUNNING));

        ExecutionState state = executionContext.getState();
        assertThat(state.isExecuting(), equalTo(true));
        assertThat(state.getRunTime().isValid(), equalTo(true));

        verify(action, times(1)).initialize(any(ActionControl.class));
        verify(action, never()).execute(any(ActionControl.class));
        verify(action, never()).end(any(FinishReason.class));
    }

    @Test
    public void execute_initialized_executes() {
        Action action = ActionsMock.actionMocker()
                .build();
        ExecutionContext executionContext = new ExecutionContextImpl(
                0,
                action,
                action.getConfiguration(),
                mock(ObsrActionContext.class),
                ClockMock.mockClockWithTime(Time.seconds(0)),
                mock(Logger.class)
        );

        executionContext.start();
        executionContext.execute();

        ExecutionContext.ExecutionResult result = executionContext.execute();
        assertThat(result, equalTo(ExecutionContext.ExecutionResult.STILL_RUNNING));

        ExecutionState state = executionContext.getState();
        assertThat(state.isExecuting(), equalTo(true));
        assertThat(state.getRunTime().isValid(), equalTo(true));

        verify(action, times(1)).initialize(any(ActionControl.class));
        verify(action, times(1)).execute(any(ActionControl.class));
        verify(action, never()).end(any(FinishReason.class));
    }

    @Test
    public void execute_controlMarkedToFinish_finishes() {
        Action action = ActionsMock.actionMocker()
                .mockIsFinished(true)
                .build();
        ExecutionContext executionContext = new ExecutionContextImpl(
                0,
                action,
                action.getConfiguration(),
                mock(ObsrActionContext.class),
                ClockMock.mockClockWithTime(Time.seconds(0)),
                mock(Logger.class)
        );

        executionContext.start();
        executionContext.execute();

        ExecutionContext.ExecutionResult result = executionContext.execute();
        assertThat(result, equalTo(ExecutionContext.ExecutionResult.FINISHED));

        ExecutionState state = executionContext.getState();
        assertThat(state.isFinished(), equalTo(true));
        assertThat(state.getFinishReason(), equalTo(FinishReason.FINISHED));

        verify(action, times(1)).initialize(any(ActionControl.class));
        verify(action, times(1)).execute(any(ActionControl.class));
        verify(action, times(1)).end(ArgumentMatchers.eq(FinishReason.FINISHED));
    }

    @Test
    public void execute_externallyCancelled_finishes() {
        Action action = ActionsMock.actionMocker()
                .build();
        ExecutionContext executionContext = new ExecutionContextImpl(
                0,
                action,
                action.getConfiguration(),
                mock(ObsrActionContext.class),
                ClockMock.mockClockWithTime(Time.seconds(0)),
                mock(Logger.class)
        );

        executionContext.start();
        executionContext.execute();

        executionContext.markInterrupted();

        ExecutionContext.ExecutionResult result = executionContext.execute();
        assertThat(result, equalTo(ExecutionContext.ExecutionResult.FINISHED));

        ExecutionState state = executionContext.getState();
        assertThat(state.isFinished(), equalTo(true));
        assertThat(state.getFinishReason(), equalTo(FinishReason.CANCELED));

        verify(action, times(1)).initialize(any(ActionControl.class));
        verify(action, never()).execute(any(ActionControl.class));
        verify(action, times(1)).end(ArgumentMatchers.eq(FinishReason.CANCELED));
    }

    @Test
    public void execute_externallyInterrupted_finishes() {
        Action action = ActionsMock.actionMocker()
                .build();
        ExecutionContext executionContext = new ExecutionContextImpl(
                0,
                action,
                action.getConfiguration(),
                mock(ObsrActionContext.class),
                ClockMock.mockClockWithTime(Time.seconds(0)),
                mock(Logger.class)
        );

        executionContext.start();
        executionContext.execute();

        executionContext.interrupt();

        ExecutionState state = executionContext.getState();
        assertThat(state.isFinished(), equalTo(true));
        assertThat(state.getFinishReason(), equalTo(FinishReason.CANCELED));

        verify(action, times(1)).initialize(any(ActionControl.class));
        verify(action, never()).execute(any(ActionControl.class));
        verify(action, times(1)).end(ArgumentMatchers.eq(FinishReason.CANCELED));
    }

    @Test
    public void execute_externallyInterruptedButNotInitialized_finishes() {
        Action action = ActionsMock.actionMocker()
                .build();
        ExecutionContext executionContext = new ExecutionContextImpl(
                0,
                action,
                action.getConfiguration(),
                mock(ObsrActionContext.class),
                ClockMock.mockClockWithTime(Time.seconds(0)),
                mock(Logger.class)
        );

        executionContext.start();

        executionContext.interrupt();

        ExecutionState state = executionContext.getState();
        assertThat(state.isFinished(), equalTo(true));
        assertThat(state.getFinishReason(), equalTo(FinishReason.CANCELED));

        verify(action, never()).initialize(any(ActionControl.class));
        verify(action, never()).execute(any(ActionControl.class));
        verify(action, never()).end(any(FinishReason.class));
    }

    @Test
    public void execute_modeDisabledAndNotAllowedInDisabledActionNotInitialized_interrupts() {
        Action action = ActionsMock.actionMocker()
                .mockRunWhenDisabled(false)
                .build();
        ExecutionContext executionContext = new ExecutionContextImpl(
                0,
                action,
                action.getConfiguration(),
                mock(ObsrActionContext.class),
                mock(Clock.class),
                mock(Logger.class)
        );
        executionContext.start();

        ExecutionContext.ExecutionResult result = executionContext.execute(SchedulerModeMock.mockDisabledMode());
        assertThat(result, equalTo(ExecutionContext.ExecutionResult.FINISHED));

        ExecutionState state = executionContext.getState();
        assertThat(state.isFinished(), equalTo(true));
        assertThat(state.getFinishReason(), equalTo(FinishReason.CANCELED));

        verify(action, never()).initialize(any(ActionControl.class));
        verify(action, never()).execute(any(ActionControl.class));
        verify(action, never()).end(any(FinishReason.class));
    }

    @Test
    public void execute_modeDisabledAndNotAllowedInDisabledActionInitialized_interrupts() {
        Action action = ActionsMock.actionMocker()
                .mockRunWhenDisabled(false)
                .build();
        ExecutionContext executionContext = new ExecutionContextImpl(
                0,
                action,
                action.getConfiguration(),
                mock(ObsrActionContext.class),
                mock(Clock.class),
                mock(Logger.class)
        );
        executionContext.start();
        executionContext.execute();

        ExecutionContext.ExecutionResult result = executionContext.execute(SchedulerModeMock.mockDisabledMode());
        assertThat(result, equalTo(ExecutionContext.ExecutionResult.FINISHED));

        ExecutionState state = executionContext.getState();
        assertThat(state.isFinished(), equalTo(true));
        assertThat(state.getFinishReason(), equalTo(FinishReason.CANCELED));

        verify(action, times(1)).initialize(any(ActionControl.class));
        verify(action, never()).execute(any(ActionControl.class));
        verify(action, times(1)).end(any(FinishReason.class));
    }
}