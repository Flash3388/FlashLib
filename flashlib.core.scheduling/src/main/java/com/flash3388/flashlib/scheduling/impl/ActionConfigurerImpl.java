package com.flash3388.flashlib.scheduling.impl;

import com.flash3388.flashlib.scheduling.ActionConfiguration;
import com.flash3388.flashlib.scheduling.ActionConfigurer;
import com.flash3388.flashlib.scheduling.ActionFlag;
import com.flash3388.flashlib.scheduling.Requirement;
import com.flash3388.flashlib.time.Time;

public class ActionConfigurerImpl implements ActionConfigurer {

    private final ActionConfiguration.Editor mConfigurationEditor;

    public ActionConfigurerImpl(ActionConfiguration configuration) {
        mConfigurationEditor = new ActionConfiguration.Editor(configuration);
    }

    @Override
    public ActionConfigurer requires(Requirement... requirements) {
        mConfigurationEditor.addRequirements(requirements);
        return this;
    }

    @Override
    public ActionConfigurer setTimeout(Time timeout) {
        mConfigurationEditor.setTimeout(timeout);
        return this;
    }

    @Override
    public ActionConfigurer setName(String name) {
        mConfigurationEditor.setName(name);
        return this;
    }

    @Override
    public ActionConfigurer addFlags(ActionFlag... flags) {
        mConfigurationEditor.addFlags(flags);
        return this;
    }

    public ActionConfiguration save() {
        return mConfigurationEditor.save();
    }
}
