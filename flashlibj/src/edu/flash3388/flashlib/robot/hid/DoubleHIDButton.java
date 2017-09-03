package edu.flash3388.flashlib.robot.hid;

import edu.flash3388.flashlib.robot.Action;
import edu.flash3388.flashlib.util.FlashUtil;

public class DoubleHIDButton extends Button{
	
	private HIDButton button1, button2;
	
	private boolean last = false;
	private int holdStart = -1;
	
	/**
	 * Creates a dual button wrapper
	 * 
	 * @param button1 the first button
	 * @param button2 the second button
	 */
	public DoubleHIDButton(HIDButton button1, HIDButton button2){
		this.button1 = button1;
		this.button2 = button2;
	}
	
	/**
	 * Gets the first button 
	 * @return first button
	 */
	public final HIDButton getButton1(){
		return button1;
	}
	/**
	 * Gets the second button 
	 * @return second button
	 */
	public final HIDButton getButton2(){
		return button2;
	}

	/**
	 * Gets the current button state
	 */
	@Override
	public boolean get() {
		return button1.get() && button2.get();
	}
	
	/**
	 * Updates the {@link Button} state. Necessary in order to activate and stop attached {@link Action}s
	 */
	@Override
	public void run(){
		boolean down = get();
		
		if(last && down && holdStart <= 0)
			holdStart = FlashUtil.millisInt();
		
		int timepassed = holdStart > 0? FlashUtil.millisInt() - holdStart : 0;
		if(last && !down){
			holdStart = -1;
			if(timepassed < HIDButton.MAX_MILLIS_PRESS)
				setPressed();
			else
				setReleased();
		}else if(last && down && timepassed > HIDButton.MAX_MILLIS_PRESS)
			setHeld();
		
		last = down;
	}
}
