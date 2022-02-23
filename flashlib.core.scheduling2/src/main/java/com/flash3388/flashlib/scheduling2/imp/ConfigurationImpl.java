package com.flash3388.flashlib.scheduling2.imp;

import com.flash3388.flashlib.scheduling2.ActionFlag;
import com.flash3388.flashlib.scheduling2.Configuration;
import com.flash3388.flashlib.scheduling2.Requirement;
import com.flash3388.flashlib.time.Time;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class ConfigurationImpl implements Configuration {

    private String mName;
    private final Set<Requirement> mRequirements;
    private Time mTimeout;
    private final Set<ActionFlag> mFlags;

    public ConfigurationImpl() {
        mName = "";
        mRequirements = new HashSet<>();
        mTimeout = Time.INVALID;
        mFlags = EnumSet.noneOf(ActionFlag.class);
    }

    @Override
    public String getName() {
        return mName;
    }

    @Override
    public void setName(String name) {
        mName = Objects.requireNonNull(name);
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
        mTimeout = Objects.requireNonNull(timeout);
    }

    @Override
    public Set<ActionFlag> getFlags() {
        return Collections.unmodifiableSet(mFlags);
    }

    @Override
    public void addFlags(Set<ActionFlag> flags) {
        mFlags.addAll(flags);
    }

    @Override
    public void removeFlags(Set<ActionFlag> flags) {
        mFlags.removeAll(flags);
    }

    @Override
    public void copyTo(Configuration other) {
        other.setName(mName);
        other.requires(mRequirements);
        other.setTimeout(mTimeout);
        other.addFlags(mFlags);
    }
}
