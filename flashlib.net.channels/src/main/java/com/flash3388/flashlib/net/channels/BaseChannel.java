package com.flash3388.flashlib.net.channels;

import com.flash3388.flashlib.net.channels.nio.ChannelListener;
import com.flash3388.flashlib.net.channels.nio.ChannelUpdater;
import com.flash3388.flashlib.net.channels.nio.UpdateRegistration;

import java.io.Closeable;
import java.io.IOException;

public interface BaseChannel extends Closeable {

    UpdateRegistration register(ChannelUpdater updater, ChannelListener listener) throws IOException;
}
