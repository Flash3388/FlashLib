package com.flash3388.flashlib.scheduling.impl.statemachines;

import com.flash3388.flashlib.scheduling.ActionControl;
import com.flash3388.flashlib.scheduling.FinishReason;
import com.flash3388.flashlib.scheduling.Scheduler;
import com.flash3388.flashlib.scheduling.actions.Action;
import com.flash3388.flashlib.scheduling.actions.ActionBase;
import com.flash3388.flashlib.scheduling.statemachines.Transition;

public class AttachedAction extends ActionBase {

    private final Action mWrapperAction;
    private final Transition mOnFinish;

    public AttachedAction(Scheduler scheduler, Action wrapperAction, Transition onFinish) {
        super(scheduler);
        mWrapperAction = wrapperAction;
        mOnFinish = onFinish;

        setConfiguration(wrapperAction.getConfiguration());
    }

    @Override
    public void initialize(ActionControl control) {
        mWrapperAction.initialize(control);
    }

    @Override
    public void execute(ActionControl control) {
        mWrapperAction.execute(control);
    }

    @Override
    public void end(FinishReason reason) {
        mWrapperAction.end(reason);

        if (mOnFinish != null) {
            mOnFinish.initiate();
        }
    }
}
