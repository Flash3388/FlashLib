package com.flash3388.flashlib.scheduling;

import com.flash3388.flashlib.scheduling.actions.Action;

import java.lang.ref.WeakReference;
import java.util.Objects;
import java.util.Optional;

/**
 * A specialized {@link Requirement} representing a subsystem of a robot. Other than being a possible
 * requirement of {@link Action Actions}, Subsystems offer additional functionality. It is recommended that robot
 * subsystems be represented in separate classes, all of which extend this class. Each such subsystem, should
 * have only one instance.
 * <pre>
 *     class DriveSystem extends Subsystem {
 *        // subsystem code
 *     }
 * </pre>
 * Doing so, isolates a subsystem to allow running only a single action using it at any given time.
 * <p>
 *     Subsystems also support default actions. Those are actions which will run automatically whenever
 *     no other action is running which uses the subsystem.
 * </p>
 *
 * @since FlashLib 1.0.0
 */
public class Subsystem implements Requirement {

    private final WeakReference<Scheduler> mScheduler;

    protected Subsystem(Scheduler scheduler) {
        Objects.requireNonNull(scheduler, "scheduler null");

        mScheduler = new WeakReference<>(scheduler);
    }

    protected Subsystem() {
        this(GlobalScheduler.getScheduler());
    }

    /**
     * Gets the current default action registration for this subsystem.
     *
     * @return last {@link DefaultActionRegistration}, or empty if no registration was done.
     * @see Scheduler#getDefaultActionRegistration(Subsystem)
     */
    public Optional<DefaultActionRegistration> getDefaultActionRegistration() {
        Scheduler scheduler = mScheduler.get();
        if (scheduler == null) {
            throw new IllegalStateException("scheduler garbage collected");
        }

        return scheduler.getDefaultActionRegistration(this);
    }

    /**
     * Sets the default action of this subsystem.
     * <p>
     *     There can be only one default action for any subsystem. Such that calling this twice
     *     will overwrite any previously set actions.
     * </p>
     *
     * @param action action to set as default.
     *
     * @return {@link DefaultActionRegistration} describing the action's registration
     *
     * @see Scheduler#setDefaultAction(Subsystem, Action)
     */
    public DefaultActionRegistration setDefaultAction(Action action) {
        Scheduler scheduler = mScheduler.get();
        if (scheduler == null) {
            throw new IllegalStateException("scheduler garbage collected");
        }

        return scheduler.setDefaultAction(this, action);
    }

    /**
     * Gets whether there's an action running which uses this subsystem as a requirement.
     *
     * @return <b>true</b> if there is, <b>false</b> otherwise.
     *
     * @see Scheduler#getActionRunningOnRequirement(Requirement)
     */
    public boolean hasCurrentAction() {
        Scheduler scheduler = mScheduler.get();
        if (scheduler == null) {
            throw new IllegalStateException("scheduler garbage collected");
        }

        return scheduler.getActionRunningOnRequirement(this).isPresent();
    }

    /**
     * Cancels the action running which uses this subsystem has a requirement.
     * If there is no such action, nothing happens.
     *
     * @see Scheduler#getActionRunningOnRequirement(Requirement)
     * @see Action#cancel()
     */
    public void cancelCurrentAction() {
        Scheduler scheduler = mScheduler.get();
        if (scheduler == null) {
            throw new IllegalStateException("scheduler garbage collected");
        }

        Optional<Action> currentAction = scheduler.getActionRunningOnRequirement(this);
        currentAction.ifPresent(Action::cancel);
    }

    @Override
    public String toString() {
        return String.format("Subsystem %s",
                getClass().getSimpleName().isEmpty() ?
                        getClass().getName() :
                        getClass().getSimpleName());
    }
}
