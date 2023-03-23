package com.flash3388.flashlib.app;

import com.flash3388.flashlib.global.GlobalDependencies;

import java.util.concurrent.atomic.AtomicReference;

public class FlashLibInstance {

    private FlashLibInstance() {}

    private final static AtomicReference<FlashLibControl> sControl = new AtomicReference<>();

    public static FlashLibControl getControl() {
        FlashLibControl control = sControl.get();
        if (control == null) {
            throw new IllegalStateException("FlashLibControl not yet initialized");
        }

        return control;
    }

    static void setControl(FlashLibControl control) {
        FlashLibControl old = sControl.getAndSet(control);
        GlobalDependencies.setClockInstance(control.getClock());
        GlobalDependencies.setLoggerInstance(control.getLogger());

        if (old != null) {
            control.getLogger().warn("FlashLibControl global modified after initial setup");
        }
    }
}
