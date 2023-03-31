package com.flash3388.flashlib.scheduling;

import com.flash3388.flashlib.time.Time;

import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

public class ActionConfigurationEditor {

    private String mName;
    private Time mTimeout;
    private final Set<Requirement> mRequirements;
    private final EnumSet<ActionFlag> mFlags;

    public ActionConfigurationEditor(ActionConfiguration configuration) {
        mName = configuration.getName();
        mTimeout = configuration.getTimeout();
        mRequirements = new HashSet<>(configuration.getRequirements());
        mFlags = EnumSet.copyOf(configuration.getFlags());
    }

    public ActionConfigurationEditor() {
        this(new ActionConfiguration());
    }

    public String getName() {
        return mName;
    }

    public ActionConfigurationEditor setName(String name) {
        mName = name;
        return this;
    }

    public Time getTimeout() {
        return mTimeout;
    }

    public ActionConfigurationEditor setTimeout(Time timeout) {
        mTimeout = timeout;
        return this;
    }

    public ActionConfigurationEditor cancelTimeout() {
        return setTimeout(Time.INVALID);
    }

    public ActionConfigurationEditor addRequirements(Collection<? extends Requirement> requirements) {
        mRequirements.addAll(requirements);
        return this;
    }

    public Set<Requirement> getRequirements() {
        return mRequirements;
    }

    public ActionConfigurationEditor addRequirements(Requirement... requirements) {
        return addRequirements(Arrays.asList(requirements));
    }

    public ActionConfigurationEditor removeRequirements(Collection<? extends Requirement> requirements) {
        mRequirements.removeAll(requirements);
        return this;
    }

    public ActionConfigurationEditor removeRequirements(Requirement... requirements) {
        return removeRequirements(Arrays.asList(requirements));
    }

    public EnumSet<ActionFlag> getFlags() {
        return mFlags;
    }

    public ActionConfigurationEditor addFlags(Collection<? extends ActionFlag> flags) {
        mFlags.addAll(flags);
        return this;
    }

    public ActionConfigurationEditor addFlags(ActionFlag... flags) {
        return addFlags(Arrays.asList(flags));
    }

    public ActionConfigurationEditor removeFlags(Collection<? extends ActionFlag> flags) {
        mFlags.removeAll(flags);
        return this;
    }

    public ActionConfigurationEditor removeFlags(ActionFlag... flags) {
        return removeFlags(Arrays.asList(flags));
    }

    public ActionConfiguration save() {
        return new ActionConfiguration(
                mName,
                mTimeout,
                mRequirements,
                mFlags
        );
    }
}
