package com.flash3388.flashlib.app;

import com.castle.concurrent.service.Service;
import com.castle.concurrent.service.TerminalService;
import com.flash3388.flashlib.util.FlashLibMainThread;
import com.flash3388.flashlib.util.logging.Logging;
import org.slf4j.Logger;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicBoolean;

public class BasicServiceRegistry implements ServiceRegistry {
    
    private static final Logger LOGGER = Logging.getLogger("ServiceRegistry");

    private final FlashLibMainThread mMainThread;
    private final Deque<Service> mServices;
    private final AtomicBoolean mHasStarted;

    public BasicServiceRegistry(FlashLibMainThread mainThread) {
        mMainThread = mainThread;
        mServices = new ConcurrentLinkedDeque<>();
        mHasStarted = new AtomicBoolean(false);
    }

    @Override
    public void register(Service service) {
        mServices.add(service);

        if (mHasStarted.get()) {
            startService(service);
        }
    }

    @Override
    public void startAll() {
        mMainThread.verifyCurrentThread();

        mHasStarted.set(true);
        for (Service service : mServices) {
            startService(service);
        }
    }

    @Override
    public void stopAll() {
        mMainThread.verifyCurrentThread();

        mHasStarted.set(false);
        for (Service service : mServices) {
            try {
                if (service.isRunning()) {
                    LOGGER.debug("Stopping service {}", service);
                    service.stop();
                }

                if (service instanceof TerminalService) {
                    TerminalService terminalService = ((TerminalService) service);
                    if (!terminalService.isClosed()) {
                        LOGGER.debug("Service {} is TerminalService, closing service", service);
                        terminalService.close();
                    }
                }
            } catch (Throwable t) {
                LOGGER.warn("Failed to stop service {}", service);
                LOGGER.error("Failed to stop service: exception", t);
            }
        }
    }

    private void startService(Service service) {
        try {
            if (!service.isRunning()) {
                LOGGER.debug("Starting service {}", service);
                service.start();
            }
        } catch (Throwable t) {
            LOGGER.warn("Failed to start service {}", service);
            LOGGER.error("Failed to start service: exception", t);
        }
    }
}
