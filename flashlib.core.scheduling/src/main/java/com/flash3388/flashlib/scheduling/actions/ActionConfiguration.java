package com.flash3388.flashlib.scheduling.actions;

import com.flash3388.flashlib.scheduling.Requirement;
import com.flash3388.flashlib.time.Time;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
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

        public Editor setRunWhenDisabled(boolean runWhenDisabled) {
            mConfiguration.setRunWhenDisabled(runWhenDisabled);
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
    private boolean mRunWhenDisabled;

    public ActionConfiguration(Collection<Requirement> requirements, Time timeout, String name, boolean runWhenDisabled) {
        mRequirements = new HashSet<>(requirements);
        mTimeout = timeout;
        mName = name;
        mRunWhenDisabled = runWhenDisabled;
    }

    public ActionConfiguration() {
        this(Collections.emptyList(), Time.INVALID, "", false);
    }

    public ActionConfiguration(ActionConfiguration other) {
        this(other.getRequirements(), other.getTimeout(), other.getName(), other.shouldRunWhenDisabled());
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

    public boolean shouldRunWhenDisabled() {
        return mRunWhenDisabled;
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

    void setRunWhenDisabled(boolean runWhenDisabled) {
        mRunWhenDisabled = runWhenDisabled;
    }
}
