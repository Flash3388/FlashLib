package com.flash3388.flashlib.app;

import com.flash3388.flashlib.util.resources.ResourceHolder;
import com.flash3388.flashlib.util.unique.InstanceId;
import org.slf4j.Logger;

public interface AppCreator {

    AppImplementation create(InstanceId instanceId, ResourceHolder holder, Logger logger) throws StartupException;
}
