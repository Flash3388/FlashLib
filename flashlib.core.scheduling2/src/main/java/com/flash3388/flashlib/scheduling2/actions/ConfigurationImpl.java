package com.flash3388.flashlib.scheduling2.actions;

import com.flash3388.flashlib.scheduling2.Requirement;
import com.flash3388.flashlib.time.Time;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ConfigurationImpl implements Configuration {

    private String mName;
    private final Set<Requirement> mRequirements;
    private Time mTimeout;
    private boolean mRunWhenDisabled;

    public ConfigurationImpl() {
        mName = "";
        mRequirements = new HashSet<>();
        mTimeout = Time.INVALID;
        mRunWhenDisabled = false;
    }

    @Override
    public String getName() {
        return mName;
    }

    @Override
    public void setName(String name) {
        mName = name;
    }

    @Override
    public Set<Requirement> getRequirements() {
        return Collections.unmodifiableSet(mRequirements);
    }

    @Override
    public void requires(Collection<? extends Requirement> requirements) {
        mRequirements.addAll(requirements);
    }

    @Override
    public Time getTimeout() {
        return mTimeout;
    }

    @Override
    public void setTimeout(Time timeout) {
        mTimeout = timeout;
    }

    @Override
    public boolean shouldRunWhenDisabled() {
        return mRunWhenDisabled;
    }

    @Override
    public void setRunWhenDisabled(boolean run) {
        mRunWhenDisabled = run;
    }

    @Override
    public void copyTo(Configuration other) {
        other.setName(mName);
        other.requires(mRequirements);
        other.setTimeout(mTimeout);
        other.setRunWhenDisabled(mRunWhenDisabled);
    }
}
