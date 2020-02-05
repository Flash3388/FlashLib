package com.flash3388.flashlib.robot.scheduling;

import com.flash3388.flashlib.robot.scheduling.actions.Action;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

class SubsystemControl implements RequirementsControl {

    private final Logger mLogger;

    private final Map<Subsystem, Action> mActionsOnSubsystems;
    private final Map<Subsystem, Action> mDefaultActionsOnSubsystems;

    SubsystemControl(Logger logger, Map<Subsystem, Action> actionsOnSubsystems, Map<Subsystem, Action> defaultActionsOnSubsystems) {
        mLogger = logger;
        mActionsOnSubsystems = actionsOnSubsystems;
        mDefaultActionsOnSubsystems = defaultActionsOnSubsystems;
    }

    public SubsystemControl(Logger logger) {
        this(logger, new HashMap<>(5), new HashMap<>(5));
    }

    @Override
    public void updateRequirementsNoCurrentAction(Action action) {
        for (Subsystem subsystem : action.getConfiguration().getRequirements()) {
            mActionsOnSubsystems.remove(subsystem);
        }
    }

    @Override
    public void updateRequirementsWithNewRunningAction(Action action) {
        for (Subsystem subsystem : action.getConfiguration().getRequirements()) {
            if (mActionsOnSubsystems.containsKey(subsystem)) {
                Action currentAction = mActionsOnSubsystems.get(subsystem);
                currentAction.cancel();

                mLogger.warn("Requirements conflict in Scheduler between {} and new action {} over subsystem {}",
                        currentAction.toString(), action.toString(), subsystem.toString());
            }

            mActionsOnSubsystems.put(subsystem, action);
        }
    }

    public Optional<Action> getActionOnSubsystem(Subsystem subsystem) {
        return Optional.ofNullable(mActionsOnSubsystems.get(subsystem));
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
            if (mActionsOnSubsystems.containsKey(entry.getKey())) {
                continue;
            }

            actionsToStart.put(entry.getKey(), entry.getValue());
        }

        return actionsToStart;
    }
}
