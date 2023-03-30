package com.flash3388.flashlib.net.channels;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;

public interface NetServerChannel extends Closeable {

    interface UpdateHandler {
        void onClientConnected(NetClientInfo clientInfo);
        void onClientDisconnected(NetClientInfo clientInfo);
        void onNewData(NetClientInfo sender, ByteBuffer buffer, int amountReceived);
    }

    void processUpdates() throws IOException;

    void writeToAll(ByteBuffer buffer) throws IOException;
    void writeToOne(ByteBuffer buffer, NetClientInfo clientInfo) throws IOException;
    void writeToAllBut(ByteBuffer buffer, NetClientInfo clientToSkip) throws IOException;
}
