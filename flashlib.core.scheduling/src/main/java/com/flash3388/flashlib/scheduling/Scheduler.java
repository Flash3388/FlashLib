package com.flash3388.flashlib.scheduling;

import com.flash3388.flashlib.annotations.MainThreadOnly;
import com.flash3388.flashlib.scheduling.actions.Action;
import com.flash3388.flashlib.scheduling.actions.ActionFlag;
import com.flash3388.flashlib.scheduling.actions.ActionGroup;
import com.flash3388.flashlib.scheduling.triggers.ManualTrigger;
import com.flash3388.flashlib.scheduling.triggers.Trigger;

import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;

/**
 * Scheduler is the executor for FlashLib's scheduling component. It is responsible for executing all
 * running actions (as described by {@link Action}) and managing their requirements.
 * <p>
 *     Each action is described by an instance {@link Action}. Different instances of the same class
 *     are seen as different {@link Action Actions}.
 * </p>
 * <p>
 *     Actions have {@link Requirement} being associated to them. These <em>dependencies</em> can be used
 *     to ensure that no two actions use the same requirement at the same time.
 *     Only one executing action may be associated with each one declared {@link Requirement}. One action
 *     may have multiple associated {@link Requirement Requirements} to it. It is no necessary to register
 *     {@link Requirement Requirements} with the scheduler as they are discovered when starting actions.
 * </p>
 * <p>
 *     A <em>requirement conflict</em> is a situation wherein two different actions are requesting to run while sharing at
 *     least one requirement. Such situations may occur when a new action is asked to run while an already running action
 *     already has acquired at least one of the requirements requested by the new action, or perhaps when two actions
 *     transition to an execution state while sharing some requirements. It is acceptable for users to intentionally cause
 *     a conflict as a way to change the execution state of actions.
 *
 *     Whatever is the case, conflicts are handled as such:
 *     <ul>
 *         <li>
 *             Normally, priority is given to the latest starting action. That is, to the action that has transitioned
 *             to execution state last. In such a case, actions conflicting with the new action will be cancelled, their
 *             requirements released and transferred to the new action.
 *         </li>
 *         <li>
 *             When a running action is marked with {@link ActionFlag#PREFERRED_FOR_REQUIREMENTS} and a new conflicting
 *             action is scheduled to execute, the new action will not be allowed to start until the running action
 *             has finished execution, effectively giving the running action priority.
 *             The new action can either be placed in a pending mode, or rejected entirely. Such behaviour is implementation-dependent.
 *         </li>
 *     </ul>
 * </p>
 * <p>
 *     Actions have different states in the scheduler:
 *     <ul>
 *         <li>
 *             Pending actions are actions requested to execute, but haven't started execution yet.
 *             This is evident by the action methods not having been called.
 *             Such a state is usually the result of a current execution state that doesn't permit
 *             starting a new action, an implementation preference to delay execution, or
 *             due to a conflict with another actions marked by {@link ActionFlag#PREFERRED_FOR_REQUIREMENTS}.
 *             Actions may be cancelled or terminated during their pending state, meaning that they never got to execute
 *             and no user code ran.
 *         </li>
 *         <li>
 *             Executing actions are actions whose code is being run by the scheduler, such that user code is called.
 *             Only running actions really hold onto requirements, so only when actions start their execution do they
 *             acquire their associated requirements. It is guaranteed that {@link Action#end(FinishReason)} will
 *             be called for any running action finished or having been cancelled.
 *         </li>
 *         <li>
 *             Not running actions are actions which are not stored in the scheduler as either pending or executing.
 *             Such is the case for actions which either finished execution or have not started execution yet.
 *         </li>
 *     </ul>
 * </p>
 * <p>
 *     There are several ways by which an action may end its execution. These are divided into two categories:
 *     <em>finish</em> and <em>interrupt</em>.
 *     <ul>
 *         <li>
 *             <em>Finish</em> end refers to situations where the action stops by the request of the action itself,
 *             via the invocation of {@link ActionControl#finish()}. These are considered the most graceful
 *             end situations, as these occur by the request of the action and thus are likely due to the action
 *             actually finishing its intended job.
 *         </li>
 *         <li>
 *             <em>Interrupt</em> end refers to situations where the actions stops at the request of an outside
 *             user, or the scheduler itself due to certain conditions. These are further divided into
 *             <ul>
 *                 <li>
 *                     <em>user cancel</em> caused by a direct user call to {@link #cancel(Action)} or {@link Action#cancel()},
 *                     or via the action invoking {@link ActionControl#cancel()}. Leads to {@link FinishReason#CANCELED}.
 *                 </li>
 *                 <li>
 *                     <em>conflict interrupt</em>, caused when the scheduler detects a conflicts which was determined to
 *                     be handled by cancelling one of the actions. Leads to {@link FinishReason#CANCELED}.
 *                 </li>
 *                 <li>
 *                     <em>action timeout</em>, caused when actions configured with a timeout reach their defined maximum
 *                     run time. Leads to {@link FinishReason#TIMEDOUT}.
 *                 </li>
 *                 <li>
 *                     <em>action error</em>, caused when the actions throws an exception for any reason.
 *                     Leads to {@link FinishReason#ERRORED}.
 *                 </li>
 *             </ul>
 *         </li>
 *     </ul>
 * </p>
 * <p>
 *     Default actions are actions associated directly with a {@link Subsystem}. Such actions are special, as in
 *     that they are automatically executed whenever no action has acquired the associated {@link Subsystem}.
 *     {@link Subsystem} is simple a special-case {@link Requirement}.
 *     Default actions will not forcibly attempt to acquire their associated requirements, and will await
 *     until it is released by other actions.
 *     The behaviour of such actions which request requirements other then the associated subsystem is
 *     not defined at the moment, and should be avoided.
 * </p>
 * <p>
 *     {@link ActionGroup ActionGroups} are a combination of multiple different actions under a single action.
 *     Depending on the specific group, actions may be executed in specific orders and combinations. One example of
 *     usage is using it to run a set of actions is sequence. Although each group has its own combination and
 *     order, all actions in a group will be executed in a manner consistent with general execution of actions.
 *     This includes the different action state. Requirements may be managed differently however, as groups, even
 *     if not immediately require it, may request to <em>acquire</em> the requirements of all actions attached to them.
 *     As such, combining the execution of a group, alongside other actions, while sharing requirements, is not possible.
 *     This is generally caused due to the scheduler observing the group as a single executed action.
 * </p>
 * <p>
 *     {@link Trigger Triggers} are condition-based activators of {@link Action Actions}. One may be created with
 *     {@link #newTrigger(BooleanSupplier)} or {@link #newManualTrigger()}. Once holding a trigger, actions
 *     may be attached to it to start or stop at specific situation defined by the associated state of the trigger.
 *     Generally, trigger can either be active, or inactive, and as such, actions may be scheduled to run
 *     when the trigger transitions to active (e.g. {@link Trigger#whenActive(Action)} and more.
 * </p>
 * <p>
 *     The scheduler recognized a general run state in {@link SchedulerMode}. This allows defining runtime situations
 *     to the scheduler, and thus affecting the execution of actions. Schedulers must be supplied with this mode
 *     during calls to {@link #run(SchedulerMode)}. If this mode has changed after a call to {@link #run(SchedulerMode)},
 *     a subsequent call to {@link #run(SchedulerMode)} is required to update it.
 *
 *     The mode has the following affects:
 *     <ul>
 *         <li>
 *             If the mode defines {@link SchedulerMode#isDisabled()} as <b>true</b>
 *             any actions not defined with {@link ActionFlag#RUN_ON_DISABLED} will be <em>cancelled</em> with
 *             {@link FinishReason#CANCELED}.
 *         </li>
 *     </ul>
 * </p>
 * <p>
 *     Actions may be handled from withing other actions. This includes pretty much any call to the scheduler. e.g.
 *     an action may start another action from within it's code.
 *     <pre>
 *          {@literal @}Override
 *          public void execute() {
 *              otherAction.start();
 *          }
 *     </pre>
 *
 *     However, in such a case, <em>otherAction</em> will most likely not start immediately, although that is entirely
 *     implementation dependent; but generally, there is no guarantee as to the execution of <em>otherAction</em> in relation
 *     to the action starting it, and as such, one should not rely on <em>otherAction</em> starting (or stopping, depending on the
 *     specific call) by the next call to <em>execute</em> or even in the lifecycle of the calling action.
 * </p>
 *
 * @since FlashLib 1.0.0
 */
public interface Scheduler {

    /**
     * <p>
     *     Starts running an {@link Action}. Generally, this
     *     should be called from {@link Action#start()} implementations
     *     and not directly.
     * </p>
     * <p>
     *     Adds the new instance to execution by the scheduler. Whether the action
     *     starts execution immediately depends on the implementation of the scheduler.
     *     But generally, this is influenced by the execution state of the scheduler and
     *     conflicting actions.
     * </p>
     * <p>
     *     This initiates a conflicts check with other running actions that may share
     *     the same <em>requirements</em> as this new action. If any running actions
     *     share at least one requirement with the new action, then conflict resolution
     *     is attempted.
     *     <ul>
     *         <li>
     *             If one of the conflicting running action is marked with {@link ActionFlag#PREFERRED_FOR_REQUIREMENTS},
     *             then the new action either becomes pending, waiting for the <em>preferred</em> action to finish
     *             execution, or the new action is rejected completely and won't be scheduled for execution.
     *         </li>
     *         <li>
     *             Otherwise, the conflicting running actions are cancelled and subsequently removed from execution,
     *             after which the new action starts execution.
     *         </li>
     *     </ul>
     * </p>
     *
     * @param action action to start
     *
     * @throws IllegalStateException if the action is already running on this scheduler.
     */
    @MainThreadOnly
    void start(Action action);

    /**
     * <p>
     *     Cancels an {@link Action} being ran by the scheduler. Generally,
     *     this should be called from {@link Action#cancel()} implementations
     *     and not directly.
     * </p>
     * <p>
     *     Requests the scheduler to cancel the instance's execution.
     *     It is not guaranteed that the action will stop immediately, as it depends
     *     both on the implementation of the scheduler and the current execution state.
     * </p>
     * <p>
     *     If the cancelled action is pending, it will be removed from the pending queue. However,
     *     because it did not start execution, it will not be alerted as to this removal.
     * </p>
     * <p>
     *     If the cancelled action is currently executing and {@link Action#initialize(ActionControl)} was called,
     *     it will receive a chance to exit gracefully, with its {@link Action#end(FinishReason)} invoked
     *     with {@link FinishReason#CANCELED}. Though, regardless of what the action does during that, it will be removed from
     *     execution.
     * </p>
     * <p>
     *     Any <em>requirements</em> held by this action will be marked as unused.
     * </p>
     * <p>
     *     If the action is marked with the flag {@link ActionFlag#PREFERRED_FOR_REQUIREMENTS}, and
     *     there are actions pending to execute with <em>requirements</em> shared with the cancelled actions
     *     and no other conflicting action has {@link ActionFlag#PREFERRED_FOR_REQUIREMENTS}, then
     *     the pending action will start its execution as described by {@link #start(Action)}.
     * </p>
     *
     * @param action action to cancel.
     *
     * @throws IllegalStateException if the action is not running on the scheduler.
     */
    @MainThreadOnly
    void cancel(Action action);

    /**
     * <p>
     *     Questions the scheduler as to the running state of a given action.
     * </p>
     *
     * @param action action to test
     *
     * @return <b>true</b> if currently execution or pending execution, <b>false</b> otherwise.
     * @see #getExecutionStateOf(Action)
     */
    @MainThreadOnly
    boolean isRunning(Action action);

    /**
     * <p>
     *     Gets the current execution state for a given action. The returned
     *     information is a snapshot of the current state and becomes stale after
     *     call.
     * </p>
     * <p>
     *     If the action is neither currently executing nor is it pending, meaning that
     *     it has never started execution, or has ended it's execution then
     *     {@link ExecutionState#isPending()} and {@link ExecutionState#isPending()} will
     *     both return <b>false</b>.
     * </p>
     *
     * @param action action to get state of
     * @return state of the execution of an action.
     */
    @MainThreadOnly
    ExecutionState getExecutionStateOf(Action action);

    /**
     * <p>
     *     Cancels all actions running on this scheduler if they match the
     *     given predicate as described by {@link Predicate#test(Object)} of
     *     that predicate.
     * </p>
     * <p>
     *     Behaviour of cancelled actions is as described in {@link #cancel(Action)}.
     * </p>
     *
     * @param predicate {@link Predicate} determining whether to cancel an action.
     * @see #cancel(Action)
     */
    @MainThreadOnly
    void cancelActionsIf(Predicate<? super Action> predicate);

    /**
     * <p>
     *     Cancels all actions running on this scheduler if they do not have the given
     *     flag configured.
     * </p>
     * <p>
     *     Behaviour of cancelled actions is as described in {@link #cancel(Action)}.
     * </p>
     *
     * @param flag flag, which, if missing from an action, will cause the action to be cancelled.
     * @see #cancel(Action)
     */
    @MainThreadOnly
    void cancelActionsIfWithoutFlag(ActionFlag flag);

    /**
     * <p>
     *     Cancels all actions running on this scheduler.
     * </p>
     * <p>
     *     Behaviour of cancelled actions is as described in {@link #cancel(Action)}.
     * </p>
     * @see #cancel(Action)
     */
    @MainThreadOnly
    void cancelAllActions();

    /**
     * <p>
     *     Sets the default {@link Action} for a given {@link Subsystem}.
     *     The given action will be started automatically when the subsystem in question
     *     has no other action running.
     * </p>
     * <p>
     *     There can only be one default action for any subsystem, thus calling this method
     *     twice on the same subsystem will overwrite the previously set default action. In addition
     *     if the previous default action is running, it will be canceled.
     * </p>
     * <p>
     *     The given subsystem must be part of the requirements of the given action.
     * </p>
     *
     * @param subsystem subsystem to set default action for.
     * @param action action to use as default
     *
     * @throws IllegalArgumentException if the given action does not require the given
     *      subsystem.
     */
    @MainThreadOnly
    void setDefaultAction(Subsystem subsystem, Action action);

    /**
     * <p>
     *     Gets the action currently running on the given {@link Requirement requirement (or subsystem)}.
     * </p>
     *
     * @param requirement the requirement.
     *
     * @return {@link Optional} containing the action, if there is one running, or {@link Optional#empty()}
     *  if there is no action running.
     */
    @MainThreadOnly
    Optional<Action> getActionRunningOnRequirement(Requirement requirement);

    /**
     * <p>
     *     Runs an update on the scheduler, updating the current mode used by the scheduler
     *     as well as updating internal execution information.
     * </p>
     * <p>
     *     Different implementations of the scheduler may use this method more
     *     or less often depending on the execution model of the implementation.
     *     It is recommended to invoke this method as fast as possible (~ 20ms).
     * </p>
     *
     * @param mode current mode for the scheduler.
     */
    @MainThreadOnly
    void run(SchedulerMode mode);

    /**
     * <p>
     *     Creates a new trigger for registering action activation to a condition. The supplied
     *     condition determines the <em>active</em> state of the trigger, and is checked automatically.
     *     Attached actions are started and stopped as defined by their attachment and the condition.
     * </p>
     * <p>
     *     The frequency of checks of the given condition is implementation dependent, and as such
     *     delays may exist between the change of the condition, and the the trigger's state being changed.
     * </p>
     * <p>
     *     The trigger's initial state is inactive. If the condition is <b>true</b> at creation time,
     *     the trigger will only be updated at it's next check.
     * </p>
     * <p>
     *     Actions attached to this trigger will be started or cancelled in a manner defined by {@link #start(Action)} and
     *     {@link #cancel(Action)}.
     * </p>
     *
     * @param condition when <b>true</b> marks the trigger as <em>active</em>,
     *                  when <b>false</b> marks the trigger as <em>inactive</em>.
     * @return the trigger
     */
    @MainThreadOnly
    Trigger newTrigger(BooleanSupplier condition);

    /**
     * <p>
     *     Creates a new trigger which can be activated or deactivated manually. The active/inactive state
     *     of the trigger is controller by user calls to {@link ManualTrigger#activate()} and {@link ManualTrigger#deactivate()}.
     *     Associated actions are affected accordingly.
     * </p>
     * <p>
     *     The trigger's initial state is inactive.
     * </p>
     * <p>
     *     Actions attached to this trigger will be started or cancelled in a manner defined by {@link #start(Action)} and
     *     {@link #cancel(Action)}.
     * </p>
     *
     * @return {@link ManualTrigger}
     */
    @MainThreadOnly
    ManualTrigger newManualTrigger();

    /**
     * <p>
     *     Creates a new group for executing a set of actions in a specific order and combination.
     *     The exact manner of execution is dependent on the behaviour of the group, but generally at least conforms
     *     to the basic execution manner of normal actions.
     * </p>
     * <p>
     *     When executing actions as part of a group, the scheduler may only recognize the group as the action
     *     being executed.
     * </p>
     *
     * @param type type of group to create. Influences the execution order and flow.
     * @return action group.
     */
    @MainThreadOnly
    ActionGroup newActionGroup(ActionGroupType type);
}