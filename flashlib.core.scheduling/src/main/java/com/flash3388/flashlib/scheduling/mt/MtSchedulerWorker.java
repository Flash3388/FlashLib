package com.flash3388.flashlib.scheduling.mt;

import com.beans.Property;
import com.flash3388.flashlib.scheduling.SchedulerMode;
import com.flash3388.flashlib.scheduling.actions.ActionContext;
import org.slf4j.Logger;

public class MtSchedulerWorker implements Runnable {

    private final MtActionsControl mControl;
    private final Property<SchedulerMode> mCurrentMode;
    private final Logger mLogger;

    public MtSchedulerWorker(MtActionsControl control, Property<SchedulerMode> currentMode,
                             Logger logger) {
        mControl = control;
        mCurrentMode = currentMode;
        mLogger = logger;
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            SchedulerMode mode = mCurrentMode.get();
            if (mode == null) {
                Thread.yield();
                continue;
            }

            ActionContext context = mControl.pollRunningAction();
            if (context == null || context.isCanceled()) {
                continue;
            }

            try {
                if (mode.isDisabled() && !context.runWhenDisabled()) {
                    context.markCanceled();
                    mControl.pushFinishedAction(context);
                    mLogger.debug("Action {} running in disabled. Canceling", context);
                    continue;
                }

                if (!context.run()) {
                    mControl.pushFinishedAction(context);
                } else {
                    mControl.pushRunningAction(context);
                }
            } catch (Throwable t) {
                mLogger.error("Error while running an action", t);
                context.markCanceled();
                mControl.pushFinishedAction(context);
            }
        }
    }
}
