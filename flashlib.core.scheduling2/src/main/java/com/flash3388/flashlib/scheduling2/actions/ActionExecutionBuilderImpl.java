package com.flash3388.flashlib.scheduling2.actions;

import com.flash3388.flashlib.scheduling2.ActionsControl;
import com.flash3388.flashlib.scheduling2.Requirement;
import com.flash3388.flashlib.time.Time;

import java.util.Collection;

public class ActionExecutionBuilderImpl<R> implements ActionExecutionBuilder<R> {

    private final ActionsControl mActionsControl;
    private final Action<R> mAction;

    private final Configuration mConfiguration;

    public ActionExecutionBuilderImpl(ActionsControl actionsControl, Action<R> action) {
        mActionsControl = actionsControl;
        mAction = action;

        mConfiguration = new ConfigurationImpl();
    }

    @Override
    public ActionExecutionBuilder<R> name(String name) {
        mConfiguration.setName(name);
        return this;
    }

    @Override
    public ActionExecutionBuilder<R> requires(Collection<? extends Requirement> requirements) {
        mConfiguration.requires(requirements);
        return this;
    }

    @Override
    public ActionExecutionBuilder<R> withTimeout(Time timeout) {
        mConfiguration.setTimeout(timeout);
        return this;
    }

    @Override
    public ActionExecutionBuilder<R> shouldRunInDisabled(boolean run) {
        mConfiguration.setRunWhenDisabled(run);
        return this;
    }

    @Override
    public Status<R> start() {
        return mActionsControl.addActionPending(mAction, mConfiguration);
    }
}
