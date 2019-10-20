package com.flash3388.flashlib.robot.scheduling.actions;

import com.flash3388.flashlib.robot.scheduling.Subsystem;
import com.flash3388.flashlib.time.Time;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public abstract class ActionBuilder<T extends ActionBuilder> {

    protected Collection<Subsystem> mRequirements;
    protected Time mTimeout;

    protected ActionBuilder() {
        mRequirements = new ArrayList<>();
        mTimeout = Time.INVALID;
    }

    public T setTimeout(Time timeout) {
        mTimeout = timeout != null ? timeout : Time.INVALID;
        return thisInstance();
    }

    public T requires(Subsystem subsystem) {
        mRequirements.add(subsystem);
        return thisInstance();
    }

    public T requires(Subsystem... subsystems) {
        mRequirements.addAll(Arrays.asList(subsystems));
        return thisInstance();
    }

    public abstract Action build();

    protected abstract T thisInstance();
}
