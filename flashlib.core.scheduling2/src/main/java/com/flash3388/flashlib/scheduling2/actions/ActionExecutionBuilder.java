package com.flash3388.flashlib.scheduling2.actions;

import com.flash3388.flashlib.scheduling2.Requirement;
import com.flash3388.flashlib.time.Time;

import java.util.Arrays;
import java.util.Collection;

public interface ActionExecutionBuilder {

    ActionExecutionBuilder name(String name);

    ActionExecutionBuilder requires(Collection<? extends Requirement> requirements);
    default ActionExecutionBuilder requires(Requirement... requirements) {
        return requires(Arrays.asList(requirements));
    }

    ActionExecutionBuilder withTimeout(Time timeout);

    ActionExecutionBuilder shouldRunInDisabled(boolean run);

    Status start();
}
