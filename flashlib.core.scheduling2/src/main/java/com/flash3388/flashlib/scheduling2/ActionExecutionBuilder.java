package com.flash3388.flashlib.scheduling2;

import com.flash3388.flashlib.time.Time;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public interface ActionExecutionBuilder {

    ActionExecutionBuilder name(String name);
    ActionExecutionBuilder timeout(Time timeout);

    ActionExecutionBuilder requires(Collection<? extends Requirement> requirements);
    default ActionExecutionBuilder requires(Requirement... requirements) {
        return requires(Arrays.asList(requirements));
    }

    ActionExecutionBuilder flags(Set<ActionFlag> flags);
    default ActionExecutionBuilder flags(ActionFlag... flags) {
        return flags(new HashSet<>(Arrays.asList(flags)));
    }

    Status start();
}
