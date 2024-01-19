package com.flash3388.flashlib.net.channels;

import com.castle.time.exceptions.TimeoutException;
import com.flash3388.flashlib.time.Time;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.function.Predicate;

public interface NetServerChannel extends Closeable {

    ServerUpdate readNextUpdate(Time timeout) throws IOException, TimeoutException;

    NetClient acceptNewClient() throws IOException;
}
