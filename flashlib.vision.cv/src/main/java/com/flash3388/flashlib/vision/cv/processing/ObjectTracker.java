package com.flash3388.flashlib.vision.cv.processing;

import java.util.Collection;
import java.util.Map;

public interface ObjectTracker {

    Map<Integer, ? extends Scorable> updateTracked(Collection<? extends Scorable> objects);
}
