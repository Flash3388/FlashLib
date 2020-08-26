package com.flash3388.flashlib.scheduling.actions;

import com.flash3388.flashlib.scheduling.Scheduler;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

public abstract class ActionGroupBase extends ActionBase implements ActionGroup {

    protected final Collection<Action> mActions;
    private final boolean mAllowRequirementCollisions;

    protected boolean mRunWhenDisabled;

    protected ActionGroupBase(Scheduler scheduler, Collection<Action> actions, boolean allowRequirementCollisions) {
        super(scheduler);
        mActions = actions;
        mAllowRequirementCollisions = allowRequirementCollisions;
        mRunWhenDisabled = false;
    }

    protected ActionGroupBase(Collection<Action> actions, boolean allowRequirementCollisions) {
        mActions = actions;
        mAllowRequirementCollisions = allowRequirementCollisions;
        mRunWhenDisabled = false;
    }

    /**
     * Adds an action to run.
     *
     * @param action action to run
     * @return this instance
     */
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

    /**
     * Adds an array of scheduling to run.
     *
     * @param actions actions to run
     * @return this instance
     */
    @Override
    public ActionGroupBase add(Action... actions){
        Objects.requireNonNull(actions, "actions is null");
        return add(Arrays.asList(actions));
    }

    /**
     * Adds an array of scheduling to run.
     *
     * @param actions action to run
     * @return this instance
     */
    @Override
    public ActionGroupBase add(Collection<Action> actions){
        Objects.requireNonNull(actions, "actions is null");
        actions.forEach(this::add);

        return this;
    }
}
