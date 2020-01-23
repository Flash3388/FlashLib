package com.flash3388.flashlib.robot.hid;

import com.flash3388.flashlib.robot.control.Invertable;
import com.flash3388.flashlib.robot.scheduling.actions.Action;
import com.flash3388.flashlib.robot.scheduling.triggers.Trigger;

import java.util.function.BooleanSupplier;

/**
 * The base logic for a button. Allows attaching {@link Action} objects which will be executed
 * according to different parameters.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public abstract class Button extends Trigger implements BooleanSupplier, Invertable {

    public abstract boolean isDown();

    @Override
    public boolean getAsBoolean() {
        return isDown();
    }

    public void addToScheduler() {
        addToScheduler(this);
    }

    public abstract void whenPressed(Action action);
    public abstract void whileHeld(Action action);
}
