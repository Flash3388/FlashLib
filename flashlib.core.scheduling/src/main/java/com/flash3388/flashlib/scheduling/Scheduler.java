package com.flash3388.flashlib.scheduling;

import com.flash3388.flashlib.scheduling.actions.Action;

import java.util.Optional;
import java.util.function.Predicate;

/**
 * Scheduler is responsible for executing tasks for robots.
 * <p>
 * For more complex operations, the scheduler can use {@link Action} objects. Those objects can be added
 * to the scheduler and then executed as well. Unlike simple tasks, scheduling might depend on {@link Requirement}
 * objects for operations. The scheduler tracks the required systems of each action making sure that only one
 * {@link Action} object runs on a {@link Requirement} at any given time.
 * <p>
 * In addition, the scheduler can allow {@link Subsystem} to hold default {@link Action} objects, which
 * run only if no {@link Action} is using the {@link Subsystem} at the moment. When the scheduler runs,
 * it checks all registered {@link Requirement} objects to see if one does not have an action at the moment.
 *
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public interface Scheduler {

    void start(Action action);
    void cancel(Action action);
    boolean isRunning(Action action);

    void cancelActionsIf(Predicate<? super Action> predicate);
    void cancelAllActions();

    void setDefaultAction(Subsystem subsystem, Action action);
    Optional<Action> getActionRunningOnRequirement(Requirement requirement);

    void run(SchedulerMode mode);
}