package com.flash3388.flashlib.app;

import com.castle.concurrent.service.Service;

/**
 *
 * A registry holding and managing {@link Service services} used by the application.
 * These services will be automatically started and stopped with the application flow.
 *
 * @since FlashLib 3.2.0
 */
public interface ServiceRegistry {

    /**
     * Registers a service for management.
     * This service will be start automatically with the application, or immediately if the application
     * is already running.
     *
     * @param service service
     */
    void register(Service service);

    void startAll();
    void stopAll();
}
