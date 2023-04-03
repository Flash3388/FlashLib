package com.flash3388.flashlib.vision;

import com.flash3388.flashlib.time.Time;

public interface SourcePollingObserver {
    void onStartProcess();
    void onEndProcess(Time runTime);
    void onErroredProcess(Throwable t);
}
