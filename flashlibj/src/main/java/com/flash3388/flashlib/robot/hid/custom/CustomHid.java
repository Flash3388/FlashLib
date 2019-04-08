package com.flash3388.flashlib.robot.hid.custom;

import com.flash3388.flashlib.robot.hid.Axis;
import com.flash3388.flashlib.robot.hid.Button;
import com.flash3388.flashlib.robot.hid.Hid;
import com.flash3388.flashlib.robot.hid.NoSuchAxisException;
import com.flash3388.flashlib.robot.hid.NoSuchButtonException;
import com.flash3388.flashlib.robot.hid.NoSuchPovException;
import com.flash3388.flashlib.robot.hid.Pov;

import java.util.Map;

public class CustomHid implements Hid {

    private final int mChannel;
    private final Map<Integer, Axis> mAxes;
    private final Map<Integer, Button> mButtons;
    private final Map<Integer, Pov> mPovs;

    public CustomHid(int channel, Map<Integer, Axis> axes, Map<Integer, Button> buttons, Map<Integer, Pov> povs) {
        mChannel = channel;
        mAxes = axes;
        mButtons = buttons;
        mPovs = povs;
    }

    @Override
    public int getChannel() {
        return mChannel;
    }

    @Override
    public Axis getAxis(int axis) {
        if (!mAxes.containsKey(axis)) {
            throw new NoSuchAxisException(mChannel, axis);
        }

        return mAxes.get(axis);
    }

    @Override
    public int getAxisCount() {
        return mAxes.size();
    }

    @Override
    public Iterable<Axis> axes() {
        return mAxes.values();
    }

    @Override
    public Button getButton(int button) {
        if (!mButtons.containsKey(button)) {
            throw new NoSuchButtonException(mChannel, button);
        }

        return mButtons.get(button);
    }

    @Override
    public int getButtonCount() {
        return mButtons.size();
    }

    @Override
    public Iterable<Button> buttons() {
        return mButtons.values();
    }

    @Override
    public Pov getPov(int pov) {
        if (!mPovs.containsKey(pov)) {
            throw new NoSuchPovException(mChannel, pov);
        }

        return mPovs.get(pov);
    }

    @Override
    public int getPovCount() {
        return mPovs.size();
    }

    @Override
    public Iterable<Pov> povs() {
        return mPovs.values();
    }
}
