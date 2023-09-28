package com.flash3388.flashlib.scheduling.triggers;

import com.flash3388.flashlib.scheduling.ActionInterface;

/**
 * A trigger is an object which can be either active or inactive. It is possible
 * to link {@link ActionInterface Actions} to be done depending on the state of a trigger.
 *
 * @since FlashLib 1.2.0
 *
 * @see Triggers
 */
public interface Trigger {

    /**
     * <p>
     *     Registers an {@link ActionInterface} to run when this trigger is activated, i.e. when
     *     this action goes from {@link TriggerState#INACTIVE} to {@link TriggerState#ACTIVE}, if
     *     the action is not running, {@link ActionInterface#start()} is called.
     * </p>
     * 
     * @param action action to register
     */
    void whenActive(ActionInterface action);

    /**
     * <p>
     *     Registers an {@link ActionInterface} to be canceled when this trigger is activated, i.e. when
     *     this action goes from {@link TriggerState#INACTIVE} to {@link TriggerState#ACTIVE}, if
     *     the action is running, {@link ActionInterface#cancel()} is called.
     * </p>
     *
     * @param action action to register
     */    
    void cancelWhenActive(ActionInterface action);

    /**
     * <p>
     *     Registers an {@link ActionInterface} to be toggled when this trigger is activated, i.e. when
     *     this action goes from {@link TriggerState#INACTIVE} to {@link TriggerState#ACTIVE}, if
     *     the action is not running, {@link ActionInterface#start()} is called; if the action is running {@link ActionInterface#cancel()}
     *     is called.
     * </p>
     *
     * @param action action to register
     */
    void toggleWhenActive(ActionInterface action);

    /**
     * <p>
     *     Registers an {@link ActionInterface} to run while this trigger is active, i.e. when
     *     this action goes from {@link TriggerState#INACTIVE} to {@link TriggerState#ACTIVE}, if
     *     the action is not running, {@link ActionInterface#start()} is called. When the state goes back to {@link TriggerState#INACTIVE},
     *     {@link ActionInterface#cancel()} is called.
     * </p>
     *
     * @param action action to register
     */
    void whileActive(ActionInterface action);

    /**
     * <p>
     *     Registers an {@link ActionInterface} to run when this trigger is inactivated, i.e. when
     *     this action goes from {@link TriggerState#ACTIVE} to {@link TriggerState#INACTIVE}, if
     *     the action is not running, {@link ActionInterface#start()} is called.
     * </p>
     *
     * @param action action to register
     */
    void whenInactive(ActionInterface action);

    /**
     * <p>
     *     Registers an {@link ActionInterface} to be canceled when this trigger is inactivated, i.e. when
     *     this action goes from {@link TriggerState#ACTIVE} to {@link TriggerState#INACTIVE}, if
     *     the action is running, {@link ActionInterface#cancel()} is called.
     * </p>
     *
     * @param action action to register
     */
    void cancelWhenInactive(ActionInterface action);

    /**
     * <p>
     *     Registers an {@link ActionInterface} to be toggled when this trigger is inactivated, i.e. when
     *     this action goes from {@link TriggerState#ACTIVE} to {@link TriggerState#INACTIVE}, if
     *     the action is not running, {@link ActionInterface#start()} is called; if the action is running {@link ActionInterface#cancel()}
     *     is called.
     * </p>
     *
     * @param action action to register
     */
    void toggleWhenInactive(ActionInterface action);

    /**
     * <p>
     *     Registers an {@link ActionInterface} to run while this trigger is inactive, i.e. when
     *     this action goes from {@link TriggerState#ACTIVE} to {@link TriggerState#INACTIVE}, if
     *     the action is not running, {@link ActionInterface#start()} is called. When the state goes back to {@link TriggerState#ACTIVE},
     *     {@link ActionInterface#cancel()} is called.
     * </p>
     *
     * @param action action to register
     */
    void whileInactive(ActionInterface action);
}
