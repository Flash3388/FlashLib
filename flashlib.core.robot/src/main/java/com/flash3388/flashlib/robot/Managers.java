package com.flash3388.flashlib.robot;

import com.flash3388.flashlib.robot.services.ServiceRegistry;
import com.flash3388.flashlib.util.resources.ResourceHolder;

public class Managers {

    private final ResourceHolder mResourceHolder;
    private final ServiceRegistry mServiceRegistry;

    public Managers(ResourceHolder resourceHolder, ServiceRegistry serviceRegistry) {
        mResourceHolder = resourceHolder;
        mServiceRegistry = serviceRegistry;
    }

    public ResourceHolder getResourceHolder() {
        return mResourceHolder;
    }

    public ServiceRegistry getServiceRegistry() {
        return mServiceRegistry;
    }
}
