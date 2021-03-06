package com.flash3388.flashlib.hid.generic;

import com.flash3388.flashlib.hid.Axis;
import com.flash3388.flashlib.hid.Button;
import com.flash3388.flashlib.hid.Hid;
import com.flash3388.flashlib.hid.Pov;

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
    public Axis getAxis(int index) {
        if (index < 0 || index >= mAxes.size()) {
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
        return mAxes;
    }

    @Override
    public Button getButton(int index) {
        if (index < 0 || index >= mButtons.size()) {
            throw new IllegalArgumentException("Unknown button " + index);
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
        if (index < 0 || index >= mPovs.size()) {
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
        return mPovs;
    }
}
