package com.flash3388.flashlib.net.channels.blocking;

import com.castle.time.exceptions.TimeoutException;
import com.flash3388.flashlib.time.Time;

import java.io.IOException;

public interface Blocking<T extends Enum<T>, D> {

    interface Update<T extends Enum<T>, D> {
        boolean isEmpty();

        T getType();
        D getData();

        void done();
    }

    Update<T, D> readNextUpdate(Time timeout) throws IOException, TimeoutException;
}
