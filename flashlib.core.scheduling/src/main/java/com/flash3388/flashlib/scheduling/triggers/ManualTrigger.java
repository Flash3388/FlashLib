package com.flash3388.flashlib.scheduling.triggers;

public interface ManualTrigger extends Trigger {

    /**
     * Activates the trigger. If active, does nothing.
     */
    void activate();

    /**
     * Deactivates the trigger. If inactive, does nothing.
     */
    void deactivate();

    /**
     * Gets whether the trigger is marked as active.
     * @return <b>true</b> if active, <b>false</b> otherwise.
     */
    boolean isActive();
}
