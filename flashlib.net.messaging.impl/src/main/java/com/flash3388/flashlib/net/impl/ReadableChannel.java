package com.flash3388.flashlib.net.impl;

import java.io.IOException;
import java.nio.ByteBuffer;

public interface ReadableChannel {

    int getIdentifier();

    int read(ByteBuffer buffer) throws IOException;
}
