package com.flash3388.flashlib.vision.cv.detection;

import com.flash3388.flashlib.vision.detection.Target;
import com.jmath.vectors.Vector2;
import org.opencv.core.Rect;

public class RectTarget implements Target {
    protected final Rect rect;

    public RectTarget(Rect rect) {
        this.rect = rect;
    }

    @Override
    public Vector2 getCenter() {
        return new Vector2(
                rect.x + rect.width * 0.5,
                rect.y + rect.height * 0.5
        );
    }

    @Override
    public int getWidthPixels() {
        return rect.width;
    }

    @Override
    public int getHeightPixels() {
        return rect.height;
    }
}
