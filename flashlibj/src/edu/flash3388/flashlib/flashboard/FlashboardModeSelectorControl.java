package edu.flash3388.flashlib.flashboard;

import edu.flash3388.flashlib.communications.SendableException;
import edu.flash3388.flashlib.util.FlashUtil;

public class FlashboardModeSelectorControl extends FlashboardControl{
	
	public static final byte UPDATE_MODE = 0x1;
	public static final byte UPDATE_DISABLED = 0x5;
	
	private static FlashboardModeSelectorControl instance;
	
	private Object stateMutex = new Object();
	
	private boolean disabled = true;
	private int currentState = 0;
	
	private FlashboardModeSelectorControl() {
		super("ModeSelector", FlashboardSendableType.MODE_SELECTOR);
	}

	public boolean isDisabled(){
		boolean d = true;
		synchronized (stateMutex) {
			d = disabled;
		}
		return d;
	}
	public int getCurrentState(){
		int state = 0;
		synchronized (stateMutex) {
			state = currentState;
		}
		return state;
	}
	
	@Override
	public void newData(byte[] data) throws SendableException {
		if(data[0] == UPDATE_DISABLED){
			boolean setd = data[1] != 0;
			synchronized (stateMutex) {
				disabled = setd;
			}
		}
		else if(data[0] == UPDATE_MODE){
			int state = FlashUtil.toInt(data, 1);
			synchronized (stateMutex) {
				currentState = state;
			}
		}
	}

	@Override
	public byte[] dataForTransmission() throws SendableException {
		return null;
	}
	@Override
	public boolean hasChanged() {
		return false;
	}

	@Override
	public void onConnection() {
		disabled = true;
	}
	@Override
	public void onConnectionLost() {
		disabled = true;
	}
	
	public static boolean hasInstance(){
		return instance != null;
	}
	public static FlashboardModeSelectorControl getInstance(){
		if(instance == null)
			instance = new FlashboardModeSelectorControl();
		return instance;
	}
}
