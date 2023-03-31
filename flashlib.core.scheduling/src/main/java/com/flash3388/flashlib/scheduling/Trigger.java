package com.flash3388.flashlib.scheduling;

/**
 * A trigger is an object which can be either active or inactive. It is possible
 * to link {@link ActionInterface ActionInterfaces} to be done depending on the state of a trigger.
 *
 * @since FlashLib 1.2.0
 *
 * @see Triggers
 */
public interface Trigger {

    /**
     * <p>
     *     Registers an {@link ActionInterface} to run when this trigger is activated, i.e. when
     *     this trigger goes from from inactive to active, if the action is not running, it is started.
     * </p>
     * 
     * @param action action to register
     */
    void whenActive(ActionInterface action);

    /**
     * <p>
     *     Registers an {@link ActionInterface} to be canceled when this trigger is activated, i.e. when
     *     this action goes from inactive to active, if the action is running, it is cancelled.
     * </p>
     *
     * @param action action to register
     */    
    void cancelWhenActive(ActionInterface action);

    /**
     * <p>
     *     Registers an {@link ActionInterface} to be toggled when this trigger is activated, i.e. when
     *     this action goes from inactive to active, if the action is not running, it started; 
     *     if the action is running it is cancelled.
     * </p>
     *
     * @param action action to register
     */
    void toggleWhenActive(ActionInterface action);

    /**
     * <p>
     *     Registers an {@link ActionInterface} to run while this trigger is active, i.e. when
     *     this action goes from inactive to active, if the action is not running, it is started. 
     *     When the state goes back to inactive it is the action is canclled.
     * </p>
     *
     * @param action action to register
     */
    void whileActive(ActionInterface action);

    /**
     * <p>
     *     Registers an {@link ActionInterface} to run when this trigger is inactivated, i.e. when
     *     this action goes from active to inactive, if the action is not running, it is started.
     * </p>
     *
     * @param action action to register
     */
    void whenInactive(ActionInterface action);

    /**
     * <p>
     *     Registers an {@link ActionInterface} to be canceled when this trigger is inactivated, i.e. when
     *     this action goes from active to inactive, if
     *     the action is running, the action is cancelled.
     * </p>
     *
     * @param action action to register
     */
    void cancelWhenInactive(ActionInterface action);

    /**
     * <p>
     *     Registers an {@link ActionInterface} to be toggled when this trigger is inactivated, i.e. when
     *     this action goes from active to inactive, if
     *     the action is not running, the action is started; if the action is running the action is cancelled.
     * </p>
     *
     * @param action action to register
     */
    void toggleWhenInactive(ActionInterface action);

    /**
     * <p>
     *     Registers an {@link ActionInterface} to run while this trigger is inactive, i.e. when
     *     this action goes from active to inactive, if
     *     the action is not running, the action is started. When the state goes back to active,
     *     the action is cancelled.
     * </p>
     *
     * @param action action to register
     */
    void whileInactive(ActionInterface action);
}
