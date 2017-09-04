package edu.flash3388.flashlib.robot.hid;

import edu.flash3388.flashlib.robot.Action;
import edu.flash3388.flashlib.util.FlashUtil;

/**
 * An extension of {@link Button} for human interface devices. Provides time buffering for activation types.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.2
 */
public class HIDButton extends Button{

	private static class TimedMultiPress extends ButtonAction{
		
		private static final int PRESS_TIMEOUT = MAX_MILLIS_PRESS * 4;
		
		private int lastPress = -1;
		private int presses = 0;
		private int pressesStart;
		
		public TimedMultiPress(Action action, int presses){
			super(ActivateType.Press, action);
			this.pressesStart = presses;
		}
		
		public void start(){
			if(!action.isRunning()){
				if(lastPress < 0)
					 lastPress = FlashUtil.millisInt();
				
				if(FlashUtil.millisInt() - lastPress > PRESS_TIMEOUT)
					presses = 0;
				
				if((++presses) == pressesStart){
					action.start();
					presses = 0;
					lastPress = -1;
				}
				
				lastPress = FlashUtil.millisInt();
			}
			else if(presses != 0){
				presses = 0;
				lastPress = -1;
			}
		}
		public void stop(){
			lastPress = -1;
			presses = 0;
			if(action.isRunning())
				action.cancel();
		}
		
	}
	
	public static final int MAX_MILLIS_PRESS = 500;
	
	private HID hid;
	private int num;
	
	private boolean last = false;
	private int holdStart = -1;
	
	/**
	 * Creates a button wrapper object
	 * 
	 * @param hid The hid
	 * @param num the number of the button on the controller.
	 */
	public HIDButton(HID hid, int num){
		this.hid = hid;
		this.num = num;
	}
	
	/**
	 * Get the HID
	 * @return hid
	 */
	public final HID getHID(){
		return hid;
	}
	/**
	 * Get the button number
	 * @return button number
	 */
	public final int getButtonNumber(){
		return num;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void whenMultiPressed(Action action, int presses) {
		super.addButtonAction(new TimedMultiPress(action, presses));
	}
	
	/**
	 * Gets the current button state
	 */
	@Override
	public boolean get() {
		return hid.getRawButton(num);
	}
	
	/**
	 * Updates the {@link Button} state. Necessary in order to activate and stop attached {@link Action}s
	 */
	@Override
	public void run(){
		boolean down = get();
		
		if(last && down && holdStart <= 0){
			holdStart = FlashUtil.millisInt();
		}
		
		int timepassed = holdStart > 0? FlashUtil.millisInt() - holdStart : 0;
		if(last && !down){
			holdStart = -1;
			if(timepassed < MAX_MILLIS_PRESS)
				setPressed();
			else
				setReleased();
		}else if(last && down && timepassed > MAX_MILLIS_PRESS)
			setHeld();
		
		last = down;
	}
}
