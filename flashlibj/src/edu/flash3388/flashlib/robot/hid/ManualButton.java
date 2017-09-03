package edu.flash3388.flashlib.robot.hid;

import edu.flash3388.flashlib.util.beans.BooleanProperty;

/**
 * An extension of {@link Button} for manually control.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.2
 */
public class ManualButton extends Button implements BooleanProperty{

	private boolean down, inverted, last;
	
	public boolean isInverted(){
		return inverted;
	}
	public void setInverted(boolean inverted){
		this.inverted = inverted;
	}
	
	@Override
	public boolean get() {
		return down ^ inverted;
	}
	@Override
	public void set(boolean b) {
		down = b;
	}
	
	@Override
	public void setValue(Boolean o) {
		set(o == null? false : o.booleanValue());
	}
	@Override
	public Boolean getValue() {
		return get();
	}

	@Override
	public void run() {
		boolean down = get();
		
		if(!last && down)
			setPressed();
		else if(last && down)
			setHeld();
		else 
			setReleased();
		
		last = down;
	}
}
