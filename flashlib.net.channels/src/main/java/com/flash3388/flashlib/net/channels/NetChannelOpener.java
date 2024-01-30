package com.flash3388.flashlib.net.channels;

import java.io.IOException;

public interface NetChannelOpener<T extends BaseChannel> {

    T open() throws IOException;
}
