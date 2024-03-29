package com.flash3388.flashlib.scheduling.actions;

import com.flash3388.flashlib.scheduling.SchedulerMode;

public enum ActionFlag {
    /**
     * When set, allows actions to run while the in {@link SchedulerMode#isDisabled() DISABLED mode},
     * which is not normally allowed.
     */
    RUN_ON_DISABLED,
    /**
     * When set, the scheduler considers the action to be preferred for running and thus
     * does not allow interrupting it as a result of a requirements conflict.
     * For this action to stop, it must finish naturally, via {@link Action#cancel()},
     * or when in {@link SchedulerMode#isDisabled() DISABLED mode}.
     */
    PREFERRED_FOR_REQUIREMENTS
}
