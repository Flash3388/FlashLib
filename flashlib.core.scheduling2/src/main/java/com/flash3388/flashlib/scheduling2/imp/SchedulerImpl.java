package com.flash3388.flashlib.scheduling2.imp;

import com.flash3388.flashlib.scheduling2.Action;
import com.flash3388.flashlib.scheduling2.ActionExecutionBuilder;
import com.flash3388.flashlib.scheduling2.ActionFlag;
import com.flash3388.flashlib.scheduling2.Configuration;
import com.flash3388.flashlib.scheduling2.Requirement;
import com.flash3388.flashlib.scheduling2.Scheduler;
import com.flash3388.flashlib.scheduling2.SchedulerMode;
import com.flash3388.flashlib.scheduling2.Status;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.time.Time;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SchedulerImpl implements Scheduler {

    private static class Pending {
        ActionContext context;
        Configuration originalConfiguration;
        Configuration configuration;

        Pending(ActionContext context, Configuration originalConfiguration) {
            this.context = context;
            this.originalConfiguration = originalConfiguration;
            this.configuration = null;
        }

        @Override
        public String toString() {
            return configuration.getName();
        }
    }
    private static class Executing {
        ActionContext context;
        Configuration configuration;

        Executing(ActionContext context, Configuration configuration) {
            this.context = context;
            this.configuration = configuration;
        }

        @Override
        public String toString() {
            return configuration.getName();
        }
    }

    private final Clock mClock;
    private final Logger mLogger;

    private final List<Pending> mPending;;
    private final List<Executing> mInExecution;

    private final Map<Requirement, Executing> mRequirementsUsage;

    public SchedulerImpl(Clock clock, Logger logger) {
        mClock = clock;
        mLogger = logger;

        mPending = new LinkedList<>();
        mInExecution = new LinkedList<>();

        mRequirementsUsage = new HashMap<>();
    }

    @Override
    public Status start(Action action) {
        return startInternal(action, newConfiguration(action));
    }

    @Override
    public ActionExecutionBuilder submit(Action action) {
        return new ActionExecutionBuilderImpl(action, newConfiguration(action), this::startInternal);
    }

    @Override
    public void run(SchedulerMode mode) {
        for (Iterator<Pending> iterator = mPending.iterator(); iterator.hasNext();) {
            Pending pending = iterator.next();

            if (pending.configuration == null) {
                // wasn't configured yet

                pending.configuration = pending.context.configure(pending.originalConfiguration);
                if (pending.configuration == null) {
                    // error in configuration
                    iterator.remove();
                    continue;
                }

                mLogger.debug("Action {} configured", pending);
            }

            if (!cancelConflictingOnRequirements(pending)) {
                // no conflicts, let's start
                Executing executing = new Executing(pending.context, pending.configuration);
                setOnRequirements(executing);
                mInExecution.add(executing);

                iterator.remove();

                mLogger.debug("Action {} started running", executing);
            }
        }

        for (Iterator<Executing> iterator = mInExecution.iterator(); iterator.hasNext();) {
            Executing executing = iterator.next();

            if (mode.isDisabled() &&
                    !executing.configuration.getFlags().contains(ActionFlag.RUN_ON_DISABLED)) {
                executing.context.markForCancellation();

                mLogger.warn("Action {} is not allowed to run in disabled. Cancelling", executing);
            }

            if (executing.context.iterate()) {
                // finished execution
                removeFromRequirements(executing);
                iterator.remove();

                mLogger.debug("Action {} finished", executing);
            }
        }
    }

    private Status startInternal(Action action, Configuration configuration) {
        ActionState state = new ActionState(mClock);
        ActionContext context = new ActionContext(mClock, action, state);
        Status status = new StatusImpl(state, context);

        mPending.add(new Pending(context, configuration));

        return status;
    }

    private Configuration newConfiguration(Action action) {
        Configuration configuration = new ConfigurationImpl();
        configuration.setName(action.getClass().getSimpleName());
        configuration.setTimeout(Time.INVALID);

        return configuration;
    }

    private boolean cancelConflictingOnRequirements(Pending pending) {
        boolean hasConflicting = false;
        for (Requirement requirement : pending.configuration.getRequirements()) {
            Executing current = mRequirementsUsage.get(requirement);
            if (current != null) {
                if (current.configuration.getFlags().contains(ActionFlag.PREFERRED_FOR_REQUIREMENT)) {
                    // cannot cancel it as it is the preferred one.
                    // will have to wait for it to finish

                    mLogger.warn("Action {} has conflict with (PREFERRED) {} on {}. Not canceling old, must wait for it to finish.",
                            pending, current, requirement);
                } else {
                    current.context.markForCancellation();

                    mLogger.warn("Action {} has conflict with {} on {}. Canceling old.",
                            pending, current, requirement);
                }

                hasConflicting = true;
            }
        }

        return hasConflicting;
    }

    private void setOnRequirements(Executing executing) {
        for (Requirement requirement : executing.configuration.getRequirements()) {
            mRequirementsUsage.put(requirement, executing);
        }
    }

    private void removeFromRequirements(Executing executing) {
        for (Requirement requirement : executing.configuration.getRequirements()) {
            mRequirementsUsage.remove(requirement);
        }
    }
}
