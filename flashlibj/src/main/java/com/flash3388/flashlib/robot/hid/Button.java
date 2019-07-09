package com.flash3388.flashlib.robot.hid;

import com.flash3388.flashlib.robot.RunningRobot;
import com.flash3388.flashlib.robot.hid.triggers.handlers.PressStateHandler;
import com.flash3388.flashlib.robot.scheduling.Action;
import com.flash3388.flashlib.robot.scheduling.triggers.Trigger;
import com.flash3388.flashlib.robot.control.Invertable;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.time.Time;

import java.util.function.BooleanSupplier;

/**
 * The base logic for a button. Allows attaching {@link Action} objects which will be executed
 * according to different parameters.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public abstract class Button extends Trigger implements BooleanSupplier, Invertable {

    private static final Time MAX_PRESS_TIME = Time.milliseconds(200);

    public abstract boolean isDown();

    @Override
    public boolean getAsBoolean() {
        return isDown();
    }

    public Trigger whenPressed(Action action, Clock clock, Time maxPressTime) {
        return addStateHandler(new PressStateHandler(action, clock, maxPressTime));
    }

    public Trigger whenPressed(Action action, Time maxPressTime) {
        return whenPressed(action, RunningRobot.INSTANCE.get().getClock(), maxPressTime);
    }

    public Trigger whenPressed(Action action) {
        return whenPressed(action, MAX_PRESS_TIME);
    }

    public Trigger whileHeld(Action action, Clock clock, Time minHeldTime) {
        return addStateHandler(new PressStateHandler(action, clock, minHeldTime));
    }

    public Trigger whileHeld(Action action, Time minHeldTime) {
        return whileHeld(action, RunningRobot.INSTANCE.get().getClock(), minHeldTime);
    }

    public Trigger whileHeld(Action action) {
        return whileHeld(action, MAX_PRESS_TIME);
    }
}
