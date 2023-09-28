package com.flash3388.flashlib.scheduling.impl;

import com.flash3388.flashlib.scheduling.Action;
import com.flash3388.flashlib.scheduling.ActionBuilder;
import com.flash3388.flashlib.scheduling.ActionConfiguration;
import com.flash3388.flashlib.scheduling.ActionFlag;
import com.flash3388.flashlib.scheduling.ActionInterface;
import com.flash3388.flashlib.scheduling.Requirement;
import com.flash3388.flashlib.time.Time;

public class ActionBuilderImpl implements ActionBuilder {

    private final ActionInterface mAction;
    private final ActionConfiguration.Editor mConfigurationEditor;

    public ActionBuilderImpl(ActionInterface action, ActionConfiguration configuration) {
        mAction = action;
        mConfigurationEditor = new ActionConfiguration.Editor(configuration);
    }

    @Override
    public ActionBuilder requires(Requirement... requirements) {
        mConfigurationEditor.addRequirements(requirements);
        return this;
    }

    @Override
    public ActionBuilder withTimeout(Time timeout) {
        mConfigurationEditor.setTimeout(timeout);
        return this;
    }

    @Override
    public ActionBuilder withName(String name) {
        mConfigurationEditor.setName(name);
        return this;
    }

    @Override
    public ActionBuilder withFlags(ActionFlag... flags) {
        mConfigurationEditor.addFlags(flags);
        return this;
    }

    @Override
    public Action build() {
        ActionConfiguration configuration = mConfigurationEditor.save();
        // TODO: BUILD
        return null;
    }
}
