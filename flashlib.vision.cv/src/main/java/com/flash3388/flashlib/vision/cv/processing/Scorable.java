package com.flash3388.flashlib.vision.cv.processing;

import com.jmath.vectors.Vector2;

public interface Scorable {

    double getWidth();
    double getHeight();

    Vector2 getCenter();

    double score();
}
