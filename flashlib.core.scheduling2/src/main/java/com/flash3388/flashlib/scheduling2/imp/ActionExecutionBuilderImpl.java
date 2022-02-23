package com.flash3388.flashlib.scheduling2.imp;

import com.flash3388.flashlib.scheduling2.Action;
import com.flash3388.flashlib.scheduling2.ActionExecutionBuilder;
import com.flash3388.flashlib.scheduling2.ActionFlag;
import com.flash3388.flashlib.scheduling2.Configuration;
import com.flash3388.flashlib.scheduling2.Requirement;
import com.flash3388.flashlib.scheduling2.Status;
import com.flash3388.flashlib.time.Time;

import java.util.Collection;
import java.util.Set;
import java.util.function.BiFunction;

public class ActionExecutionBuilderImpl implements ActionExecutionBuilder {

    private final Action mAction;
    private final Configuration mConfiguration;
    private final BiFunction<Action, Configuration, Status> mStart;

    public ActionExecutionBuilderImpl(Action action, Configuration configuration,
                                      BiFunction<Action, Configuration, Status> start) {
        mAction = action;
        mConfiguration = configuration;
        mStart = start;
    }

    @Override
    public ActionExecutionBuilder name(String name) {
        mConfiguration.setName(name);
        return this;
    }

    @Override
    public ActionExecutionBuilder timeout(Time timeout) {
        mConfiguration.setTimeout(timeout);
        return this;
    }

    @Override
    public ActionExecutionBuilder requires(Collection<? extends Requirement> requirements) {
        mConfiguration.requires(requirements);
        return this;
    }

    @Override
    public ActionExecutionBuilder flags(Set<ActionFlag> flags) {
        mConfiguration.addFlags(flags);
        return this;
    }

    @Override
    public Status start() {
        return mStart.apply(mAction, mConfiguration);
    }
}
