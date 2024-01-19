package com.flash3388.flashlib.net.channels;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.function.Predicate;

public interface NetServerChannel extends Closeable {

    interface UpdateHandler {
        void onClientConnected(NetClientInfo clientInfo);
        void onClientDisconnected(NetClientInfo clientInfo);
        void onNewData(NetClientInfo sender, ByteBuffer buffer, int amountReceived) throws IOException;
    }

    void processUpdates(UpdateHandler updateHandler) throws IOException;

    void writeToAll(ByteBuffer buffer) throws IOException;
    void writeToMatching(ByteBuffer buffer, Predicate<NetClientInfo> predicate) throws IOException;

    default void writeToAll(ByteBuffer buffer, Collection<? extends NetClientInfo> clientInfos) throws IOException {
        writeToMatching(buffer, clientInfos::contains);
    }
}
