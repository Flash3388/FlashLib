package com.flash3388.flashlib.app.net;

import com.flash3388.flashlib.app.ServiceRegistry;
import com.flash3388.flashlib.net.channels.tcp.TcpRoutingService;
import com.flash3388.flashlib.net.hfcs.HfcsRegistry;
import com.flash3388.flashlib.net.hfcs.impl.HfcsServiceBase;
import com.flash3388.flashlib.net.messaging.KnownMessageTypes;
import com.flash3388.flashlib.net.messaging.Messenger;
import com.flash3388.flashlib.net.messaging.MessengerService;
import com.flash3388.flashlib.net.obsr.ObjectStorage;
import com.flash3388.flashlib.net.obsr.impl.ObsrNodeServiceBase;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.util.FlashLibMainThread;
import com.flash3388.flashlib.util.unique.InstanceId;

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
            try {
                ObsrConfiguration.Creator creator = configuration.getObsrConfiguration().creator;
                assert creator != null;

                ObsrNodeServiceBase service = creator.create(instanceId, clock);
                serviceRegistry.register(service);

                mObjectStorage = service;
            } catch (Exception e) {
                throw new Error(e);
            }
        } else {
            mObjectStorage = null;
        }

        if (configuration.isNetworkingEnabled() && configuration.isHfcsEnabled()) {
            try {
                HfcsConfiguration.Creator creator = configuration.getHfcsConfiguration().creator;
                assert creator != null;

                HfcsServiceBase service = creator.create(instanceId, clock);
                serviceRegistry.register(service);

                mHfcsRegistry = service;
            } catch (Exception e) {
                throw new Error(e);
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
