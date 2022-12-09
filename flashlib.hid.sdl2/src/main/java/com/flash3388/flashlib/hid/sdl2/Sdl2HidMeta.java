package com.flash3388.flashlib.hid.sdl2;

import sdl2.JoystickType;

public class Sdl2HidMeta {

    private final JoystickType mType;
    private final int mNumAxes;
    private final int mNumButtons;
    private final int mNumHats;

    public Sdl2HidMeta(JoystickType type, int numAxes, int numButtons, int numHats) {
        mType = type;
        mNumAxes = numAxes;
        mNumButtons = numButtons;
        mNumHats = numHats;
    }

    public JoystickType getType() {
        return mType;
    }

    public int getNumAxes() {
        return mNumAxes;
    }

    public int getNumButtons() {
        return mNumButtons;
    }

    public int getNumHats() {
        return mNumHats;
    }
}
