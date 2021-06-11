package com.flash3388.flashlib.hid.generic.weak;

import com.flash3388.flashlib.hid.Axis;
import com.flash3388.flashlib.hid.Button;
import com.flash3388.flashlib.hid.Hid;
import com.flash3388.flashlib.hid.Pov;
import com.flash3388.flashlib.hid.generic.GenericAxis;
import com.flash3388.flashlib.hid.generic.GenericButton;
import com.flash3388.flashlib.hid.generic.GenericPov;
import com.flash3388.flashlib.hid.generic.RawHidInterface;

import java.util.ArrayList;
import java.util.List;

public class WeakHid implements Hid {

    private final RawHidInterface mInterface;
    private final int mChannel;

    private final List<Axis> mAxes;
    private final List<Button> mButtons;
    private final List<Pov> mPovs;

    public WeakHid(RawHidInterface hidInterface, int channel,
                   List<Axis> axes, List<Button> buttons, List<Pov> povs) {
        mInterface = hidInterface;
        mChannel = channel;
        mAxes = axes;
        mButtons = buttons;
        mPovs = povs;
    }

    public WeakHid(RawHidInterface hidInterface, int channel) {
        this(hidInterface, channel, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    }

    @Override
    public Axis getAxis(int index) {
        if (index < 0) {
            throw new IllegalArgumentException("Unknown axis " + index);
        }

        refreshAxes();

        if (index == mAxes.size()) {
            mAxes.add(new GenericAxis(mInterface, mChannel, index));
        } else if (index > mAxes.size()) {
            for (int i = mAxes.size(); i <= index; i++) {
                mAxes.add(new GenericAxis(mInterface, mChannel, i));
            }
        }

        return mAxes.get(index);
    }

    @Override
    public int getAxisCount() {
        refreshAxes();
        return mAxes.size();
    }

    @Override
    public Iterable<Axis> axes() {
        refreshAxes();
        return mAxes;
    }

    @Override
    public Button getButton(int index) {
        if (index < 0) {
            throw new IllegalArgumentException("Unknown button " + index);
        }

        refreshButtons();

        if (index == mButtons.size()) {
            mButtons.add(new GenericButton(mInterface, mChannel, index));
        } else if (index > mButtons.size()) {
            for (int i = mButtons.size(); i <= index; i++) {
                mButtons.add(new GenericButton(mInterface, mChannel, i));
            }
        }

        return mButtons.get(index);
    }

    @Override
    public int getButtonCount(){
        refreshButtons();
        return mButtons.size();
    }

    @Override
    public Iterable<Button> buttons() {
        refreshButtons();
        return mButtons;
    }

    @Override
    public Pov getPov(int index) {
        if (index < 0) {
            throw new IllegalArgumentException("Unknown pov " + index);
        }

        refreshPovs();

        if (index == mPovs.size()) {
            mPovs.add(new GenericPov(mInterface, mChannel, index));
        } else if (index > mPovs.size()) {
            for (int i = mPovs.size(); i <= index; i++) {
                mPovs.add(new GenericPov(mInterface, mChannel, i));
            }
        }

        return mPovs.get(index);
    }

    @Override
    public int getPovCount() {
        refreshPovs();
        return mPovs.size();
    }

    @Override
    public Iterable<Pov> povs() {
        refreshPovs();
        return mPovs;
    }

    private void refreshAxes() {
        int axisCount = mInterface.getAxesCount(mChannel);
        if (axisCount > mAxes.size()) {
            for (int i = mAxes.size(); i < axisCount; i++) {
                mAxes.add(new GenericAxis(mInterface, mChannel, i));
            }
        }
    }

    private void refreshButtons() {
        int buttonCount = mInterface.getButtonsCount(mChannel);
        if (buttonCount > mButtons.size()) {
            for (int i = mButtons.size(); i < buttonCount; i++) {
                mButtons.add(new GenericButton(mInterface, mChannel, i));
            }
        }
    }

    private void refreshPovs() {
        int povCount = mInterface.getPovsCount(mChannel);
        if (povCount > mPovs.size()) {
            for (int i = mPovs.size(); i < povCount; i++) {
                mPovs.add(new GenericPov(mInterface, mChannel, i));
            }
        }
    }
}
