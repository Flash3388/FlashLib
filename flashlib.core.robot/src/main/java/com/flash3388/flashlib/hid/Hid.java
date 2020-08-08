package com.flash3388.flashlib.hid;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public interface Hid {

    Axis getAxis(int axis);
    int getAxisCount();
    Iterable<Axis> axes();

    Button getButton(int button);
    int getButtonCount();
    Iterable<Button> buttons();

    Pov getPov(int pov);
    int getPovCount();
    Iterable<Pov> povs();

    class Builder {
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
            return new Custom(mAxes, mButtons, mPovs);
        }
    }

    class Custom implements Hid {

        private final Map<Integer, Axis> mAxes;
        private final Map<Integer, Button> mButtons;
        private final Map<Integer, Pov> mPovs;

        public Custom(Map<Integer, Axis> axes, Map<Integer, Button> buttons, Map<Integer, Pov> povs) {
            mAxes = new HashMap<>(axes);
            mButtons = new HashMap<>(buttons);
            mPovs = new HashMap<>(povs);
        }

        @Override
        public Axis getAxis(int axis) {
            if (!mAxes.containsKey(axis)) {
                throw new IllegalArgumentException("Unknown axis " + axis);
            }

            return mAxes.get(axis);
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
        public Button getButton(int button) {
            if (!mButtons.containsKey(button)) {
                throw new IllegalArgumentException("Unknown button " + button);
            }

            return mButtons.get(button);
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
        public Pov getPov(int pov) {
            if (!mPovs.containsKey(pov)) {
                throw new IllegalArgumentException("Unknown pov " + pov);
            }

            return mPovs.get(pov);
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


}
