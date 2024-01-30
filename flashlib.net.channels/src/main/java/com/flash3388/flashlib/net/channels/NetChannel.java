package com.flash3388.flashlib.net.channels;

import java.io.IOException;
import java.nio.ByteBuffer;

public interface NetChannel extends BaseChannel {

    IncomingData read(ByteBuffer buffer) throws IOException;
    void write(ByteBuffer buffer) throws IOException;
}
