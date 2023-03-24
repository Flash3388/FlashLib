package com.flash3388.flashlib.app;

/**
 * A simplified flow for a FlashLib application.
 *
 * @since FlashLib 3.2.0
 */
public interface SimpleApp extends AutoCloseable {

    interface Creator {
        SimpleApp create(FlashLibControl control) throws StartupException;
    }

    /**
     * The main phase of the application.
     *
     * @throws Exception if an error has occurred which requires the application to stop execution.
     */
    void main() throws Exception;
}
