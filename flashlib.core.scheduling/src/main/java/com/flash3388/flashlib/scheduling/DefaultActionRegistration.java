package com.flash3388.flashlib.scheduling;

import com.flash3388.flashlib.scheduling.actions.Action;

import java.util.Optional;

/**
 * Describes registration of a default {@link com.flash3388.flashlib.scheduling.actions.Action}
 * for a {@link Subsystem} with the {@link Scheduler}.
 * <p>
 * Can be used to both monitor and control the action.
 * <p>
 * Properties accessed here will be stored and associated with the action even if it is not running
 * at the moment. They will not be deleted post finish, but only if the registration is removed or replaced.
 *
 * @since FlashLib 3.3.0
 */
public interface DefaultActionRegistration extends ActionPropertyAccessor { //TODO: UPDATE SINCE TO RIGHT VERSION

    /**
     * Indicates that the action associated with this registration is still registered
     * as the default action of a subsystem.
     * Registration may be changed by calls to {@link Scheduler#setDefaultAction(Subsystem, Action)}.
     *
     * @return <b>true</b> if action is still registered, <b>false</b> otherwise.
     */
    boolean isRegistered();

    /**
     * Indicates that the action is currently running.
     *
     * @return <b>true</b> if action is running, <b>false</b> otherwise.
     * @throws IllegalStateException if {@link #isRegistered()} is false.
     * @see ScheduledAction#isRunning()
     */
    boolean isRunning();

    /**
     * Gets the latest {@link ScheduledAction} associated with this action. {@link ScheduledAction} will
     * only be created once the action has started running. As such, until the default action has
     * started it's first run, this will return {@link Optional#empty()}. If the action has started and stopped
     * at least once and then started again, the returned instance will refer to the latest execution of the action,
     * and will become stale once the action has finished its next run.
     *
     * @return the latest {@link ScheduledAction} associated with this action, or empty if the action hasn't started
     *  running at all.
     * @throws IllegalStateException if {@link #isRegistered()} is false.
     */
    Optional<ScheduledAction> getLastScheduled();
}
