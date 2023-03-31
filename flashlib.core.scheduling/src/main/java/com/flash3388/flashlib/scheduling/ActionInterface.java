package com.flash3388.flashlib.scheduling;

public interface ActionInterface {


    default void configure(ActionConfigurationEditor editor) {
    }

    /**
     * Called once when the action is started.
     */
    default void initialize(ActionControl control) {
    }

    /**
     * Called repeatedly during the execution of the action.
     */
    void execute(ActionControl control);

    /**
     * Called when the action ends run.
     */
    default void end(FinishReason reason) {
    }
}
