package com.flash3388.flashlib.scheduling.triggers;

import com.beans.BooleanProperty;
import com.beans.properties.SimpleBooleanProperty;
import com.flash3388.flashlib.robot.RunningRobotMock;
import com.flash3388.flashlib.scheduling.Scheduler;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class SchedulerTriggerActivationActionTest {

    @Test
    public void execute_conditionNotMetLastAndIsMetNow_triggerActivates() throws Exception {
        RunningRobotMock.mockRobotWithDependencies();

        SchedulerTrigger trigger = mock(SchedulerTrigger.class);
        BooleanProperty condition = new SimpleBooleanProperty(false);

        TriggerActivationAction triggerActivationAction = new TriggerActivationAction(mock(Scheduler.class),
                condition, trigger);
        triggerActivationAction.execute(); // not met

        condition.setAsBoolean(true);
        triggerActivationAction.execute(); // is met

        verify(trigger, times(1)).activate();
    }

    @Test
    public void execute_conditionMetLastAndNotMetNow_triggerDeactivates() throws Exception {
        RunningRobotMock.mockRobotWithDependencies();

        SchedulerTrigger trigger = mock(SchedulerTrigger.class);
        BooleanProperty condition = new SimpleBooleanProperty(true);

        TriggerActivationAction triggerActivationAction = new TriggerActivationAction(mock(Scheduler.class),
                condition, trigger);
        triggerActivationAction.execute(); // is met

        condition.setAsBoolean(false);
        triggerActivationAction.execute(); // not met

        verify(trigger, times(1)).deactivate();
    }
}