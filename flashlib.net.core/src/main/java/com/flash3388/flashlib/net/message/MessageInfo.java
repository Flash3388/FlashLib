package com.flash3388.flashlib.net.message;

import com.flash3388.flashlib.net.Remote;
import com.flash3388.flashlib.time.Time;

public interface MessageInfo {

    Remote getSender();
    Time getTimestamp();
}
