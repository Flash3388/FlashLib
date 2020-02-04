package com.flash3388.flashlib.robot.hid.xbox;

import com.flash3388.flashlib.robot.RunningRobot;
import com.flash3388.flashlib.robot.hid.Axis;
import com.flash3388.flashlib.robot.hid.Button;
import com.flash3388.flashlib.robot.hid.DPad;
import com.flash3388.flashlib.robot.hid.HardwareButton;
import com.flash3388.flashlib.robot.hid.Hid;
import com.flash3388.flashlib.robot.hid.HidAxis;
import com.flash3388.flashlib.robot.hid.HidButton;
import com.flash3388.flashlib.robot.hid.HidInterface;
import com.flash3388.flashlib.robot.hid.NoSuchAxisException;
import com.flash3388.flashlib.robot.hid.NoSuchButtonException;
import com.flash3388.flashlib.robot.hid.NoSuchPovException;
import com.flash3388.flashlib.robot.hid.Pov;
import com.flash3388.flashlib.robot.scheduling.Scheduler;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.time.Time;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Represents an Xbox 360 controller for use for robot control.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public class XboxController implements Hid {
	
	private static final int BUTTON_COUNT = 10;
	private static final int AXES_COUNT = 6;
	private static final int DPAD_POV_INDEX = 0;

	private final int mChannel;

    private final List<Axis> mAxes;
    private final List<Button> mButtons;
    private final DPad mDpad;

	public XboxController(Scheduler scheduler, Clock clock, HidInterface hidInterface, int channel, Time buttonPressTime){
		mChannel = channel;

		mAxes = Collections.unmodifiableList(IntStream.range(0, AXES_COUNT)
                .mapToObj((i)-> new HidAxis(hidInterface, mChannel, i))
                .collect(Collectors.toList()));

        mButtons = Collections.unmodifiableList(IntStream.range(0, BUTTON_COUNT)
                .mapToObj((i)-> new HidButton(clock, buttonPressTime, hidInterface, mChannel, i))
                .collect(Collectors.toList()));

        mDpad = new DPad(clock, buttonPressTime, hidInterface, mChannel, DPAD_POV_INDEX);

        mButtons.forEach(Button::addToScheduler);
        mDpad.buttons().forEach(Button::addToScheduler);
	}

    public XboxController(Scheduler scheduler, Clock clock, HidInterface hidInterface, int channel) {
	    this(scheduler, clock, hidInterface, channel, HardwareButton.DEFAULT_MAX_PRESS_TIME);
    }

	public XboxController(int channel) {
	    this(RunningRobot.getInstance().getScheduler(),
	            RunningRobot.getInstance().getClock(),
                RunningRobot.getInstance().getHidInterface(),
                channel);
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

    public Axis getAxis(XboxAxis xboxAxis) {
        return getAxis(xboxAxis.axisIndex());
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

	public Button getButton(XboxButton xboxButton) {
	    return getButton(xboxButton.buttonIndex());
    }

	@Override 
	public int getButtonCount(){
		return BUTTON_COUNT;
	}

    @Override
    public Iterable<Button> buttons() {
        return mButtons;
    }

    @Override
    public Pov getPov(int pov) {
        if (pov != DPAD_POV_INDEX) {
            throw new NoSuchPovException(mChannel, pov);
        }

        return getDPad();
    }

    public DPad getDPad() {
        return mDpad;
    }

    @Override
    public int getPovCount() {
        return 1;
    }

    @Override
    public Iterable<Pov> povs() {
        return Collections.singleton(mDpad);
    }
}

