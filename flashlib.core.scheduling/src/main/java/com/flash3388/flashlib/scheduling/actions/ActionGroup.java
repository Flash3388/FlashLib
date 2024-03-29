package com.flash3388.flashlib.scheduling.actions;

import java.util.Collection;

public interface ActionGroup extends Action {

    /**
     * <p>
     *     Adds a new action to this group.
     * </p>
     *
     * @param action action to add.
     *
     * @return this
     */
    ActionGroup add(Action action);

    /**
     * <p>
     *     Adds new actions to this group.
     * </p>
     *
     * @param actions actions to add.
     *
     * @return this
     */
    ActionGroup add(Action... actions);

    /**
     * <p>
     *     Adds new actions to this group.
     * </p>
     *
     * @param actions actions to add.
     *
     * @return this
     */
    ActionGroup add(Collection<Action> actions);

    /**
     * Sets a callback to run when the action group is interrupted.
     *
     * @return this
     */
    ActionGroup whenInterrupted(Runnable runnable);
}
