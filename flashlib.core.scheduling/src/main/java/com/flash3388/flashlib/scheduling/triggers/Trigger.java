package com.flash3388.flashlib.scheduling.triggers;

import com.flash3388.flashlib.scheduling.actions.Action;

public interface Trigger {

    /**
     * <p>
     *     Registers an {@link Action} to run when this trigger is activated, i.e. when
     *     this action goes from {@link TriggerState#INACTIVE} to {@link TriggerState#ACTIVE}, if
     *     the action is not running, {@link Action#start()} is called.
     * </p>
     * 
     * @param action action to register
     */
    void whenActive(Action action);

    /**
     * <p>
     *     Registers an {@link Action} to be canceled when this trigger is activated, i.e. when
     *     this action goes from {@link TriggerState#INACTIVE} to {@link TriggerState#ACTIVE}, if
     *     the action is running, {@link Action#cancel()} is called.
     * </p>
     *
     * @param action action to register
     */    
    void cancelWhenActive(Action action);

    /**
     * <p>
     *     Registers an {@link Action} to be toggled when this trigger is activated, i.e. when
     *     this action goes from {@link TriggerState#INACTIVE} to {@link TriggerState#ACTIVE}, if
     *     the action is not running, {@link Action#start()} is called; if the action is running {@link Action#cancel()}
     *     is called.
     * </p>
     *
     * @param action action to register
     */
    void toggleWhenActive(Action action);

    /**
     * <p>
     *     Registers an {@link Action} to run while this trigger is active, i.e. when
     *     this action goes from {@link TriggerState#INACTIVE} to {@link TriggerState#ACTIVE}, if
     *     the action is not running, {@link Action#start()} is called. When the state goes back to {@link TriggerState#INACTIVE},
     *     {@link Action#cancel()} is called.
     * </p>
     *
     * @param action action to register
     */
    void whileActive(Action action);

    /**
     * <p>
     *     Registers an {@link Action} to run when this trigger is inactivated, i.e. when
     *     this action goes from {@link TriggerState#ACTIVE} to {@link TriggerState#INACTIVE}, if
     *     the action is not running, {@link Action#start()} is called.
     * </p>
     *
     * @param action action to register
     */
    void whenInactive(Action action);

    /**
     * <p>
     *     Registers an {@link Action} to be canceled when this trigger is inactivated, i.e. when
     *     this action goes from {@link TriggerState#ACTIVE} to {@link TriggerState#INACTIVE}, if
     *     the action is running, {@link Action#cancel()} is called.
     * </p>
     *
     * @param action action to register
     */
    void cancelWhenInactive(Action action);

    /**
     * <p>
     *     Registers an {@link Action} to be toggled when this trigger is inactivated, i.e. when
     *     this action goes from {@link TriggerState#ACTIVE} to {@link TriggerState#INACTIVE}, if
     *     the action is not running, {@link Action#start()} is called; if the action is running {@link Action#cancel()}
     *     is called.
     * </p>
     *
     * @param action action to register
     */
    void toggleWhenInactive(Action action);

    /**
     * <p>
     *     Registers an {@link Action} to run while this trigger is inactive, i.e. when
     *     this action goes from {@link TriggerState#ACTIVE} to {@link TriggerState#INACTIVE}, if
     *     the action is not running, {@link Action#start()} is called. When the state goes back to {@link TriggerState#ACTIVE},
     *     {@link Action#cancel()} is called.
     * </p>
     *
     * @param action action to register
     */
    void whileInactive(Action action);
}
