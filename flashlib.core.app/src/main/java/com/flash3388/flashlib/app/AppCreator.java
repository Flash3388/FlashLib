package com.flash3388.flashlib.app;

import com.flash3388.flashlib.util.resources.ResourceHolder;
import com.flash3388.flashlib.util.unique.InstanceId;

public interface AppCreator {

    AppImplementation create(InstanceId instanceId, ResourceHolder holder) throws StartupException;
}
