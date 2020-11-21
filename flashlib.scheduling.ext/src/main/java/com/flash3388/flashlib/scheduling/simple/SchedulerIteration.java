package com.flash3388.flashlib.scheduling.simple;

import com.flash3388.flashlib.scheduling.SchedulerMode;
import com.flash3388.flashlib.scheduling.actions.Action;
import com.flash3388.flashlib.scheduling.ActionContext;
import com.flash3388.flashlib.scheduling.ActionStore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class SchedulerIteration {

    private final ActionStore mActionStore;

    private final Collection<Action> mActionsToRemove;

    public SchedulerIteration(ActionStore actionStore) {
        mActionStore = actionStore;
        mActionsToRemove = new ArrayList<>(5);
    }

    public void run(SchedulerMode mode) {
        Map<Action, ActionContext> running = mActionStore.updateActionStatus(mActionsToRemove, mode);
        mActionsToRemove.clear();

        runActions(running.entrySet(), mode);
    }

    private void runActions(Set<Map.Entry<Action, ActionContext>> running, SchedulerMode mode) {
        for (Map.Entry<Action, ActionContext> entry : running) {
            Action action = entry.getKey();
            ActionContext context = entry.getValue();

            /*if (mode.isDisabled() && !context.shouldRunWhenDisabled()) {
                mActionsToRemove.add(action);
                continue;
            }

            if (!context.run()) {
                mActionsToRemove.add(action);
            }*/
        }
    }
}
