package edu.flash3388.flashlib.robot;

/**
 * Interface for a task to be executed by the Scheduler. Unlike an Action, this task is simple.
 * It runs without relation to systems and without phases.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
@FunctionalInterface
public interface ScheduledTask {
	/**
	 * Runs the task. If the task returns false, it is removed.
	 * @return true to continue running, false otherwise
	 */
	boolean run();
}
