package com.flash3388.flashlib.scheduling.impl;

import com.flash3388.flashlib.scheduling.ActionConfiguration;
import com.flash3388.flashlib.scheduling.ActionInterface;
import com.flash3388.flashlib.scheduling.ExecutionContext;
import com.flash3388.flashlib.scheduling.Requirement;
import com.flash3388.flashlib.time.Clock;
import org.slf4j.Logger;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class InternalSingleThreadedScheduler {

    private final Clock mClock;
    private final Logger mLogger;

    private final Set<RunningActionNode> mPendingActions;
    private final Set<RunningActionNode> mRunningActions;
    private final Map<Requirement, RunningActionNode> mRequirementsUsage;


    public RunningActionNode start(ActionInterface action, ActionConfiguration configuration) {
        RunningActionNode node = new RunningActionNode(action, configuration, mRoot, mClock, mLogger);
        node.configure();

        if (!tryStartingAction(node)) {
            mPendingActions.add(node);
            mLogger.debug("Action {} pending", node);
        }

        return node;
    }

    /*public ExecutionContext createExecutionContext(ActionInterface action, ActionConfiguration configuration) {


        return new ExecutionContextImpl(mLogger);
    }*/

    private boolean tryStartingAction(RunningActionNode node) {
        if (!mCanModifyRunningActions) {
            mLogger.debug("Cannot modify running actions");
            return false;
        }

        try {
            Set<RunningActionNode> conflicts = getConflictingOnRequirements(node);
            conflicts.forEach((conflict)-> {
                cancelAndEnd(conflict);
                mRunningActions.remove(conflict);

                mLogger.warn("Action {} has conflict with {}. Canceling old.",
                        node, conflict);
            });

            // no conflicts, let's start

            setOnRequirements(node);
            mRunningActions.add(node);
            node.start();

            mLogger.debug("Action {} started running", node);

            return true;
        } catch (ActionHasPreferredException e) {
            return false;
        }
    }

    private Set<RunningActionNode> getConflictingOnRequirements(RunningActionNode newNode) {
        Set<RunningActionNode> conflicts = new HashSet<>();
        for (Requirement requirement : newNode.getRequirements()) {
            RunningActionNode currentNode = mRequirementsUsage.get(requirement);
            if (currentNode != null) {
                if (currentNode.isPreferred()) {
                    // cannot cancel it as it is the preferred one.
                    // will have to wait for it to finish

                    mLogger.warn("Action {} has conflict with (PREFERRED) {} on {}. " +
                                    "Not canceling old, must wait for it to finish.",
                            newNode, currentNode, requirement);

                    throw new ActionHasPreferredException();
                }

                conflicts.add(currentNode);
            }
        }

        return conflicts;
    }

    private void setOnRequirements(RunningActionNode node) {
        for (Requirement requirement : node.getRequirements()) {
            mRequirementsUsage.put(requirement, node);
        }
    }

    private void removeFromRequirements(RunningActionNode node) {
        for (Requirement requirement : node.getRequirements()) {
            mRequirementsUsage.remove(requirement);
        }
    }

    private void cancelAndEnd(RunningActionNode node) {
        node.interrupt();
        removeFromRequirements(node);

        mLogger.debug("Action {} finished", node);
    }
}
