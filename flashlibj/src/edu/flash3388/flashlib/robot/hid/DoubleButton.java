package edu.flash3388.flashlib.robot.hid;

/**
 * An extension of {@link Button} which combines two button objects. This basically means that
 * {@link #get()} returns true if {@link #get()} for both buttons returns true.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.2
 */
public abstract class DoubleButton extends Button{
	
	private Button button1, button2;
	
	/**
	 * Creates a dual button wrapper
	 * 
	 * @param button1 the first button
	 * @param button2 the second button
	 */
	public DoubleButton(Button button1, Button button2){
		this.button1 = button1;
		this.button2 = button2;
	}
	
	/**
	 * Gets the first button 
	 * @return first button
	 */
	public final Button getButton1(){
		return button1;
	}
	/**
	 * Gets the second button 
	 * @return second button
	 */
	public final Button getButton2(){
		return button2;
	}

	/**
	 * Gets the current button state
	 */
	@Override
	public boolean get() {
		return button1.get() && button2.get();
	}
}
