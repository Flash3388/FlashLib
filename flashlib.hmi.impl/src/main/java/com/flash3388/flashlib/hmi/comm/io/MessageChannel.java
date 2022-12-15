package com.flash3388.flashlib.hmi.comm.io;

import com.flash3388.flashlib.hmi.comm.BasicMessage;
import com.flash3388.flashlib.time.Time;

import java.io.IOException;

public interface MessageChannel {

    void writeMessage(BasicMessage message) throws IOException;
    BasicMessage readMessage(Time readTimeout) throws IOException;
}
