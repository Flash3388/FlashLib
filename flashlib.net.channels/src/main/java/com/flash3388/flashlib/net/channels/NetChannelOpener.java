package com.flash3388.flashlib.net.channels;

import java.io.IOException;

public interface NetChannelOpener<T extends BaseChannel> {

    boolean isTargetChannelStreaming();

    T open() throws IOException;
}
