package com.flash3388.flashlib.scheduling.impl;

public class GroupPolicy {

    private final boolean mAllowRequirementCollisions;
    private final boolean mExecuteActionsInParallel;
    private final boolean mStopOnFirstActionFinished;

    public GroupPolicy(boolean allowRequirementCollisions, boolean executeActionsInParallel, boolean stopOnFirstActionFinished) {
        mAllowRequirementCollisions = allowRequirementCollisions;
        mExecuteActionsInParallel = executeActionsInParallel;
        mStopOnFirstActionFinished = stopOnFirstActionFinished;
    }

    public static GroupPolicy sequential() {
        return new GroupPolicy(true, false, false);
    }

    public static GroupPolicy parallel() {
        return new GroupPolicy(false, true, false);
    }

    public static GroupPolicy parallelRace() {
        return new GroupPolicy(false, true, true);
    }

    public boolean shouldAllowRequirementCollisions() {
        return mAllowRequirementCollisions;
    }


    public boolean shouldExecuteActionsInParallel() {
        return mExecuteActionsInParallel;
    }

    public boolean shouldStopOnFirstActionFinished() {
        return mStopOnFirstActionFinished;
    }
}
