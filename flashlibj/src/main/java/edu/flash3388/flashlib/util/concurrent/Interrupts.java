package edu.flash3388.flashlib.util.concurrent;

public class Interrupts {

    private Interrupts() {}

    public static void preserveInterruptState() {
        Thread.currentThread().interrupt();
    }
}
