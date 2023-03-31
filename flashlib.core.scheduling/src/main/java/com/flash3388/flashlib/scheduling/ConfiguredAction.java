package com.flash3388.flashlib.scheduling;

public interface ConfiguredAction {

    /**
     * <p>
     *     Gets the configuration of this action.
     * </p>
     *
     * @return {@link ActionConfiguration} for this action.
     */
    ActionConfiguration getConfiguration();

    /**
     * <p>
     *     Sets the configuration of this action.
     * </p>
     * <p>
     *      Configuration cannot be modified while the action is running.
     * </p>
     *
     * @param configuration {@link ActionConfiguration} to set.
     *
     * @throws IllegalStateException if the action is running.
     */
    void setConfiguration(ActionConfiguration configuration);

    /**
     * <p>
     *     Starts the action.
     * </p>
     *
     * @throws IllegalStateException if the action is already running.
     */
    void start();

    /**
     * <p>
     *     Cancels the action.
     * </p>
     *
     * @throws IllegalStateException if the action is not running.
     */
    void cancel();

    /**
     * <p>
     *     Gets whether or not the action is running.
     * </p>
     *
     * @return <b>true</b> if the action is running, <b>false</b> otherwise.
     */
    boolean isRunning();

    /**
     * <p>
     *     Groups this actions with the given actions to run
     *     in a sequential order, such that this action runs first,
     *     and the given actions run in order of the given arguments.
     * </p>
     *
     * @param actions actions to group with this one.
     *
     * @return {@link ActionGroup} running in sequence this and the given actions.
     */
    ActionGroup andThen(ActionInterface... actions);

    /**
     * <p>
     *     Groups this actions with the given actions to run
     *     in a parallel order, such that this actions runs in parallel
     *     of the given arguments.
     * </p>
     *
     * @param actions actions to group with this one.
     *
     * @return {@link ActionGroup} running in parallel this and the given actions.
     */
    ActionGroup alongWith(ActionInterface... actions);

    /**
     * <p>
     *     Groups this actions with the given actions to run
     *     in a parallel order, such that this actions runs in parallel
     *     of the given arguments.
     * </p>
     * <p>
     *     Unlike normal parallel groups, <code>race</code> groups will
     *     stop when the first action in group stops, rather then waiting for
     *     all of the to finish.
     * </p>
     *
     * @param actions actions to group with this one.
     *
     * @return {@link ActionGroup} running in parallel this and the given actions.
     */
    ActionGroup raceWith(ActionInterface... actions);
}
