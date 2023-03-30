package com.flash3388.flashlib.util;

import com.flash3388.flashlib.annotations.MainThreadOnly;

/**
 * Represents the main thread of the application.
 *
 * @since FlashLib 3.2.0
 */
public interface FlashLibMainThread {

    boolean isCurrentThread();
    void verifyCurrentThread();

    void runOnThisThread(Runnable runnable);
    @MainThreadOnly
    void executePendingTasks();

    class Stub implements FlashLibMainThread {

        @Override
        public boolean isCurrentThread() {
            return true;
        }

        @Override
        public void verifyCurrentThread() {

        }

        @Override
        public void runOnThisThread(Runnable runnable) {

        }

        @Override
        public void executePendingTasks() {

        }
    }
}
