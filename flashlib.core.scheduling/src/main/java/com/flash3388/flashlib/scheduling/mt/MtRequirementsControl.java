package com.flash3388.flashlib.scheduling.mt;

import com.flash3388.flashlib.scheduling.Requirement;
import com.flash3388.flashlib.scheduling.Subsystem;
import com.flash3388.flashlib.scheduling.actions.Action;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class MtRequirementsControl {

    private final Logger mLogger;

    private final Map<Requirement, Action> mActionsOnRequirement;
    private final Map<Subsystem, Action> mDefaultActionsOnSubsystems;

    MtRequirementsControl(Logger logger, Map<Requirement, Action> actionsOnRequirement, Map<Subsystem, Action> defaultActionsOnSubsystems) {
        mLogger = logger;
        mActionsOnRequirement = actionsOnRequirement;
        mDefaultActionsOnSubsystems = defaultActionsOnSubsystems;
    }

    public MtRequirementsControl(Logger logger) {
        this(logger, new HashMap<>(5), new HashMap<>(5));
    }

    public void updateRequirementsNoCurrentAction(Action action) {
        for (Requirement requirement : action.getConfiguration().getRequirements()) {
            mActionsOnRequirement.remove(requirement);
        }
    }

    public Set<Action> updateRequirementsWithNewRunningAction(Action action) {
        Set<Action> conflictingActions = new HashSet<>();

        for (Requirement requirement : action.getConfiguration().getRequirements()) {
            if (mActionsOnRequirement.containsKey(requirement)) {
                Action currentAction = mActionsOnRequirement.get(requirement);
                conflictingActions.add(currentAction);

                mLogger.warn("Requirements conflict in Scheduler between {} and new action {} over requirement {}",
                        currentAction.toString(), action.toString(), requirement.toString());
            }

            mActionsOnRequirement.put(requirement, action);
        }

        return conflictingActions;
    }

    public Optional<Action> getActionOnRequirement(Requirement requirement) {
        return Optional.ofNullable(mActionsOnRequirement.get(requirement));
    }

    public void setDefaultActionOnSubsystem(Subsystem subsystem, Action action) {
        if (!action.getConfiguration().getRequirements().contains(subsystem)) {
            throw new IllegalArgumentException("Action should require the default subsystem");
        }

        Action old = mDefaultActionsOnSubsystems.put(subsystem, action);
        if (old != null && old.isRunning()) {
            old.cancel();
        }
    }

    public Map<Subsystem, Action> getDefaultActionsToStart() {
        Map<Subsystem, Action> actionsToStart = new HashMap<>();

        for (Map.Entry<Subsystem, Action> entry : mDefaultActionsOnSubsystems.entrySet()) {
            if (mActionsOnRequirement.containsKey(entry.getKey())) {
                continue;
            }

            actionsToStart.put(entry.getKey(), entry.getValue());
        }

        return actionsToStart;
    }
}
