package com.flash3388.flashlib.robot.hid.custom;

import com.flash3388.flashlib.robot.RunningRobot;
import com.flash3388.flashlib.robot.hid.Axis;
import com.flash3388.flashlib.robot.hid.Button;
import com.flash3388.flashlib.robot.hid.DPad;
import com.flash3388.flashlib.robot.hid.HidAxis;
import com.flash3388.flashlib.robot.hid.HidButton;
import com.flash3388.flashlib.robot.hid.HidInterface;
import com.flash3388.flashlib.robot.hid.Pov;
import com.flash3388.flashlib.time.Clock;

import java.util.HashMap;
import java.util.Map;

public class CustomHidBuilder {

    private final Clock mClock;
    private final HidInterface mHidInterface;
    private final int mChannel;
    private final Map<Integer, Axis> mAxes;
    private final Map<Integer, Button> mButtons;
    private final Map<Integer, Pov> mPovs;

    public CustomHidBuilder(Clock clock, HidInterface hidInterface, int channel) {
        mClock = clock;
        mHidInterface = hidInterface;
        mChannel = channel;

        mAxes = new HashMap<>();
        mButtons = new HashMap<>();
        mPovs = new HashMap<>();
    }

    public CustomHidBuilder(int channel) {
        this(RunningRobot.INSTANCE.get().getClock(), RunningRobot.INSTANCE.get().getHidInterface(), channel);
    }

    public CustomHidBuilder addAxis(int axisNumber, Axis axis) {
        mAxes.put(axisNumber, axis);
        return this;
    }

    public CustomHidBuilder addAxis(int axisNumber) {
        return addAxis(axisNumber, new HidAxis(mHidInterface, mChannel, axisNumber));
    }

    public CustomHidBuilder addButton(int buttonNumber, Button button) {
        mButtons.put(buttonNumber, button);
        return this;
    }

    public CustomHidBuilder addButton(int buttonNumber) {
        return addButton(buttonNumber, new HidButton(mClock, mHidInterface, mChannel, buttonNumber));
    }

    public CustomHidBuilder addPov(int povNumber, Pov pov) {
        mPovs.put(povNumber, pov);
        return this;
    }

    public CustomHidBuilder addPov(int povNumber) {
        return addPov(povNumber, new Pov(mHidInterface, mChannel, povNumber));
    }

    public CustomHidBuilder addDpad(int povNumber) {
        return addPov(povNumber, new DPad(mClock, mHidInterface, mChannel, povNumber));
    }

    public CustomHid build() {
        return new CustomHid(mChannel, mAxes, mButtons, mPovs);
    }
}
