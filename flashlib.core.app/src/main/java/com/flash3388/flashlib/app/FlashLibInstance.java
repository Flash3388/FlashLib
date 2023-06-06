package com.flash3388.flashlib.app;

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

    public static void setControl(FlashLibControl control) {
        FlashLibControl old = sControl.getAndSet(control);
        if (old != null) {
            control.getLogger().warn("FlashLibControl global modified after initial setup");
        }
    }
}
