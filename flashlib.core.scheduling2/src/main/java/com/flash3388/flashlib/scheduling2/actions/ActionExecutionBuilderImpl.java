package com.flash3388.flashlib.scheduling2.actions;

import com.flash3388.flashlib.scheduling2.ActionsControl;
import com.flash3388.flashlib.scheduling2.Requirement;
import com.flash3388.flashlib.time.Time;

import java.util.Collection;

public class ActionExecutionBuilderImpl implements ActionExecutionBuilder {

    private final ActionsControl mActionsControl;
    private final Action mAction;

    private final Configuration mConfiguration;

    public ActionExecutionBuilderImpl(ActionsControl actionsControl, Action action) {
        mActionsControl = actionsControl;
        mAction = action;

        mConfiguration = new ConfigurationImpl();
    }

    @Override
    public ActionExecutionBuilder name(String name) {
        mConfiguration.setName(name);
        return this;
    }

    @Override
    public ActionExecutionBuilder requires(Collection<? extends Requirement> requirements) {
        mConfiguration.requires(requirements);
        return this;
    }

    @Override
    public ActionExecutionBuilder withTimeout(Time timeout) {
        mConfiguration.setTimeout(timeout);
        return this;
    }

    @Override
    public ActionExecutionBuilder shouldRunInDisabled(boolean run) {
        mConfiguration.setRunWhenDisabled(run);
        return this;
    }

    @Override
    public Status start() {
        return mActionsControl.addActionPending(mAction, mConfiguration);
    }
}
