package com.flash3388.flashlib.robot.scheduling;

import com.flash3388.flashlib.robot.scheduling.actions.Action;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

class RequirementsControl {

    private final Logger mLogger;

    private final Map<Requirement, Action> mActionsOnRequirement;
    private final Map<Subsystem, Action> mDefaultActionsOnSubsystems;

    RequirementsControl(Logger logger, Map<Requirement, Action> actionsOnRequirement, Map<Subsystem, Action> defaultActionsOnSubsystems) {
        mLogger = logger;
        mActionsOnRequirement = actionsOnRequirement;
        mDefaultActionsOnSubsystems = defaultActionsOnSubsystems;
    }

    public RequirementsControl(Logger logger) {
        this(logger, new HashMap<>(5), new HashMap<>(5));
    }

    public void updateRequirementsNoCurrentAction(Action action) {
        for (Requirement requirement : action.getConfiguration().getRequirements()) {
            mActionsOnRequirement.remove(requirement);
        }
    }

    public void updateRequirementsWithNewRunningAction(Action action) {
        for (Requirement requirement : action.getConfiguration().getRequirements()) {
            if (mActionsOnRequirement.containsKey(requirement)) {
                Action currentAction = mActionsOnRequirement.get(requirement);
                currentAction.cancel();

                mLogger.warn("Requirements conflict in Scheduler between {} and new action {} over requirement {}",
                        currentAction.toString(), action.toString(), requirement.toString());
            }

            mActionsOnRequirement.put(requirement, action);
        }
    }

    public Optional<Action> getActionOnRequirement(Requirement requirement) {
        return Optional.ofNullable(mActionsOnRequirement.get(requirement));
    }

    public void setDefaultActionOnSubsystem(Subsystem subsystem, Action action) {
        if (!action.getConfiguration().getRequirements().contains(subsystem)) {
            action.configure()
                    .requires(subsystem)
                    .save();
        }

        mDefaultActionsOnSubsystems.put(subsystem, action);
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
