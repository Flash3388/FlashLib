package com.flash3388.flashlib.net.robolink;

import com.flash3388.flashlib.time.Time;

public interface Remote {

    String getId();

    Time getLastSeenTimestamp();
}
