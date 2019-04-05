package com.flash3388.flashlib.io;

/**
 * Indicates when the resources should be closed.
 *
 * @author Tom Tzook
 * @since FlashLib 1.3.0
 */
public enum CloseOption {
    /**
     * Close the resources when the callable finishes, whether or not it has succeeded.
     */
    CLOSE_ALWAYS {
        @Override
        boolean shouldClose(boolean hasErrorOccurred) {
            return true;
        }
    },
    /**
     * Closes the resources only if the callable has thrown an exception.
     */
    CLOSE_ON_ERROR {
        @Override
        boolean shouldClose(boolean hasErrorOccurred) {
            return hasErrorOccurred;
        }
    },
    /**
     * Closes the resources only if the callable has finished successfully, i.e. no error was thrown.
     */
    CLOSE_ON_SUCCESS {
        @Override
        boolean shouldClose(boolean hasErrorOccurred) {
            return !hasErrorOccurred;
        }
    };

    abstract boolean shouldClose(boolean hasErrorOccurred);
}
