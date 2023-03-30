package com.flash3388.flashlib.app.net;

import com.flash3388.flashlib.app.ServiceRegistry;
import com.flash3388.flashlib.net.channels.messsaging.KnownMessageTypes;
import com.flash3388.flashlib.net.channels.tcp.TcpRoutingService;
import com.flash3388.flashlib.net.hfcs.HfcsRegistry;
import com.flash3388.flashlib.net.hfcs.impl.HfcsServiceBase;
import com.flash3388.flashlib.net.hfcs.impl.HfcsServices;
import com.flash3388.flashlib.net.messaging.Messenger;
import com.flash3388.flashlib.net.messaging.MessengerService;
import com.flash3388.flashlib.net.obsr.ObjectStorage;
import com.flash3388.flashlib.net.obsr.impl.ObsrPrimaryNodeService;
import com.flash3388.flashlib.net.obsr.impl.ObsrSecondaryNodeService;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.util.FlashLibMainThread;
import com.flash3388.flashlib.util.unique.InstanceId;

import java.net.InetAddress;
import java.net.SocketAddress;

public class NetworkInterfaceImpl implements NetworkInterface {


    private final InstanceId mInstanceId;
    private final ServiceRegistry mServiceRegistry;
    private final Clock mClock;
    private final FlashLibMainThread mMainThread;

    private final NetworkingMode mMode;
    private final ObjectStorage mObjectStorage;
    private final HfcsRegistry mHfcsRegistry;

    public NetworkInterfaceImpl(NetworkConfiguration configuration,
                                InstanceId instanceId,
                                ServiceRegistry serviceRegistry,
                                Clock clock,
                                FlashLibMainThread mainThread) {
        mMode = configuration;
        mInstanceId = instanceId;
        mServiceRegistry = serviceRegistry;
        mClock = clock;
        mMainThread = mainThread;

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
                HfcsServiceBase hfcsService;
                int bindPort = configuration.getHfcsConfiguration().bindPort;
                if (bindPort == NetworkConfiguration.HfcsConfiguration.INVALID_PORT) {
                    bindPort = HfcsServices.DEFAULT_PORT;
                }

                hfcsService = HfcsServices.autoReplyTarget(instanceId, clock, bindPort);

                serviceRegistry.register(hfcsService);
                mHfcsRegistry = hfcsService;
            } else if (configuration.getHfcsConfiguration().specificTargetModeEnabled) {
                HfcsServiceBase hfcsService;
                int bindPort = configuration.getHfcsConfiguration().bindPort;
                SocketAddress remote = configuration.getHfcsConfiguration().specificTargetAddress;
                if (bindPort == NetworkConfiguration.HfcsConfiguration.INVALID_PORT) {
                    bindPort = HfcsServices.DEFAULT_PORT;
                }

                hfcsService = HfcsServices.unicast(instanceId, clock, bindPort, remote);

                serviceRegistry.register(hfcsService);
                mHfcsRegistry = hfcsService;
            } else if (configuration.getHfcsConfiguration().multicastModeEnabled) {
                HfcsServiceBase hfcsService;
                int bindPort = configuration.getHfcsConfiguration().bindPort;
                java.net.NetworkInterface networkInterface = configuration.getHfcsConfiguration().multicastInterface;
                InetAddress group = configuration.getHfcsConfiguration().multicastGroup;
                int remotePort = configuration.getHfcsConfiguration().remotePort;
                if (bindPort == NetworkConfiguration.HfcsConfiguration.INVALID_PORT) {
                    bindPort = HfcsServices.DEFAULT_PORT;
                }

                hfcsService = HfcsServices.multicast(instanceId, clock, bindPort, remotePort, networkInterface, group);

                serviceRegistry.register(hfcsService);
                mHfcsRegistry = hfcsService;
            } else if (configuration.getHfcsConfiguration().broadcastModeEnabled) {
                HfcsServiceBase hfcsService;
                int bindPort = configuration.getHfcsConfiguration().bindPort;
                int remotePort = configuration.getHfcsConfiguration().remotePort;
                if (bindPort == NetworkConfiguration.HfcsConfiguration.INVALID_PORT) {
                    bindPort = HfcsServices.DEFAULT_PORT;
                }

                hfcsService = HfcsServices.broadcast(instanceId, clock, bindPort, remotePort);

                serviceRegistry.register(hfcsService);
                mHfcsRegistry = hfcsService;
            } else {
                throw new UnsupportedOperationException("HFCS mode not supported");
            }
        } else {
            mHfcsRegistry = null;
        }
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

    @Override
    public Messenger newMessenger(KnownMessageTypes messageTypes, MessengerConfiguration configuration) {
        mMainThread.verifyCurrentThread();

        if (configuration.serverMode) {
            TcpRoutingService routingService = new TcpRoutingService(configuration.serverAddress);
            mServiceRegistry.register(routingService);
        }

        MessengerService service = new MessengerService(mInstanceId, mClock, messageTypes, configuration.serverAddress);
        mServiceRegistry.register(service);
        return service;
    }
}
