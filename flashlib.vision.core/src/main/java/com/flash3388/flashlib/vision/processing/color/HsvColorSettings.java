package com.flash3388.flashlib.vision.processing.color;

public class HsvColorSettings {

    private final ColorRange mHue;
    private final ColorRange mSaturation;
    private final ColorRange mValue;

    public HsvColorSettings(ColorRange hue, ColorRange saturation, ColorRange value) {
        mHue = hue;
        mSaturation = saturation;
        mValue = value;
    }

    public ColorRange getHue() {
        return mHue;
    }

    public ColorRange getSaturation() {
        return mSaturation;
    }

    public ColorRange getValue() {
        return mValue;
    }
}
