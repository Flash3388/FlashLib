package com.flash3388.flashlib.app.net;

import com.flash3388.flashlib.app.ServiceRegistry;
import com.flash3388.flashlib.net.channels.nio.ChannelUpdater;
import com.flash3388.flashlib.net.channels.nio.ChannelUpdaterService;
import com.flash3388.flashlib.net.hfcs.HfcsRegistry;
import com.flash3388.flashlib.net.hfcs.HfcsService;
import com.flash3388.flashlib.net.messaging.ChannelId;
import com.flash3388.flashlib.net.messaging.MessageType;
import com.flash3388.flashlib.net.messaging.Messenger;
import com.flash3388.flashlib.net.messaging.MessengerService;
import com.flash3388.flashlib.net.obsr.ObjectStorage;
import com.flash3388.flashlib.net.obsr.ObsrService;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.util.FlashLibMainThread;
import com.flash3388.flashlib.util.unique.InstanceId;

import java.util.Set;

public class NetworkInterfaceImpl implements NetworkInterface {


    private final InstanceId mInstanceId;
    private final ServiceRegistry mServiceRegistry;
    private final Clock mClock;
    private final FlashLibMainThread mMainThread;

    private final NetworkingMode mMode;
    private final ObjectStorage mObjectStorage;
    private final HfcsRegistry mHfcsRegistry;
    private final ChannelUpdater mChannelUpdater;

    private int mNextMessengerId;

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

        mNextMessengerId = 0;

        ChannelUpdaterService channelUpdaterService = new ChannelUpdaterService();
        mServiceRegistry.register(channelUpdaterService);
        mChannelUpdater = channelUpdaterService;

        if (configuration.isNetworkingEnabled() && configuration.isObjectStorageEnabled()) {
            ObsrService service = new ObsrService(mInstanceId, mClock);
            if (configuration.mObsrConfiguration.isLocal) {
                service.configureLocal();
            } else if (configuration.mObsrConfiguration.isPrimaryMode) {
                service.configurePrimary(mChannelUpdater, configuration.mObsrConfiguration.serverAddress);
            } else {
                service.configureSecondary(mChannelUpdater, configuration.mObsrConfiguration.serverAddress);
            }

            mServiceRegistry.register(service);
            mObjectStorage = service;
        } else {
            mObjectStorage = null;
        }

        if (configuration.isNetworkingEnabled() && configuration.isHfcsEnabled()) {
            HfcsService service = new HfcsService(channelUpdaterService, mInstanceId, mClock);
            if (configuration.mHfcsConfiguration.isTargeted) {
                service.configureTargeted(configuration.mHfcsConfiguration.localAddress, configuration.mHfcsConfiguration.remoteAddress);
            } else {
                service.configureReplying(configuration.mHfcsConfiguration.localAddress);
            }

            mServiceRegistry.register(service);
            mHfcsRegistry = service;
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
    public Messenger newMessenger(Set<? extends MessageType> messageTypes, MessengerConfiguration configuration) {
        mMainThread.verifyCurrentThread();

        int messengerId = ++mNextMessengerId;
        ChannelId channelId = new ChannelId(mInstanceId, messengerId);

        MessengerService service = new MessengerService(mChannelUpdater, channelId, mClock);
        if (configuration.serverMode) {
            service.configureServer(configuration.serverAddress);
        } else {
            service.configureClient(configuration.serverAddress);
        }

        service.registerMessageTypes(messageTypes);
        mServiceRegistry.register(service);

        return service;
    }
}
