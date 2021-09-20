package com.flash3388.flashlib.robot.services;

import com.castle.concurrent.service.Service;
import com.castle.concurrent.service.TerminalService;
import com.castle.exceptions.ServiceException;
import com.castle.util.throwables.ThrowableChain;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicBoolean;

public class ServiceContainer implements ServiceRegistry {

    private final Set<Service> mServices;
    private final AtomicBoolean mAutoStart;

    public ServiceContainer() {
        mServices = new CopyOnWriteArraySet<>();
        mAutoStart = new AtomicBoolean(false);
    }

    @Override
    public void add(Service service) {
        if (mServices.add(service) && mAutoStart.get() &&
                !service.isRunning()) {
            try {
                service.start();
            } catch (ServiceException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void start() {
        mAutoStart.set(true);

        for (Service service : mServices) {
            if (!service.isRunning()) {
                try {
                    service.start();
                } catch (ServiceException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public void stop() {
        mAutoStart.set(false);

        ThrowableChain chain = new ThrowableChain();
        for (Service service : mServices) {
            try {
                if (service.isRunning()) {
                    service.stop();
                }

                if (service instanceof TerminalService) {
                    TerminalService terminalService = ((TerminalService) service);
                    if (!terminalService.isClosed()) {
                        terminalService.close();
                    }
                }
            } catch (Throwable t) {
                chain.chain(t);
            }
        }

        chain.throwAsRuntime();
    }
}
