package edu.flash3388.flashlib.robot.hid;

import java.util.ArrayList;
import java.util.List;

public class GenericHid implements Hid {

	private final int mChannel;

	private final List<Axis> mAxes;
	private final List<Button> mButtons;
    private final List<Pov> mPovs;

	public GenericHid(HidInterface hidInterface, int channel, int axisCount, int buttonCount, int povsCount){
		mChannel = channel;

        mAxes = new ArrayList<>();
        for(int i = 0; i < axisCount; i++) {
            mAxes.add(new Axis(hidInterface, channel, i));
        }

		mButtons = new ArrayList<>();
		for(int i = 0; i < buttonCount; i++) {
			mButtons.add(new HidButton(hidInterface, channel, i));
		}

        mPovs = new ArrayList<>();
        for(int i = 0; i < povsCount; i++) {
            mPovs.add(new Pov(hidInterface, channel, i));
        }
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
}
