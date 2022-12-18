package com.flash3388.flashlib.net.message;

import com.flash3388.flashlib.time.Time;
import com.flash3388.flashlib.util.unique.InstanceId;

public interface MessageInfo {

    InstanceId getSender();
    Time getTimestamp();
}
