package com.flash3388.flashlib.vision.detection;

import java.util.Collection;
import java.util.Map;

public interface ObjectTracker<T extends Target> {

    Map<Integer, ? extends T> updateTracked(Collection<? extends T> objects);
}
