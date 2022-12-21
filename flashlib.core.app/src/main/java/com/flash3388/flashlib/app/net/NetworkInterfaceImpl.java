package com.flash3388.flashlib.app.net;

import com.flash3388.flashlib.app.ServiceRegistry;
import com.flash3388.flashlib.app.net.NetworkConfiguration;
import com.flash3388.flashlib.app.net.NetworkInterface;
import com.flash3388.flashlib.app.net.NetworkingMode;
import com.flash3388.flashlib.net.obsr.ObjectStorage;
import com.flash3388.flashlib.net.obsr.impl.ObsrPrimaryNodeService;
import com.flash3388.flashlib.net.obsr.impl.ObsrSecondaryNodeService;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.util.logging.Logging;
import com.flash3388.flashlib.util.unique.InstanceId;
import org.slf4j.Logger;

public class NetworkInterfaceImpl implements NetworkInterface {

    private final NetworkingMode mMode;
    private final ObjectStorage mObjectStorage;

    public NetworkInterfaceImpl(NetworkConfiguration configuration,
                                InstanceId instanceId,
                                ServiceRegistry serviceRegistry,
                                Clock clock,
                                Logger logger) {
        mMode = configuration;

        if (configuration.isNetworkingEnabled() && configuration.isObjectStorageEnabled()) {
            NetworkConfiguration.ObjectStorageConfiguration objectStorageConfiguration =
                    configuration.getObjectStorageConfiguration();
            if (objectStorageConfiguration.isPrimaryNode) {
                ObsrPrimaryNodeService obsrPrimaryNodeService = new ObsrPrimaryNodeService(
                        instanceId, clock, logger);
                serviceRegistry.register(obsrPrimaryNodeService);
                mObjectStorage = obsrPrimaryNodeService;
            } else {
                ObsrSecondaryNodeService obsrSecondaryNodeService = new ObsrSecondaryNodeService(
                        instanceId, objectStorageConfiguration.primaryNodeAddress, clock, logger);
                serviceRegistry.register(obsrSecondaryNodeService);
                mObjectStorage = obsrSecondaryNodeService;
            }
        } else {
            mObjectStorage = null;
        }
    }

    public NetworkInterfaceImpl() {
        this(NetworkConfiguration.disabled(), null, null, null, Logging.stub());
    }

    @Override
    public NetworkingMode getMode() {
        return mMode;
    }

    @Override
    public ObjectStorage getObjectStorage() {
        if (mObjectStorage == null) {
            throw new IllegalStateException("ObjectStorage disabled");
        }

        return mObjectStorage;
    }
}
