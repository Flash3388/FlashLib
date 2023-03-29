package com.flash3388.flashlib.app.net;

import com.flash3388.flashlib.app.ServiceRegistry;
import com.flash3388.flashlib.net.hfcs.HfcsRegistry;
import com.flash3388.flashlib.net.hfcs.impl.HfcsAutoReplyService;
import com.flash3388.flashlib.net.hfcs.impl.HfcsMulticastService;
import com.flash3388.flashlib.net.hfcs.impl.HfcsUnicastService;
import com.flash3388.flashlib.net.obsr.ObjectStorage;
import com.flash3388.flashlib.net.obsr.impl.ObsrPrimaryNodeService;
import com.flash3388.flashlib.net.obsr.impl.ObsrSecondaryNodeService;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.util.unique.InstanceId;

import java.net.InetAddress;
import java.net.SocketAddress;

public class NetworkInterfaceImpl implements NetworkInterface {

    private final NetworkingMode mMode;
    private final ObjectStorage mObjectStorage;
    private final HfcsRegistry mHfcsRegistry;

    public NetworkInterfaceImpl(NetworkConfiguration configuration,
                                InstanceId instanceId,
                                ServiceRegistry serviceRegistry,
                                Clock clock) {
        mMode = configuration;

        if (configuration.isNetworkingEnabled() && configuration.isObjectStorageEnabled()) {
            NetworkConfiguration.ObjectStorageConfiguration objectStorageConfiguration =
                    configuration.getObjectStorageConfiguration();
            if (objectStorageConfiguration.isPrimaryNode) {
                ObsrPrimaryNodeService obsrPrimaryNodeService = new ObsrPrimaryNodeService(
                        instanceId, clock);
                serviceRegistry.register(obsrPrimaryNodeService);
                mObjectStorage = obsrPrimaryNodeService;
            } else {
                ObsrSecondaryNodeService obsrSecondaryNodeService = new ObsrSecondaryNodeService(
                        instanceId, objectStorageConfiguration.primaryNodeAddress, clock);
                serviceRegistry.register(obsrSecondaryNodeService);
                mObjectStorage = obsrSecondaryNodeService;
            }
        } else {
            mObjectStorage = null;
        }

        if (configuration.isNetworkingEnabled() && configuration.isHfcsEnabled()) {
            if (configuration.getHfcsConfiguration().replyToSenderModeEnabled) {
                HfcsAutoReplyService hfcsService;
                int bindPort = configuration.getHfcsConfiguration().bindPort;
                if (bindPort == NetworkConfiguration.HfcsConfiguration.INVALID_PORT) {
                    hfcsService = new HfcsAutoReplyService(instanceId, clock);
                } else {
                    hfcsService = new HfcsAutoReplyService(instanceId, clock, bindPort);
                }

                serviceRegistry.register(hfcsService);
                mHfcsRegistry = hfcsService;
            } else if (configuration.getHfcsConfiguration().specificTargetModeEnabled) {
                HfcsUnicastService hfcsService;
                int bindPort = configuration.getHfcsConfiguration().bindPort;
                SocketAddress remote = configuration.getHfcsConfiguration().specificTargetAddress;
                if (bindPort == NetworkConfiguration.HfcsConfiguration.INVALID_PORT) {
                    hfcsService = new HfcsUnicastService(instanceId, clock, remote);
                } else {
                    hfcsService = new HfcsUnicastService(instanceId, clock, bindPort, remote);
                }

                serviceRegistry.register(hfcsService);
                mHfcsRegistry = hfcsService;
            } else if (configuration.getHfcsConfiguration().multicastModeEnabled) {
                HfcsMulticastService hfcsService;
                int bindPort = configuration.getHfcsConfiguration().bindPort;
                java.net.NetworkInterface networkInterface = configuration.getHfcsConfiguration().multicastInterface;
                InetAddress group = configuration.getHfcsConfiguration().multicastGroup;
                int remotePort = configuration.getHfcsConfiguration().remotePort;
                if (bindPort == NetworkConfiguration.HfcsConfiguration.INVALID_PORT) {
                    hfcsService = new HfcsMulticastService(instanceId, clock, remotePort, networkInterface, group);
                } else {
                    hfcsService = new HfcsMulticastService(instanceId, clock, bindPort, remotePort, networkInterface, group);
                }

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
        this(NetworkConfiguration.disabled(), null, null, null);
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
