package com.flash3388.flashlib.app;

import com.castle.concurrent.service.Service;

public interface ServiceRegistry {

    void register(Service service);

    void startAll();
    void stopAll();
}
