package com.flash3388.flashlib.scheduling.impl;


import com.flash3388.flashlib.scheduling.ActionConfiguration;
import com.flash3388.flashlib.scheduling.ActionFuture;
import com.flash3388.flashlib.scheduling.ActionGroup;
import com.flash3388.flashlib.scheduling.ActionGroupType;
import com.flash3388.flashlib.scheduling.ActionInterface;
import com.flash3388.flashlib.scheduling.ConfiguredAction;
import com.flash3388.flashlib.scheduling.Scheduler;

import java.lang.ref.WeakReference;

public class ConfiguredActionImpl implements ConfiguredAction {

    private final WeakReference<Scheduler> mScheduler;
    private final ActionInterface mInterface;

    private ActionConfiguration mConfiguration;
    private ActionFuture mFuture;

    public ConfiguredActionImpl(Scheduler scheduler, ActionInterface anInterface, ActionConfiguration configuration) {
        mScheduler = new WeakReference<>(scheduler);
        mInterface = anInterface;
        mConfiguration = configuration;
    }

    @Override
    public ActionConfiguration getConfiguration() {
        return mConfiguration;
    }

    @Override
    public void setConfiguration(ActionConfiguration configuration) {
        verifyNotRunning();

        mConfiguration = configuration;
    }

    @Override
    public boolean isRunning() {
        return mFuture != null && mFuture.isRunning();
    }

    @Override
    public void start() {
        verifyNotRunning();

        Scheduler scheduler = mScheduler.get();
        if (scheduler == null) {
            throw new IllegalStateException("scheduler was garbage collected");
        }

        mFuture = scheduler.start(mInterface, mConfiguration);
    }

    @Override
    public void cancel() {
        verifyRunning();

        if (mFuture != null) {
            mFuture.cancel();
        }
    }

    @Override
    public ActionGroup andThen(ActionInterface... actions) {
        verifyNotRunning();

        Scheduler scheduler = mScheduler.get();
        if (scheduler == null) {
            throw new IllegalStateException("scheduler was garbage collected");
        }

        return scheduler.newActionGroup(ActionGroupType.SEQUENTIAL, mConfiguration)
                .add(mInterface);
    }

    @Override
    public ActionGroup alongWith(ActionInterface... actions) {
        verifyNotRunning();

        Scheduler scheduler = mScheduler.get();
        if (scheduler == null) {
            throw new IllegalStateException("scheduler was garbage collected");
        }

        return scheduler.newActionGroup(ActionGroupType.PARALLEL, mConfiguration)
                .add(mInterface);
    }

    @Override
    public ActionGroup raceWith(ActionInterface... actions) {
        verifyNotRunning();

        Scheduler scheduler = mScheduler.get();
        if (scheduler == null) {
            throw new IllegalStateException("scheduler was garbage collected");
        }

        return scheduler.newActionGroup(ActionGroupType.PARALLEL_RACE, mConfiguration)
                .add(mInterface);
    }

    private void verifyRunning() {
        if (!isRunning()) {
            throw new IllegalStateException("action not running");
        }
    }

    private void verifyNotRunning() {
        if (isRunning()) {
            throw new IllegalStateException("action running");
        }
    }
}
