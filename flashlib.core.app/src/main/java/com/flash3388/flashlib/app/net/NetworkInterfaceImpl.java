package com.flash3388.flashlib.app.net;

import com.flash3388.flashlib.app.ServiceRegistry;
import com.flash3388.flashlib.net.hfcs.HfcsRegistry;
import com.flash3388.flashlib.net.messaging.MessageType;
import com.flash3388.flashlib.net.messaging.Messenger;
import com.flash3388.flashlib.net.obsr.ObjectStorage;
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
            throw new UnsupportedOperationException();
        } else {
            mObjectStorage = null;
        }

        if (configuration.isNetworkingEnabled() && configuration.isHfcsEnabled()) {
            throw new UnsupportedOperationException();
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
        throw new UnsupportedOperationException();
    }
}
