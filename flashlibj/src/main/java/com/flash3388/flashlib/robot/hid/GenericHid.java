package com.flash3388.flashlib.robot.hid;

import com.flash3388.flashlib.robot.RunningRobot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GenericHid implements Hid {

	private final int mChannel;

	private final List<Axis> mAxes;
	private final List<Button> mButtons;
    private final List<Pov> mPovs;

	public GenericHid(HidInterface hidInterface, int channel, int axisCount, int buttonCount, int povsCount){
		mChannel = channel;

        List<Axis> axes = new ArrayList<>();
        for(int i = 0; i < axisCount; i++) {
            axes.add(new HidAxis(hidInterface, channel, i));
        }
        mAxes = Collections.unmodifiableList(axes);

        List<Button> buttons = new ArrayList<>();
		for(int i = 0; i < buttonCount; i++) {
            buttons.add(new HidButton(hidInterface, channel, i));
		}
		mButtons = Collections.unmodifiableList(buttons);

        List<Pov> povs = new ArrayList<>();
        for(int i = 0; i < povsCount; i++) {
            povs.add(new Pov(hidInterface, channel, i));
        }
        mPovs = Collections.unmodifiableList(povs);
	}

	public GenericHid(int channel, int axisCount, int buttonCount, int povsCount) {
	    this(RunningRobot.INSTANCE.get().getHidInterface(),
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
