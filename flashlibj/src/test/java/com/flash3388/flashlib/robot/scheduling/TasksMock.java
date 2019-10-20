package com.flash3388.flashlib.robot.scheduling;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TasksMock {

    public static SchedulerTask mockRepeatingTask() {
        SchedulerTask task = mock(SchedulerTask.class);
        when(task.run()).thenReturn(true);

        return task;
    }
}
