package com.flash3388.flashlib.scheduling.triggers;

import com.beans.BooleanProperty;
import com.beans.properties.SimpleBooleanProperty;
import com.flash3388.flashlib.scheduling.ActionControl;
import com.flash3388.flashlib.scheduling.Scheduler;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

public class TriggerActivationActionTest {

    @Test
    public void execute_conditionNotMetLastAndIsMetNow_triggerActivates() throws Exception {
        TriggerImpl trigger = mock(TriggerImpl.class);
        BooleanProperty condition = new SimpleBooleanProperty(false);

        TriggerActivationAction triggerActivationAction = new TriggerActivationAction(mock(Scheduler.class),
                condition, trigger);
        triggerActivationAction.execute(mock(ActionControl.class)); // not met

        condition.setAsBoolean(true);
        triggerActivationAction.execute(mock(ActionControl.class)); // is met

        verify(trigger, times(1)).activate();
    }

    @Test
    public void execute_conditionMetLastAndNotMetNow_triggerDeactivates() throws Exception {
        TriggerImpl trigger = mock(TriggerImpl.class);
        BooleanProperty condition = new SimpleBooleanProperty(true);

        TriggerActivationAction triggerActivationAction = new TriggerActivationAction(mock(Scheduler.class),
                condition, trigger);
        triggerActivationAction.execute(mock(ActionControl.class)); // is met

        condition.setAsBoolean(false);
        triggerActivationAction.execute(mock(ActionControl.class)); // not met

        verify(trigger, times(1)).deactivate();
    }
}