package com.flash3388.flashlib.app;

import com.castle.concurrent.service.Service;
import com.castle.concurrent.service.TerminalService;
import org.slf4j.Logger;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicBoolean;

public class BasicServiceRegistry implements ServiceRegistry {

    private final Logger mLogger;

    private final Deque<Service> mServices;
    private final AtomicBoolean mHasStarted;

    public BasicServiceRegistry(Logger logger) {
        mLogger = logger;
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
        if (mHasStarted.compareAndSet(false, true)) {
            for (Service service : mServices) {
                startService(service);
            }
        }
    }

    @Override
    public void stopAll() {
        if (mHasStarted.compareAndSet(true, false)) {
            for (Service service : mServices) {
                try {
                    if (service.isRunning()) {
                        mLogger.debug("Stopping service {}", service);
                        service.stop();
                    }

                    if (service instanceof TerminalService) {
                        TerminalService terminalService = ((TerminalService) service);
                        if (!terminalService.isClosed()) {
                            mLogger.debug("Service {} is TerminalService, closing service", service);
                            terminalService.close();
                        }
                    }
                } catch (Throwable t) {
                    mLogger.warn("Failed to stop service {}", service);
                    mLogger.error("Failed to stop service: exception", t);
                }
            }
        }
    }

    private void startService(Service service) {
        try {
            if (!service.isRunning()) {
                mLogger.debug("Starting service {}", service);
                service.start();
            }
        } catch (Throwable t) {
            mLogger.warn("Failed to start service {}", service);
            mLogger.error("Failed to start service: exception", t);
        }
    }
}
