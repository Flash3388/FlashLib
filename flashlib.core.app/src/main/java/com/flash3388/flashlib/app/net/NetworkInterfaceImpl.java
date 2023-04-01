package com.flash3388.flashlib.app.net;

import com.flash3388.flashlib.app.ServiceRegistry;
import com.flash3388.flashlib.net.messaging.KnownMessageTypes;
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
                ObsrPrimaryNodeService obsrPrimaryNodeService;
                String bindAddress = objectStorageConfiguration.primaryNodeAddress;
                if (bindAddress == null) {
                    obsrPrimaryNodeService = new ObsrPrimaryNodeService(instanceId, clock);
                } else {
                    obsrPrimaryNodeService = new ObsrPrimaryNodeService(instanceId, clock, bindAddress);
                }
                serviceRegistry.register(obsrPrimaryNodeService);
                mObjectStorage = obsrPrimaryNodeService;
            } else {
                ObsrSecondaryNodeService obsrSecondaryNodeService = new ObsrSecondaryNodeService(
                        instanceId, clock,
                        objectStorageConfiguration.primaryNodeAddress);
                serviceRegistry.register(obsrSecondaryNodeService);
                mObjectStorage = obsrSecondaryNodeService;
            }
        } else {
            mObjectStorage = null;
        }

        if (configuration.isNetworkingEnabled() && configuration.isHfcsEnabled()) {
            if (configuration.getHfcsConfiguration().replyToSenderModeEnabled) {
                HfcsServiceBase hfcsService;

                SocketAddress bindAddress = configuration.getHfcsConfiguration().bindAddress;

                hfcsService = HfcsServices.autoReplyTarget(instanceId, clock, bindAddress);

                serviceRegistry.register(hfcsService);
                mHfcsRegistry = hfcsService;
            } else if (configuration.getHfcsConfiguration().specificTargetModeEnabled) {
                HfcsServiceBase hfcsService;

                SocketAddress remote = configuration.getHfcsConfiguration().specificTargetAddress;
                SocketAddress bindAddress = configuration.getHfcsConfiguration().bindAddress;

                hfcsService = HfcsServices.unicast(instanceId, clock, bindAddress, remote);

                serviceRegistry.register(hfcsService);
                mHfcsRegistry = hfcsService;
            } else if (configuration.getHfcsConfiguration().multicastModeEnabled) {
                HfcsServiceBase hfcsService;

                java.net.NetworkInterface networkInterface = configuration.getHfcsConfiguration().multicastInterface;
                InetAddress group = configuration.getHfcsConfiguration().multicastGroup;
                int remotePort = configuration.getHfcsConfiguration().remotePort;
                SocketAddress bindAddress = configuration.getHfcsConfiguration().bindAddress;

                hfcsService = HfcsServices.multicast(instanceId, clock, bindAddress, remotePort, networkInterface, group);

                serviceRegistry.register(hfcsService);
                mHfcsRegistry = hfcsService;
            } else if (configuration.getHfcsConfiguration().broadcastModeEnabled) {
                HfcsServiceBase hfcsService;

                InetAddress broadcastAddress = configuration.getHfcsConfiguration().broadcastAddress;
                int remotePort = configuration.getHfcsConfiguration().remotePort;
                SocketAddress bindAddress = configuration.getHfcsConfiguration().bindAddress;

                hfcsService = HfcsServices.broadcast(instanceId, clock, bindAddress, broadcastAddress, remotePort);

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
