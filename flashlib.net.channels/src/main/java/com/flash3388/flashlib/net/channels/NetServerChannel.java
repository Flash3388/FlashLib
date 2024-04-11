package com.flash3388.flashlib.net.channels;

import java.io.IOException;

public interface NetServerChannel extends BaseChannel {

    NetClient acceptNewClient() throws IOException;
}
