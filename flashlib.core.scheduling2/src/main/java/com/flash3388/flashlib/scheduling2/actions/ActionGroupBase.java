package com.flash3388.flashlib.scheduling2.actions;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

public abstract class ActionGroupBase implements ActionGroup {

    protected final Collection<Action> mActions;
    protected final boolean mAllowRequirementCollisions;

    protected ActionGroupBase(Collection<Action> actions, boolean allowRequirementCollisions) {
        mActions = actions;
        mAllowRequirementCollisions = allowRequirementCollisions;
    }

    @Override
    public ActionGroupBase add(Action action){
        Objects.requireNonNull(action, "action is null");
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
        mActions.addAll(actions);
        return this;
    }
}
