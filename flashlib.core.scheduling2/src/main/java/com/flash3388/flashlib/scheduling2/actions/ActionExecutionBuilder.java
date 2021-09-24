package com.flash3388.flashlib.scheduling2.actions;

import com.flash3388.flashlib.scheduling2.Requirement;
import com.flash3388.flashlib.time.Time;

import java.util.Arrays;
import java.util.Collection;

public interface ActionExecutionBuilder<R> {

    ActionExecutionBuilder<R> name(String name);

    ActionExecutionBuilder<R> requires(Collection<? extends Requirement> requirements);
    default ActionExecutionBuilder<R> requires(Requirement... requirements) {
        return requires(Arrays.asList(requirements));
    }

    ActionExecutionBuilder<R> withTimeout(Time timeout);

    ActionExecutionBuilder<R> shouldRunInDisabled(boolean run);

    Status<R> start();
}
