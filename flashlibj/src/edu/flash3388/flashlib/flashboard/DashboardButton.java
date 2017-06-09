package edu.flash3388.flashlib.flashboard;

import edu.flash3388.flashlib.communications.Sendable;
import edu.flash3388.flashlib.robot.Action;
import edu.flash3388.flashlib.robot.hid.Button;

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
			running = false;
			button.setPressed(false);
			button.stopAll();
		}
	}
	@Override
	public byte[] dataForTransmition() {
		return new byte[] {UP};
	}
	@Override
	public boolean hasChanged() {
		return running && button.actionsStilRunning();
	}
	@Override
	public void onConnection() {
		running = false;
		button.setPressed(false);
	}
	@Override
	public void onConnectionLost() {
		button.stopAll();
	}
}
