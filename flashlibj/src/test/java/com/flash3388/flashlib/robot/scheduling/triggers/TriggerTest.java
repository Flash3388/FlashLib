package com.flash3388.flashlib.robot.scheduling.triggers;

import com.flash3388.flashlib.robot.scheduling.Action;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class TriggerTest {

    @Test
    public void whenActive_triggerActivates_startAction() throws Exception {
        Action mockAction = mock(Action.class);

        Trigger trigger = new Trigger();
        trigger.whenActive(mockAction);

        trigger.activate();

        verify(mockAction, times(1)).start();
    }

    @Test
    public void whenInactive_triggerDeactivates_startAction() throws Exception {
        Action mockAction = mock(Action.class);

        Trigger trigger = new Trigger();
        trigger.whenInactive(mockAction);

        trigger.deactivate();

        verify(mockAction, times(1)).start();
    }

    @Test
    public void cancelWhenActive_triggerActivates_cancelsAction() throws Exception {
        Action mockAction = mock(Action.class);

        Trigger trigger = new Trigger();
        trigger.cancelWhenActive(mockAction);

        trigger.activate();

        verify(mockAction, times(1)).cancel();
    }

    @Test
    public void cancelWhenInactive_triggerDeactivates_cancelsAction() throws Exception {
        Action mockAction = mock(Action.class);

        Trigger trigger = new Trigger();
        trigger.cancelWhenInactive(mockAction);

        trigger.deactivate();

        verify(mockAction, times(1)).cancel();
    }

    @Test
    public void whileActive_triggerActivates_startAction() throws Exception {
        Action mockAction = mock(Action.class);

        Trigger trigger = new Trigger();
        trigger.whileActive(mockAction);

        trigger.activate();

        verify(mockAction, times(1)).start();
    }

    @Test
    public void whileActive_triggerDeactivates_cancelsAction() throws Exception {
        Action mockAction = mock(Action.class);

        Trigger trigger = new Trigger();
        trigger.whileActive(mockAction);

        trigger.deactivate();

        verify(mockAction, times(1)).cancel();
    }
}