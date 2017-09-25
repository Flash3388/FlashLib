package edu.flash3388.flashlib.flashboard;

import edu.flash3388.flashlib.communications.Sendable;
import edu.flash3388.flashlib.robot.Action;
import edu.flash3388.flashlib.robot.hid.Button;
import edu.flash3388.flashlib.robot.hid.ManualButton;

/**
 * Represents a button on the Flashboard.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public class DashboardButton extends Sendable{
	
	public static final byte DOWN = 0xe;
	public static final byte UP = 0x5;
	public static final byte ENABLED = 0x1;
	
	private Button button;
	private boolean running = false;
	private boolean updateDisable = true;
	private boolean enabled = true;
	
	public DashboardButton(String name) {
		super(name, FlashboardSendableType.ACTIVATABLE);
		
		button = new ManualButton();
	}

	public void whenPressed(Action action){
		button.whenPressed(action);
	}
	public void setEnabled(boolean enable){
		if(!enable && running){
			running = false;
			button.stopAll();
		}
		this.enabled = enable;
		updateDisable = true;
	}
	public boolean isEnabled(){
		return enabled;
	}
	
	@Override
	public void newData(byte[] data) {
		if(data[0] == DOWN && !running){
			running = true;
			button.setPressed();
		}else if(data[0] == UP){
			button.stopAll();
			running = false;
		}
	}
	@Override
	public byte[] dataForTransmition() {
		if(updateDisable){
			updateDisable = false;
			return new byte[] {ENABLED, (byte) (enabled? 1 : 0)};
		}
		return new byte[] {UP};
	}
	@Override
	public boolean hasChanged() {
		return updateDisable || (running && !button.actionsRunning());
	}
	@Override
	public void onConnection() {
		updateDisable = true;
	}
	@Override
	public void onConnectionLost(){
		button.stopAll();
		running = false;
	}
}
