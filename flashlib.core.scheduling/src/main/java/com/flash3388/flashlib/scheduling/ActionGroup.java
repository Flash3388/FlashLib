package com.flash3388.flashlib.scheduling;

import java.util.Collection;

public interface ActionGroup extends ActionInterface {

    /**
     * <p>
     *     Adds a new action to this group.
     * </p>
     *
     * @param action action to add.
     *
     * @return this
     */
    ActionGroup add(ActionInterface action);

    /**
     * <p>
     *     Adds new actions to this group.
     * </p>
     *
     * @param actions actions to add.
     *
     * @return this
     */
    ActionGroup add(ActionInterface... actions);

    /**
     * <p>
     *     Adds new actions to this group.
     * </p>
     *
     * @param actions actions to add.
     *
     * @return this
     */
    ActionGroup add(Collection<ActionInterface> actions);

    /**
     * Sets a callback to run when the action group is interrupted.
     *
     * @return this
     */
    ActionGroup whenInterrupted(Runnable runnable);
}
