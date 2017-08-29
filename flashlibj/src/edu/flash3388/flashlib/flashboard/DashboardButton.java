package edu.flash3388.flashlib.flashboard;

import edu.flash3388.flashlib.communications.Sendable;
import edu.flash3388.flashlib.robot.Action;
import edu.flash3388.flashlib.robot.hid.Button;

/**
 * Represents a button on the Flashboard.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public class DashboardButton extends Sendable{
	
	public static final byte DOWN = 0xe;
	public static final byte UP = 0x5;
	
	private Button button;
	private boolean running = false;
	
	public DashboardButton(String name) {
		super(name, FlashboardSendableType.ACTIVATABLE);
		
		button = new Button(name, -1, -1);
	}

	public void whenPressed(Action action){
		button.whenPressed(action);
	}
	
	@Override
	public void newData(byte[] data) {
		if(data[0] == DOWN && !running){
			running = true;
			button.setPressed(true);
		}else if(data[0] == UP){
			button.setPressed(false);
			button.stopAll();
			running = false;
		}
	}
	@Override
	public byte[] dataForTransmition() {
		return new byte[] {UP};
	}
	@Override
	public boolean hasChanged() {
		return running && !button.actionsStillRunning();
	}
	@Override
	public void onConnection() {
		
	}
	@Override
	public void onConnectionLost(){
		button.setPressed(false);
		button.stopAll();
		running = false;
	}
}
