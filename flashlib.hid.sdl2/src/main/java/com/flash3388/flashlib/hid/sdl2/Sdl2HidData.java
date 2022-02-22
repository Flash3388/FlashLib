package com.flash3388.flashlib.hid.sdl2;

public class Sdl2HidData {

    private static final int MAX_HID = 6;
    private static final int MAX_AXES = 10;
    private static final int MAX_BUTTONS = 20;
    private static final int MAX_HATS = 2;

    private final Sdl2HidMeta[] mMeta;
    private final int[][] mAxes;
    private final boolean[][] mButtons;
    private final int[][] mHats;

    public Sdl2HidData() {
        mMeta = new Sdl2HidMeta[MAX_HID];
        mAxes = new int[MAX_HID][MAX_AXES];
        mButtons = new boolean[MAX_HID][MAX_BUTTONS];
        mHats = new int[MAX_HID][MAX_HATS];
    }

    boolean isConnected(int index) {
        return mMeta[index] != null;
    }

    Sdl2HidMeta getMeta(int index) {
        return mMeta[index];
    }

    void setMeta(int index, Sdl2HidMeta meta) {
        mMeta[index] = meta;
    }

    int getAxis(int index, int axis) {
        return mAxes[index][axis];
    }

    void setAxis(int index, int axis, int value) {
        mAxes[index][axis] = value;
    }

    boolean getButton(int index, int button) {
        return mButtons[index][button];
    }

    void setButton(int index, int button, boolean value) {
        mButtons[index][button] = value;
    }

    int getHat(int index, int hat) {
        return mHats[index][hat];
    }

    void setHat(int index, int hat, int value) {
        mHats[index][hat] = value;
    }
}
