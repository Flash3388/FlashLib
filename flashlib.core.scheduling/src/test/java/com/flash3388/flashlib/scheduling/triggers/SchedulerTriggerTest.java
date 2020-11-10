package com.flash3388.flashlib.scheduling.triggers;

import com.flash3388.flashlib.scheduling.actions.Action;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class SchedulerTriggerTest {

    @Test
    public void setState_stateChanges_updatesListenersWithChange() throws Exception {
        TriggerStateListener listener = mock(TriggerStateListener.class);

        SchedulerTrigger trigger = new SchedulerTrigger(TriggerState.INACTIVE);
        trigger.addStateListener(listener);

        trigger.setState(TriggerState.ACTIVE);

        verify(listener, times(1)).onStateChange(
                eq(TriggerState.ACTIVE),
                eq(TriggerState.INACTIVE));
    }

    @Test
    public void whenActive_triggerActivates_startAction() throws Exception {
        Action mockAction = mock(Action.class);

        SchedulerTrigger trigger = new SchedulerTrigger(TriggerState.INACTIVE);
        trigger.whenActive(mockAction);

        trigger.activate();

        verify(mockAction, times(1)).start();
    }

    @Test
    public void whenInactive_triggerDeactivates_startAction() throws Exception {
        Action mockAction = mock(Action.class);

        SchedulerTrigger trigger = new SchedulerTrigger(TriggerState.ACTIVE);
        trigger.whenInactive(mockAction);

        trigger.deactivate();

        verify(mockAction, times(1)).start();
    }

    @Test
    public void cancelWhenActive_triggerActivates_cancelsAction() throws Exception {
        Action mockAction = mock(Action.class);

        SchedulerTrigger trigger = new SchedulerTrigger(TriggerState.INACTIVE);
        trigger.cancelWhenActive(mockAction);

        trigger.activate();

        verify(mockAction, times(1)).cancel();
    }

    @Test
    public void cancelWhenInactive_triggerDeactivates_cancelsAction() throws Exception {
        Action mockAction = mock(Action.class);

        SchedulerTrigger trigger = new SchedulerTrigger(TriggerState.ACTIVE);
        trigger.cancelWhenInactive(mockAction);

        trigger.deactivate();

        verify(mockAction, times(1)).cancel();
    }

    @Test
    public void whileActive_triggerActivates_startAction() throws Exception {
        Action mockAction = mock(Action.class);

        SchedulerTrigger trigger = new SchedulerTrigger(TriggerState.INACTIVE);
        trigger.whileActive(mockAction);

        trigger.activate();

        verify(mockAction, times(1)).start();
    }

    @Test
    public void whileActive_triggerDeactivates_cancelsAction() throws Exception {
        Action mockAction = mock(Action.class);

        SchedulerTrigger trigger = new SchedulerTrigger(TriggerState.ACTIVE);
        trigger.whileActive(mockAction);

        trigger.deactivate();

        verify(mockAction, times(1)).cancel();
    }
}