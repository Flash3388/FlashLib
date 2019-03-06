package com.flash3388.flashlib.robot.scheduling;

@FunctionalInterface
public interface SchedulerTask {

    /**
     * Run the task.
     *
     * @return true, if the task should run again, false otherwise
     */
    boolean run();
}
