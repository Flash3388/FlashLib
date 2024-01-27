package com.flash3388.flashlib.net.channels;

import com.castle.time.exceptions.TimeoutException;
import com.flash3388.flashlib.time.Time;

import java.io.Closeable;
import java.io.IOException;

public interface NetServerChannel extends Closeable {

    interface Update {

        enum UpdateType {
            NONE,
            NEW_CLIENT,
            NEW_DATA
        }

        UpdateType getType();
        NetClient getUpdatedClient();

        void done();
    }

    Update readNextUpdate(Time timeout) throws IOException, TimeoutException;

    NetClient acceptNewClient() throws IOException;
}
