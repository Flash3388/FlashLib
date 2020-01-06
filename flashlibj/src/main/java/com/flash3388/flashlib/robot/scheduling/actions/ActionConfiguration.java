package com.flash3388.flashlib.robot.scheduling.actions;

import com.flash3388.flashlib.robot.scheduling.Subsystem;
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
            mConfiguration = new ActionConfiguration(originalConfiguration);
        }

        /**
         * Sets the run timeout for this action. If the given timeout is valid ({@link Time#isValid()}), then this
         * action will stop running (be canceled) once that timeout was reached.
         *
         * @param timeout timeout to set, or {@link Time#INVALID} to cancel timeout.
         * @return this instance
         */
        public Editor setTimeout(Time timeout){
            Objects.requireNonNull(timeout, "timeout is null");
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

        public Editor requires(Subsystem... subsystems){
            Objects.requireNonNull(subsystems, "requirements is null");
            mConfiguration.requires(Arrays.asList(subsystems));

            return this;
        }

        public Action save() {
            mAction.setConfiguration(mConfiguration);
            return mAction;
        }
    }

    private final Set<Subsystem> mRequirements;
    private Time mTimeout;

    public ActionConfiguration() {
        mRequirements = new HashSet<>();
        mTimeout = Time.INVALID;
    }

    public ActionConfiguration(ActionConfiguration other) {
        mRequirements = new HashSet<>(other.getRequirements());
        mTimeout = other.getTimeout();
    }

    public Set<Subsystem> getRequirements() {
        return Collections.unmodifiableSet(mRequirements);
    }

    public Time getTimeout() {
        return mTimeout;
    }

    public void setTimeout(Time timeout){
        Objects.requireNonNull(timeout, "timeout is null");
        mTimeout = timeout;
    }

    public void requires(Collection<Subsystem> subsystems) {
        Objects.requireNonNull(subsystems, "requirements is null");
        mRequirements.addAll(subsystems);
    }
}
