package com.flash3388.flashlib.vision.cv.processing;

import com.jmath.vectors.Vector2;

public interface Scorable extends Comparable<Scorable> {

    double getWidth();
    double getHeight();

    Vector2 getCenter();

    double score();

    @Override
    default int compareTo(Scorable o) {
        return Double.compare(score(), o.score());
    }
}
