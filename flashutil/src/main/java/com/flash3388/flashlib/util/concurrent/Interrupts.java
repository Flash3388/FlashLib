package com.flash3388.flashlib.util.concurrent;

public final class Interrupts {

    private Interrupts() {}

    public static void preserveInterruptState() {
        Thread.currentThread().interrupt();
    }
}
