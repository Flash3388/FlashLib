package edu.flash3388.flashlib.robot.hid;

import edu.flash3388.flashlib.robot.Action;
import edu.flash3388.flashlib.robot.hid.HIDButton.HIDRunnable;
import edu.flash3388.flashlib.robot.hid.HIDButton.TimedMultiPress;

/**
 * An extension of {@link DoubleButton} for {@link HIDButton}s.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.2
 */
public class DoubleHIDButton extends DoubleButton{
	
	private HIDRunnable updateRunnable;
	
	public DoubleHIDButton(Button a, Button b) {
		super(a, b);
		updateRunnable = new HIDRunnable(this);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void whenMultiPressed(Action action, int presses) {
		super.addButtonAction(new TimedMultiPress(action, presses));
	}

	/**
	 * Updates the {@link Button} state. Necessary in order to activate and stop attached {@link Action}s
	 */
	@Override
	public void run(){
		updateRunnable.run();
	}
}
