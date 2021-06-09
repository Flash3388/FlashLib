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
        return mAxes.size();
    }

    @Override
    public Iterable<Axis> axes() {
        return mAxes;
    }

    @Override
    public Button getButton(int index) {
        if (index < 0) {
            throw new IllegalArgumentException("Unknown button " + index);
        }

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
        return mButtons.size();
    }

    @Override
    public Iterable<Button> buttons() {
        return mButtons;
    }

    @Override
    public Pov getPov(int index) {
        if (index < 0) {
            throw new IllegalArgumentException("Unknown pov " + index);
        }

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
        return mPovs.size();
    }

    @Override
    public Iterable<Pov> povs() {
        return mPovs;
    }

}
