package com.flash3388.flashlib.scheduling.impl;

import com.flash3388.flashlib.scheduling.DefaultActionRegistration;
import com.flash3388.flashlib.scheduling.ScheduledAction;
import com.flash3388.flashlib.scheduling.actions.Action;
import com.flash3388.flashlib.scheduling.actions.ActionConfiguration;

public class RegisteredDefaultAction {

    private final long mId;
    private final Action mAction;
    private final ActionConfiguration mConfiguration;
    private final ObsrActionContext mObsrActionContext;
    private final DefaultActionRegistrationImpl mDefaultActionRegistration;

    public RegisteredDefaultAction(long id,
                                   Action action,
                                   ActionConfiguration configuration,
                                   ObsrActionContext obsrActionContext,
                                   DefaultActionRegistrationImpl defaultActionRegistration) {
        mId = id;
        mAction = action;
        mConfiguration = configuration;
        mObsrActionContext = obsrActionContext;
        mDefaultActionRegistration = defaultActionRegistration;
    }

    public long getId() {
        return mId;
    }

    public Action getAction() {
        return mAction;
    }

    public ActionConfiguration getConfiguration() {
        return mConfiguration;
    }

    public ObsrActionContext getObsrActionContext() {
        return mObsrActionContext;
    }

    public DefaultActionRegistration getRegistration() {
        return mDefaultActionRegistration;
    }

    public void updateActionStarted(ScheduledAction action) {
        mDefaultActionRegistration.updateStarted(action);
    }

    public void removed() {
        mObsrActionContext.delete();
        mDefaultActionRegistration.removed();
    }
}
