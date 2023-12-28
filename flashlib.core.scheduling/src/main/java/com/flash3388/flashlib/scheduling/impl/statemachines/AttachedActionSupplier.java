package com.flash3388.flashlib.scheduling.impl.statemachines;

import com.flash3388.flashlib.scheduling.Scheduler;
import com.flash3388.flashlib.scheduling.actions.Action;

import java.util.Collection;
import java.util.function.Function;
import java.util.function.Supplier;

public class AttachedActionSupplier implements Function<Scheduler, Action> {

    private final Supplier<Action> mAction;
    private final Collection<AttachmentTransition> mTransitions;

    public AttachedActionSupplier(Supplier<Action> action, Collection<AttachmentTransition> transitions) {
        mAction = action;
        mTransitions = transitions;
    }

    @Override
    public Action apply(Scheduler scheduler) {
        Action action = mAction.get();
        return new AttachedAction(scheduler, action, mTransitions);
    }
}
