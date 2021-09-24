package com.flash3388.flashlib.scheduling2;

import com.flash3388.flashlib.scheduling2.actions.Action;
import com.flash3388.flashlib.scheduling2.actions.ActionContext;
import com.flash3388.flashlib.scheduling2.actions.Status;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RequirementsControl {

    private final Map<Requirement, ActionContext<?>> mActionsOnRequirement;
    private final Map<Requirement, ActionContext<?>> mDefaultActions;

    public RequirementsControl() {
        mActionsOnRequirement = new HashMap<>(5);
        mDefaultActions = new HashMap<>(5);
    }

    public void updateRequirementsFree(Collection<? extends Requirement> requirements) {
        for (Requirement requirement : requirements) {
            mActionsOnRequirement.remove(requirement);
        }
    }

    public void updateRequirementsTaken(Collection<? extends Requirement> requirements,
                                                         ActionContext<?> newContext) {
        for (Requirement requirement : requirements) {
            mActionsOnRequirement.put(requirement, newContext);
        }
    }

    public Set<ActionContext<?>> getConflicting(Collection<? extends Requirement> requirements) {
        Set<ActionContext<?>> conflicting = new HashSet<>();

        for (Requirement requirement : requirements) {
            if (mActionsOnRequirement.containsKey(requirement)) {
                ActionContext<?> current = mActionsOnRequirement.get(requirement);
                conflicting.add(current);
            }
        }

        return conflicting;
    }

    public void setDefaultActionOnRequirement(Requirement requirement, ActionContext<?> context) {
        ActionContext<?> old = mDefaultActions.put(requirement, context);
        if (old != null) {
            old.cancel();
        }
    }

    public Map<Requirement, ActionContext<?>> getDefaultActionsToStart() {
        Map<Requirement, ActionContext<?>> actionsToStart = new HashMap<>();

        for (Map.Entry<Requirement, ActionContext<?>> entry : mDefaultActions.entrySet()) {
            if (mActionsOnRequirement.containsKey(entry.getKey())) {
                continue;
            }

            actionsToStart.put(entry.getKey(), entry.getValue());
        }

        return actionsToStart;
    }
}
