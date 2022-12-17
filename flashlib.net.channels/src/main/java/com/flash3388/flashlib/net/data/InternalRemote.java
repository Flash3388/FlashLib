package com.flash3388.flashlib.net.data;

import com.flash3388.flashlib.net.Remote;
import com.flash3388.flashlib.time.Time;

public interface InternalRemote extends Remote {

    void updateLastSeen(Time lastSeen);
}
