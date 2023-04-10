package com.flash3388.flashlib.vision.detection;

import com.jmath.vectors.Vector2;

public interface Target {

    Vector2 getCenter();

    int getWidthPixels();
    int getHeightPixels();
}
