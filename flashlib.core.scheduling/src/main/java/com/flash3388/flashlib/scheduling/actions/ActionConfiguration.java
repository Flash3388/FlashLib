package com.flash3388.flashlib.scheduling.actions;

import com.flash3388.flashlib.scheduling.Requirement;
import com.flash3388.flashlib.time.Time;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class ActionConfiguration {

    public static class Editor {

        private final Action mAction;
        private final ActionConfiguration mConfiguration;

        Editor(Action action, ActionConfiguration originalConfiguration) {
            mAction = action;
            mConfiguration = originalConfiguration;
        }

        /**
         * Sets the run timeout for this action. If the given timeout is valid ({@link Time#isValid()}), then this
         * action will stop running (be canceled) once that timeout was reached.
         *
         * @param timeout timeout to set, or {@link Time#INVALID} to cancel timeout.
         * @return this instance
         */
        public Editor setTimeout(Time timeout){
            mConfiguration.setTimeout(timeout);
            return this;
        }

        /**
         * Cancels the timeout set for this action. Calls {@link #setTimeout(Time)} with {@link Time#INVALID}
         *
         * @return this instance
         */
        public Editor cancelTimeout(){
            return setTimeout(Time.INVALID);
        }

        public Editor requires(Collection<? extends Requirement> requirements){
            Objects.requireNonNull(requirements, "requirements is null");
            mConfiguration.requires(requirements);
            return this;
        }

        public Editor requires(Requirement... requirements){
            Objects.requireNonNull(requirements, "requirements is null");
            return requires(Arrays.asList(requirements));
        }

        public Editor setName(String name) {
            mConfiguration.setName(name);
            return this;
        }

        public Editor addFlags(ActionFlag... flags) {
            mConfiguration.addFlags(flags);
            return this;
        }

        public Editor removeFlags(ActionFlag... flags) {
            mConfiguration.removeFlags(flags);
            return this;
        }

        public Editor setRunWhenDisabled(boolean runWhenDisabled) {
            if (runWhenDisabled) {
                mConfiguration.addFlags(ActionFlag.RUN_ON_DISABLED);
            } else {
                mConfiguration.removeFlags(ActionFlag.RUN_ON_DISABLED);
            }
            return this;
        }

        public Action save() {
            mAction.setConfiguration(mConfiguration);
            return mAction;
        }
    }

    private final Set<Requirement> mRequirements;
    private Time mTimeout;
    private String mName;
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

    void requires(Collection<? extends Requirement> requirements) {
        Objects.requireNonNull(requirements, "requirements is null");
        mRequirements.addAll(requirements);
    }

    void setTimeout(Time timeout){
        Objects.requireNonNull(timeout, "timeout is null");
        mTimeout = timeout;
    }

    void setName(String name) {
        Objects.requireNonNull(name, "name is null");
        mName = name;
    }

    void addFlags(ActionFlag... flags) {
        mFlags.addAll(Arrays.asList(flags));
    }

    void removeFlags(ActionFlag... flags) {
        mFlags.removeAll(Arrays.asList(flags));
    }
}
