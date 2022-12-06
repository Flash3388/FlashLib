package com.flash3388.flashlib.scheduling.actions;

import com.flash3388.flashlib.global.GlobalDependencies;
import com.flash3388.flashlib.scheduling.Scheduler;
import org.slf4j.Logger;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

public abstract class ActionGroupBase extends ActionBase implements ActionGroup {

    private final Logger mLogger;
    protected final Collection<Action> mActions;
    private final boolean mAllowRequirementCollisions;

    protected boolean mRunWhenDisabled;
    protected Runnable mWhenInterrupted;

    protected ActionGroupBase(Scheduler scheduler, Logger logger, Collection<Action> actions, boolean allowRequirementCollisions) {
        super(scheduler);
        mLogger = logger;
        mActions = actions;
        mAllowRequirementCollisions = allowRequirementCollisions;
        mRunWhenDisabled = false;
        mWhenInterrupted = null;
    }

    protected ActionGroupBase(Logger logger, Collection<Action> actions, boolean allowRequirementCollisions) {
        mLogger = logger;
        mActions = actions;
        mAllowRequirementCollisions = allowRequirementCollisions;
        mRunWhenDisabled = false;
    }

    protected ActionGroupBase(Collection<Action> actions, boolean allowRequirementCollisions) {
        this(GlobalDependencies.getLogger(), actions, allowRequirementCollisions);
    }

    @Override
    public ActionGroupBase add(Action action){
        Objects.requireNonNull(action, "action is null");

        ActionConfiguration configuration = action.getConfiguration();

        if (!mAllowRequirementCollisions) {
            if (!Collections.disjoint(getConfiguration().getRequirements(),
                    configuration.getRequirements())) {
                throw new IllegalArgumentException("Actions cannot share requirements");
            }
        }

        if (mActions.isEmpty()) {
            mRunWhenDisabled = configuration.shouldRunWhenDisabled();
        } else {
            mRunWhenDisabled &= configuration.shouldRunWhenDisabled();
        }

        configure()
                .setRunWhenDisabled(mRunWhenDisabled)
                .requires(configuration.getRequirements())
                .save();

        mActions.add(action);

        return this;
    }

    @Override
    public ActionGroupBase add(Action... actions){
        Objects.requireNonNull(actions, "actions is null");
        return add(Arrays.asList(actions));
    }

    @Override
    public ActionGroupBase add(Collection<Action> actions){
        Objects.requireNonNull(actions, "actions is null");
        actions.forEach(this::add);

        return this;
    }

    @Override
    public ActionGroupBase whenInterrupted(Runnable runnable) {
        if (mWhenInterrupted != null) {
            mLogger.debug("whenInterrupted callback overridden for ActionGroup: {}", getConfiguration().getName());
        }

        mWhenInterrupted = runnable;
        return this;
    }

    @Override
    public void end(boolean wasInterrupted) {
        if (wasInterrupted && mWhenInterrupted != null) {
            mWhenInterrupted.run();
        }
    }
}
