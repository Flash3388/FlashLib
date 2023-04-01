package com.flash3388.flashlib.app.net;

import com.flash3388.flashlib.net.hfcs.impl.HfcsServices;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketAddress;

public class NetworkConfiguration implements NetworkingMode {

    public static class ObjectStorageConfiguration {

        final boolean isEnabled;
        final boolean isPrimaryNode;
        final String primaryNodeAddress;

        private ObjectStorageConfiguration(boolean isEnabled, boolean isPrimaryNode, String primaryNodeAddress) {
            this.isEnabled = isEnabled;
            this.isPrimaryNode = isPrimaryNode;
            this.primaryNodeAddress = primaryNodeAddress;
        }

        public static ObjectStorageConfiguration disabled() {
            return new ObjectStorageConfiguration(
                    false,
                    false,
                    null);
        }

        public static ObjectStorageConfiguration primaryNode() {
            return new ObjectStorageConfiguration(
                    true,
                    true,
                    null);
        }

        public static ObjectStorageConfiguration primaryNode(String bindAddress) {
            return new ObjectStorageConfiguration(
                    true,
                    true,
                    bindAddress);
        }

        public static ObjectStorageConfiguration secondaryNode(String primaryNodeAddress) {
            return new ObjectStorageConfiguration(
                    true,
                    false,
                    primaryNodeAddress);
        }
    }

    public static class HfcsConfiguration {
        private static final int DEFAULT_PORT = HfcsServices.DEFAULT_PORT;

        final boolean isEnabled;
        final boolean replyToSenderModeEnabled;
        final boolean specificTargetModeEnabled;
        final boolean multicastModeEnabled;
        final boolean broadcastModeEnabled;
        final SocketAddress specificTargetAddress;
        final NetworkInterface multicastInterface;
        final InetAddress multicastGroup;
        final int remotePort;
        final SocketAddress bindAddress;
        final InetAddress broadcastAddress;

        private HfcsConfiguration(boolean isEnabled,
                                  boolean replyToSenderModeEnabled,
                                  boolean specificTargetModeEnabled,
                                  boolean multicastModeEnabled,
                                  boolean broadcastModeEnabled,
                                  SocketAddress specificTargetAddress,
                                  NetworkInterface multicastInterface,
                                  InetAddress multicastGroup,
                                  int remotePort,
                                  SocketAddress bindAddress,
                                  InetAddress broadcastAddress) {
            this.isEnabled = isEnabled;
            this.replyToSenderModeEnabled = replyToSenderModeEnabled;
            this.specificTargetModeEnabled = specificTargetModeEnabled;
            this.multicastModeEnabled = multicastModeEnabled;
            this.broadcastModeEnabled = broadcastModeEnabled;
            this.specificTargetAddress = specificTargetAddress;
            this.multicastInterface = multicastInterface;
            this.multicastGroup = multicastGroup;
            this.remotePort = remotePort;
            this.bindAddress = bindAddress;
            this.broadcastAddress = broadcastAddress;
        }

        public static HfcsConfiguration disabled() {
            return new HfcsConfiguration(
                    false,
                    false,
                    false,
                    false,
                    false,
                    null,
                    null,
                    null,
                    0,
                    null,
                    null);
        }

        public static HfcsConfiguration replyToSenderMode(int bindPort) {
            return new HfcsConfiguration(
                    true,
                    true,
                    false,
                    false,
                    false,
                    null,
                    null,
                    null,
                    DEFAULT_PORT,
                    new InetSocketAddress(bindPort),
                    null);
        }

        public static HfcsConfiguration replyToSenderMode() {
            return replyToSenderMode(DEFAULT_PORT);
        }

        public static HfcsConfiguration specificTargetMode(int bindPort, SocketAddress remote) {
            return new HfcsConfiguration(
                    true,
                    false,
                    true,
                    false,
                    false,
                    remote,
                    null,
                    null,
                    DEFAULT_PORT,
                    new InetSocketAddress(bindPort),
                    null);
        }

        public static HfcsConfiguration specificTargetMode(SocketAddress remote) {
            return specificTargetMode(DEFAULT_PORT, remote);
        }

        public static HfcsConfiguration multicastMode(int bindPort,
                                                      NetworkInterface networkInterface,
                                                      InetAddress group,
                                                      int remotePort) {
            return new HfcsConfiguration(
                    true,
                    false,
                    false,
                    true,
                    false,
                    null,
                    networkInterface,
                    group,
                    remotePort,
                    new InetSocketAddress(bindPort),
                    null);
        }

        public static HfcsConfiguration broadcastMode(SocketAddress bindAddress,
                                                      InetAddress broadcastAddress,
                                                      int remotePort) {
            return new HfcsConfiguration(
                    true,
                    false,
                    false,
                    false,
                    true,
                    null,
                    null,
                    null,
                    remotePort,
                    bindAddress,
                    broadcastAddress);
        }

        public static HfcsConfiguration broadcastMode(SocketAddress bindAddress,
                                                      int remotePort) {
            try {
                InetAddress address = InetAddress.getByName("255.255.255.255");
                return broadcastMode(bindAddress, address, remotePort);
            } catch (IOException e) {
                throw new Error(e);
            }
        }

        public static HfcsConfiguration broadcastMode(int bindPort, int remotePort) {
            return broadcastMode(new InetSocketAddress(bindPort), remotePort);
        }
    }

    private final boolean mEnabled;
    private final ObjectStorageConfiguration mObjectStorageConfiguration;
    private final HfcsConfiguration mHfcsConfiguration;

    private NetworkConfiguration(boolean enabled,
                                 ObjectStorageConfiguration objectStorageConfiguration,
                                 HfcsConfiguration hfcsConfiguration) {
        mEnabled = enabled;
        mObjectStorageConfiguration = objectStorageConfiguration;
        mHfcsConfiguration = hfcsConfiguration;
    }

    private NetworkConfiguration() {
        this(false, null, null);
    }

    public static NetworkConfiguration disabled() {
        return new NetworkConfiguration();
    }

    public static NetworkConfiguration enabled(ObjectStorageConfiguration objectStorageConfiguration,
                                               HfcsConfiguration hfcsConfiguration) {
        return new NetworkConfiguration(true, objectStorageConfiguration, hfcsConfiguration);
    }

    @Override
    public boolean isNetworkingEnabled() {
        return mEnabled;
    }

    @Override
    public boolean isObjectStorageEnabled() {
        return mObjectStorageConfiguration != null && mObjectStorageConfiguration.isEnabled;
    }

    @Override
    public boolean isHfcsEnabled() {
        return mHfcsConfiguration != null && mHfcsConfiguration.isEnabled;
    }

    public ObjectStorageConfiguration getObjectStorageConfiguration() {
        return mObjectStorageConfiguration;
    }

    public HfcsConfiguration getHfcsConfiguration() {
        return mHfcsConfiguration;
    }
}
