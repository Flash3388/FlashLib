package edu.flash3388.flashlib.robot.hid;

import edu.flash3388.flashlib.robot.scheduling.Action;
import edu.flash3388.flashlib.robot.scheduling.triggers.Trigger;
import edu.flash3388.flashlib.util.beans.BooleanSource;

/**
 * The base logic for a button. Allows attaching {@link Action} objects which will be executed 
 * according to different parameters.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public abstract class Button extends Trigger implements BooleanSource {

	public abstract boolean isDown();

	@Override
	public boolean get() {
		return isDown();
	}
}
