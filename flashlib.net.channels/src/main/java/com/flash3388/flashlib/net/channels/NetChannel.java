package com.flash3388.flashlib.net.channels;

import java.io.IOException;
import java.nio.ByteBuffer;

public interface NetChannel extends BaseChannel {

    boolean isStreamed();

    IncomingData read(ByteBuffer buffer) throws IOException;
    int write(ByteBuffer buffer) throws IOException;
}
