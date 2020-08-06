package com.flash3388.flashlib.robot.hid.generic;

import com.flash3388.flashlib.robot.hid.Axis;
import com.flash3388.flashlib.robot.hid.Button;
import com.flash3388.flashlib.robot.hid.Hid;
import com.flash3388.flashlib.robot.hid.Pov;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GenericHid implements Hid {

    private final List<Axis> mAxes;
    private final List<Button> mButtons;
    private final List<Pov> mPovs;

    public GenericHid(List<Axis> axes, List<Button> buttons, List<Pov> povs) {
        mAxes = axes;
        mButtons = buttons;
        mPovs = povs;
    }

    public GenericHid(RawHidInterface hidInterface, int channel, int axisCount, int buttonCount, int povsCount){
        this(Collections.unmodifiableList(IntStream.range(0, axisCount)
                    .mapToObj((i) -> new GenericAxis(hidInterface, channel, i))
                    .collect(Collectors.toList())),
                Collections.unmodifiableList(IntStream.range(0, buttonCount)
                    .mapToObj((i) -> new GenericButton(hidInterface, channel, i))
                    .collect(Collectors.toList())),
                Collections.unmodifiableList(IntStream.range(0, povsCount)
                    .mapToObj((i) -> new GenericPov(hidInterface, channel, i))
                    .collect(Collectors.toList()))
        );
    }

    public GenericHid(RawHidInterface hidInterface, int channel) {
        this(hidInterface, channel,
                hidInterface.getAxesCount(channel),
                hidInterface.getButtonsCount(channel),
                hidInterface.getPovsCount(channel));
    }

    @Override
    public Axis getAxis(int axis) {
        if (axis < 0 || axis >= mAxes.size()) {
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
        return mAxes;
    }

    @Override
    public Button getButton(int button) {
        if (button < 0 || button >= mButtons.size()) {
            throw new IllegalArgumentException("Unknown button " + button);
        }

        return mButtons.get(button);
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
    public Pov getPov(int pov) {
        if (pov < 0 || pov >= mPovs.size()) {
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
        return mPovs;
    }
}
