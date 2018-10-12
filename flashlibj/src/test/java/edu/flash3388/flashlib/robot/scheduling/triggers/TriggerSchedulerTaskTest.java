package edu.flash3388.flashlib.robot.scheduling.triggers;

import com.beans.BooleanProperty;
import com.beans.properties.SimpleBooleanProperty;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class TriggerSchedulerTaskTest {

    @Test
    public void run_conditionNotMetLastAndIsMetNow_triggerActivates() throws Exception {
        Trigger trigger = mock(Trigger.class);
        BooleanProperty condition = new SimpleBooleanProperty(false);

        TriggerSchedulerTask triggerSchedulerTask = new TriggerSchedulerTask(condition, trigger);
        triggerSchedulerTask.run(); // not met

        condition.setAsBoolean(true);
        triggerSchedulerTask.run(); // is met

        verify(trigger, times(1)).activate();
    }

    @Test
    public void run_conditionMetLastAndNotMetNow_triggerDeactivates() throws Exception {
        Trigger trigger = mock(Trigger.class);
        BooleanProperty condition = new SimpleBooleanProperty(true);

        TriggerSchedulerTask triggerSchedulerTask = new TriggerSchedulerTask(condition, trigger);
        triggerSchedulerTask.run(); // is met

        condition.setAsBoolean(false);
        triggerSchedulerTask.run(); // not met

        verify(trigger, times(1)).deactivate();
    }
}