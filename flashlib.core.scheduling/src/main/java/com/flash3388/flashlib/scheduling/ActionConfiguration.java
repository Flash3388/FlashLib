package com.flash3388.flashlib.scheduling;

import com.flash3388.flashlib.time.Time;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

public class ActionConfiguration {

    public static class Editor {

        private final Set<Requirement> mRequirements;
        private Time mTimeout;
        private String mName;
        private final EnumSet<ActionFlag> mFlags;

        public Editor(ActionConfiguration configuration) {
            mRequirements = configuration.mRequirements;
            mTimeout = configuration.mTimeout;
            mName = configuration.mName;
            mFlags = configuration.mFlags;
        }

        public void addRequirements(Collection<? extends Requirement> requirements) {
            mRequirements.addAll(requirements);
        }

        public void addRequirements(Requirement... requirements) {
            addRequirements(Arrays.asList(requirements));
        }

        public void removeRequirements(Collection<? extends Requirement> requirements) {
            mRequirements.removeAll(requirements);
        }

        public void removeRequirements(Requirement... requirements) {
            removeRequirements(Arrays.asList(requirements));
        }

        public void setTimeout(Time timeout) {
            mTimeout = timeout;
        }

        public void clearTimeout() {
            setTimeout(Time.INVALID);
        }

        public void setName(String name) {
            mName = name;
        }

        public void addFlags(Collection<? extends ActionFlag> flags) {
            mFlags.addAll(flags);
        }

        public void addFlags(ActionFlag... flags) {
            addFlags(Arrays.asList(flags));
        }

        public void removeFlags(Collection<? extends ActionFlag> flags) {
            mFlags.removeAll(flags);
        }

        public void removeFlags(ActionFlag... flags) {
            removeFlags(Arrays.asList(flags));
        }

        public ActionConfiguration save() {
            return new ActionConfiguration(mRequirements, mTimeout, mName, mFlags);
        }
    }

    private final Set<Requirement> mRequirements;
    private final Time mTimeout;
    private final String mName;
    private final EnumSet<ActionFlag> mFlags;

    public ActionConfiguration(Collection<Requirement> requirements, Time timeout, String name, Set<ActionFlag> flags) {
        mRequirements = new HashSet<>(requirements);
        mTimeout = timeout;
        mName = name;
        mFlags = EnumSet.noneOf(ActionFlag.class);
        mFlags.addAll(flags);
    }

    public ActionConfiguration(Collection<Requirement> requirements, Time timeout, String name, ActionFlag... flags) {
        this(requirements, timeout, name, new HashSet<>(Arrays.asList(flags)));
    }

    public ActionConfiguration() {
        this(Collections.emptyList(), Time.INVALID, "", EnumSet.noneOf(ActionFlag.class));
    }

    @SuppressWarnings("CopyConstructorMissesField")
    public ActionConfiguration(ActionConfiguration other) {
        this(other.getRequirements(), other.getTimeout(), other.getName(), other.flags());
    }

    public Set<Requirement> getRequirements() {
        return Collections.unmodifiableSet(mRequirements);
    }

    public Time getTimeout() {
        return mTimeout;
    }

    public String getName() {
        return mName;
    }

    public Set<ActionFlag> flags() {
        return Collections.unmodifiableSet(mFlags);
    }

    public boolean hasFlags(ActionFlag... flags) {
        for (ActionFlag flag : flags) {
            if (!mFlags.contains(flag)) {
                return false;
            }
        }

        return true;
    }

    public boolean shouldRunWhenDisabled() {
        return hasFlags(ActionFlag.RUN_ON_DISABLED);
    }
}
