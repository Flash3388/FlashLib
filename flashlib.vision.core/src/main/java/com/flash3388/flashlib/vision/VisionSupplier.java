package com.flash3388.flashlib.vision;

import com.flash3388.flashlib.time.Time;

import java.util.Optional;

public interface VisionSupplier {

    Optional<VisionResult> getLatestResult();
    Optional<VisionResult> getLatestResult(Time maxTimestamp);
}
