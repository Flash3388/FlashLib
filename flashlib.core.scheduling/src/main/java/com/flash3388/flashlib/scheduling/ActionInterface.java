package com.flash3388.flashlib.scheduling;

/**
 * An Action is something that can be executed on the robot. This can include any operation on the robot,
 * depending on what users want. When started, the action is added to the {@link Scheduler} which is responsible
 * for executing the action. To start an action, call {@link #start()}. This class is abstract.
 * <p>
 * An action has few running phases:
 * <ul>
 * 	<li> {@link #initialize(ActionControl)} called once at the start </li>
 * 	<li> {@link #execute(ActionControl)}: called repeatedly until
 * 	{@link ActionControl#finish()} or {@link ActionControl#cancel()} are called</li>
 * 	<li> {@link #end(FinishReason)}: called when the action is done</li>
 * </ul>
 * To indicate whether an action should stop running, users can use {@link ActionControl} and
 * invoke its various methods to stop the action. To manually stop an action, {@link #cancel()} can be called.
 * <p>
 * An action can have a timeout. If the time since the action started running has passed a given timeout, the
 * action is canceled, invoking a call to {@link #end(FinishReason) end(FinishReason.TIMEDOUT)}.
 * Set the timeout by calling {@link #configure()}.
 * <p>
 * It is possible to define dependencies for scheduling. Basically, if an action is using a {@link Subsystem} object
 * of our robot, it is necessary to insure that no other action will use the same object, so that it won't confuse
 * that system. To do that, users must explicitly call {@link #configure()} and pass a system which is
 * used (multiple systems can be required). By doing so, the {@link Scheduler} now knows to stop any other action
 * with at least one similar system requirement. If an action is running and another starts with a similar system
 * requirement, the previous action is canceled, invoking a call to {@link #end(FinishReason) end(FinishReason.CANCELLED)}.
 *
 * @since FlashLib 1.0.0
 */
public interface ActionInterface {

    void configure(ActionConfigurer configurer);

    /**
     * Called once when the action is started.
     * @param control component for controlling and querying the action execution state.
     */
    void initialize(ActionControl control);

    /**
     * Called repeatedly during the execution of the action.
     * @param control component for controlling and querying the action execution state.
     */
    void execute(ActionControl control);

    /**
     * Called when the action ends run.
     * @param reason reason for execution finish
     */
    void end(FinishReason reason);
}