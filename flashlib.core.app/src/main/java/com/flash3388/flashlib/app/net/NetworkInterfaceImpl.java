package com.flash3388.flashlib.app.net;

import com.flash3388.flashlib.app.ServiceRegistry;
import com.flash3388.flashlib.net.hfcs.HfcsRegistry;
import com.flash3388.flashlib.net.hfcs.impl.HfcsWideService;
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
    private final HfcsRegistry mHfcsRegistry;

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

        if (configuration.isNetworkingEnabled() && configuration.isHfcsEnabled()) {
            if (configuration.getHfcsConfiguration().broadcastModeEnabled) {
                HfcsWideService hfcsService = new HfcsWideService(
                        HfcsWideService.ChannelType.BROADCAST,
                        instanceId, clock, logger);
                serviceRegistry.register(hfcsService);
                mHfcsRegistry = hfcsService;
            } else if (configuration.getHfcsConfiguration().replyToSenderModeEnabled) {
                HfcsWideService hfcsService = new HfcsWideService(
                        HfcsWideService.ChannelType.AUTO_REPLY,
                        instanceId, clock, logger);
                serviceRegistry.register(hfcsService);
                mHfcsRegistry = hfcsService;
            } else {
                throw new UnsupportedOperationException("HFCS mode not supported");
            }
        } else {
            mHfcsRegistry = null;
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

    @Override
    public HfcsRegistry getHfcsRegistry() {
        if (mHfcsRegistry == null) {
            throw new IllegalStateException("HFCS disabled");
        }

        return mHfcsRegistry;
    }
}
