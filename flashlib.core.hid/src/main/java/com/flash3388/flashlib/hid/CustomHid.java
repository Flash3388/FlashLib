package com.flash3388.flashlib.hid;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * A customizable {@link Hid} implementation. Allows defining {@link Hid Hids} which are not necessarily
 * representative of an actual single device.
 * Use {@link CustomHid.Builder} to easily construct an instance.
 *
 * @since FlashLib 3.0.0
 */
public class CustomHid implements Hid {

    public static class Builder {

        private final Map<Integer, Axis> mAxes;
        private final Map<Integer, Button> mButtons;
        private final Map<Integer, Pov> mPovs;

        public Builder() {
            mAxes = new HashMap<>();
            mButtons = new HashMap<>();
            mPovs = new HashMap<>();
        }

        public Builder addAxis(int axisIndex, Axis axis) {
            mAxes.put(axisIndex, axis);
            return this;
        }

        public Builder addButton(int buttonIndex, Button button) {
            mButtons.put(buttonIndex, button);
            return this;
        }

        public Builder addPov(int povIndex, Pov pov) {
            mPovs.put(povIndex, pov);
            return this;
        }

        public Hid build() {
            return new CustomHid(mAxes, mButtons, mPovs);
        }
    }

    private final Map<Integer, Axis> mAxes;
    private final Map<Integer, Button> mButtons;
    private final Map<Integer, Pov> mPovs;

    public CustomHid(Map<Integer, Axis> axes, Map<Integer, Button> buttons, Map<Integer, Pov> povs) {
        mAxes = new HashMap<>(axes);
        mButtons = new HashMap<>(buttons);
        mPovs = new HashMap<>(povs);
    }

    @Override
    public Axis getAxis(int index) {
        if (!mAxes.containsKey(index)) {
            throw new IllegalArgumentException("Unknown axis " + index);
        }

        return mAxes.get(index);
    }

    @Override
    public int getAxisCount() {
        return mAxes.size();
    }

    @Override
    public Iterable<Axis> axes() {
        return Collections.unmodifiableCollection(mAxes.values());
    }

    @Override
    public Button getButton(int index) {
        if (!mButtons.containsKey(index)) {
            throw new IllegalArgumentException("Unknown button " + index);
        }

        return mButtons.get(index);
    }

    @Override
    public int getButtonCount() {
        return mButtons.size();
    }

    @Override
    public Iterable<Button> buttons() {
        return Collections.unmodifiableCollection(mButtons.values());
    }

    @Override
    public Pov getPov(int index) {
        if (!mPovs.containsKey(index)) {
            throw new IllegalArgumentException("Unknown pov " + index);
        }

        return mPovs.get(index);
    }

    @Override
    public int getPovCount() {
        return mPovs.size();
    }

    @Override
    public Iterable<Pov> povs() {
        return Collections.unmodifiableCollection(mPovs.values());
    }
}
