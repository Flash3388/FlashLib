package com.flash3388.flashlib.scheduling;

import com.flash3388.flashlib.time.Time;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class ActionConfiguration {

    private final String mName;
    private final Time mTimeout;
    private final Set<Requirement> mRequirements;
    private final EnumSet<ActionFlag> mFlags;

    public ActionConfiguration(String name, Time timeout, Set<Requirement> requirements, EnumSet<ActionFlag> flags) {
        mName = Objects.requireNonNull(name, "name is null");
        mTimeout = Objects.requireNonNull(timeout, "timeout is null");
        mRequirements = Collections.unmodifiableSet(
                new HashSet<>(Objects.requireNonNull(requirements, "requirements is null")));
        mFlags = EnumSet.copyOf(Objects.requireNonNull(flags, "flags are null"));
    }

    public ActionConfiguration(ActionConfiguration other) {
        this(other.mName, other.mTimeout, other.mRequirements, other.mFlags);
    }

    public ActionConfiguration() {
        this("", Time.INVALID, Collections.emptySet(), EnumSet.noneOf(ActionFlag.class));
    }

    public String getName() {
        return mName;
    }

    public Time getTimeout() {
        return mTimeout;
    }

    public Set<Requirement> getRequirements() {
        return mRequirements;
    }

    public EnumSet<ActionFlag> getFlags() {
        return mFlags;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ActionConfiguration that = (ActionConfiguration) o;
        return Objects.equals(mName, that.mName) &&
                Objects.equals(mTimeout, that.mTimeout) &&
                Objects.equals(mRequirements, that.mRequirements) &&
                Objects.equals(mFlags, that.mFlags);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mName, mTimeout, mRequirements, mFlags);
    }

    @Override
    public String toString() {
        return "ActionConfiguration{" +
                "mName='" + mName + '\'' +
                ", mTimeout=" + mTimeout +
                ", mRequirements=" + mRequirements +
                ", mFlags=" + mFlags +
                '}';
    }
}
