package com.flash3388.flashlib.scheduling.impl;

import com.flash3388.flashlib.net.obsr.StoredObject;
import com.flash3388.flashlib.scheduling.ActionConfiguration;
import com.flash3388.flashlib.scheduling.ActionFlag;
import com.flash3388.flashlib.scheduling.ActionInterface;
import com.flash3388.flashlib.scheduling.ExecutionContext;
import com.flash3388.flashlib.scheduling.Requirement;
import com.flash3388.flashlib.time.Clock;
import org.slf4j.Logger;

import java.util.Set;
import java.util.UUID;

public class RunningActionNode {

    private final ActionInterface mAction;
    private ActionConfiguration mConfiguration;
    private ExecutionContext mContext;

    private final ActionContext2 mBaseContext;

    public RunningActionNode(ActionInterface action, ActionConfiguration configuration,
                             StoredObject rootObject,
                             Clock clock,
                             Logger logger) {
        mAction = action;

        StoredObject obsrObject = rootObject.getChild(UUID.randomUUID().toString());
        mBaseContext = new ActionContext2(
                action, configuration,
                obsrObject,
                clock,
                logger
        );
    }


    public boolean shouldRunInDisabled() {
        return mConfiguration.hasFlags(ActionFlag.RUN_ON_DISABLED);
    }

    public boolean isPreferred() {
        return mConfiguration.hasFlags(ActionFlag.PREFERRED_FOR_REQUIREMENTS);
    }

    public Set<Requirement> getRequirements() {
        return mConfiguration.getRequirements();
    }

    public void configure() {
        mConfiguration = mBaseContext.configure();
    }

    public void start() {
        mContext = new ExecutionContextImpl(mBaseContext, mLogger);
    }

    public void interrupt() {
        mContext.interrupt();
    }
}
