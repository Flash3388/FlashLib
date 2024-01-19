package com.flash3388.flashlib.net.channels;

import java.net.SocketAddress;
import java.util.Objects;

public class NetClientInfo {

    private final SocketAddress mAddress;

    public NetClientInfo(SocketAddress address) {
        mAddress = address;
    }

    public SocketAddress getAddress() {
        return mAddress;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NetClientInfo that = (NetClientInfo) o;
        return Objects.equals(mAddress, that.mAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mAddress);
    }

    @Override
    public String toString() {
        return mAddress.toString();
    }
}
