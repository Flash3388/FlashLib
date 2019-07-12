package com.flash3388.flashlib.robot.hid;

import com.flash3388.flashlib.robot.RunningRobot;
import com.flash3388.flashlib.time.Clock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GenericHid implements Hid {

	private final int mChannel;

	private final List<Axis> mAxes;
	private final List<Button> mButtons;
    private final List<Pov> mPovs;

	public GenericHid(Clock clock, HidInterface hidInterface, int channel, int axisCount, int buttonCount, int povsCount){
		mChannel = channel;

        mAxes = IntStream.range(0, axisCount)
                .mapToObj((i) -> new HidAxis(hidInterface, channel, i))
                .collect(Collectors.toList());

        mButtons = IntStream.range(0, buttonCount)
                .mapToObj((i) -> new HidButton(clock, hidInterface, channel, i))
                .collect(Collectors.toList());

        mPovs = IntStream.range(0, povsCount)
                .mapToObj((i) -> new Pov(hidInterface, channel, i))
                .collect(Collectors.toList());
	}

	public GenericHid(int channel, int axisCount, int buttonCount, int povsCount) {
	    this(RunningRobot.INSTANCE.get().getClock(), RunningRobot.INSTANCE.get().getHidInterface(),
                channel, axisCount, buttonCount, povsCount);
    }

	@Override
	public int getChannel(){
		return mChannel;
	}

    @Override
    public Axis getAxis(int axis) {
        if (axis < 0 || axis >= mAxes.size()) {
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
        return mAxes;
    }

    @Override
	public Button getButton(int button) {
        if (button < 0 || button >= mButtons.size()) {
            throw new NoSuchButtonException(mChannel, button);
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
        return mPovs;
    }
}
