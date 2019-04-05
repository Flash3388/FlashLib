package com.flash3388.flashlib.robot.hid;

import com.flash3388.flashlib.robot.scheduling.Action;
import com.flash3388.flashlib.robot.scheduling.triggers.Trigger;
import com.flash3388.flashlib.robot.control.Invertable;

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
}
