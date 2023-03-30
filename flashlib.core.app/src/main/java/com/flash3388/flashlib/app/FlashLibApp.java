package com.flash3388.flashlib.app;

/**
 * A FlashLib application is the main execution point of an application
 * utilizing FlashLib.
 *
 * @since FlashLib 3.2.0
 */
public interface FlashLibApp {

    /**
     * Called when the application is at an initialization phase, i.e. the application
     * should initialize its components here.
     *
     * @param control FlashLib control components
     * @throws StartupException if an error occurs during initialization making running impossible.
     */
    void initialize(FlashLibControl control) throws StartupException;

    /**
     * The main phase of the application. Apply the main logic here. Once the application
     * is finished, this method should return.
     *
     * @param control FlashLib control components
     * @throws Exception an error occurs in the application, aborting run
     */
    void main(FlashLibControl control) throws Exception;

    /**
     * The end phase of a FlashLib application. De-initialize and destruct any resources here.
     * Called when the application has finished either due to an error or not.
     *
     * @param control FlashLib control components
     * @throws Exception an error occurs in the shutdown of the application. This does not abort the shutdown.
     */
    void shutdown(FlashLibControl control) throws Exception;
}
