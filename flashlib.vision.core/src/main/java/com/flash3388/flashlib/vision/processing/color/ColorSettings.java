package com.flash3388.flashlib.vision.processing.color;

public class ColorSettings {

    private final ColorRange mHue;
    private final ColorRange mSaturation;
    private final ColorRange mValue;

    public ColorSettings(ColorRange hue, ColorRange saturation, ColorRange value) {
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
