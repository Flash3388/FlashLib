package edu.flash3388.flashlib.flashboard;

import edu.flash3388.flashlib.communications.Sendable;
import edu.flash3388.flashlib.util.FlashUtil;

public class FlashboardHIDSendable extends Sendable implements Runnable{

	private static class ButtonData{
		public int buttons;
		public byte count;
	}
	private static class AxesData{
		public float[] axes;
		public byte count;
		
		public AxesData(int count){
			axes = new float[count];
		}
	}
	private static class POVData{
		public short[] povs;
		public byte count;
		
		public POVData(int count){
			povs = new short[count];
		}
	}
	
	public static final byte JOYSTICK_DATA = 0x1;
	
	public static final int MAX_PORT_COUNT = 6;
	public static final int MAX_AXES_COUNT = 10;
	public static final int MAX_POV_COUNT = 2;
	
	private ButtonData[] joystickButton_data = new ButtonData[MAX_PORT_COUNT];
	private POVData[] joystickPOV_data = new POVData[MAX_PORT_COUNT];
	private AxesData[] joystickAxes_data = new AxesData[MAX_PORT_COUNT];
	
	private ButtonData[] joystickButton_cache = new ButtonData[MAX_PORT_COUNT];
	private POVData[] joystickPOV_cache = new POVData[MAX_PORT_COUNT];
	private AxesData[] joystickAxes_cache = new AxesData[MAX_PORT_COUNT];
	
	private boolean updated = false;
	private Object joystickMutex = new Object();
	
	public FlashboardHIDSendable(String name) {
		super(name, FlashboardSendableType.JOYSTICK);
		
		for (int i = 0; i < MAX_PORT_COUNT; i++) {
			joystickAxes_data[i] = new AxesData(MAX_AXES_COUNT);
			joystickButton_data[i] = new ButtonData();
			joystickPOV_data[i] = new POVData(MAX_POV_COUNT);
			
			joystickAxes_cache[i] = new AxesData(MAX_AXES_COUNT);
			joystickButton_cache[i] = new ButtonData();
			joystickPOV_cache[i] = new POVData(MAX_POV_COUNT);
		}
	}

	
	public int getAxisCount(int channel){
		if(channel < 0 || channel >= MAX_PORT_COUNT)
			throw new IllegalArgumentException("Channel value should be between 0-"+MAX_PORT_COUNT);
		if(!remoteAttached())
			return 0;
		
		int value = 0;
		synchronized (joystickMutex) {
			value = joystickAxes_data[channel].count;
		}
		
		return value;
	}
	public double getAxis(int channel, int axis){
		if(channel < 0 || channel >= MAX_PORT_COUNT)
			throw new IllegalArgumentException("Channel value should be between 0-"+MAX_PORT_COUNT);
		if(axis < 0 || axis > MAX_AXES_COUNT)
			throw new IllegalArgumentException("Axis index should be between 0-"+MAX_AXES_COUNT);
		if(!remoteAttached())
			return 0.0;
		
		double value = 0.0;
		
		synchronized (joystickMutex) {
			if(joystickAxes_data[channel].count > axis)
				value = joystickAxes_data[channel].axes[axis];
		}
		
		return value;
	}
	
	public int getPOVCount(int channel){
		if(channel < 0 || channel >= MAX_PORT_COUNT)
			throw new IllegalArgumentException("Channel value should be between 0-"+MAX_PORT_COUNT);
		if(!remoteAttached())
			return 0;
		
		int value = 0;
		synchronized (joystickMutex) {
			value = joystickPOV_data[channel].count;
		}
		
		return value;
	}
	public int getPOV(int channel, int pov){
		if(channel < 0 || channel >= MAX_PORT_COUNT)
			throw new IllegalArgumentException("Channel value should be between 0-"+MAX_PORT_COUNT);
		if(pov < 0 || pov > MAX_POV_COUNT)
			throw new IllegalArgumentException("POV index should be between 0-"+MAX_POV_COUNT);
		if(!remoteAttached())
			return -1;
		
		int value = -1;
		
		synchronized (joystickMutex) {
			if(joystickPOV_data[channel].count > pov)
				value = joystickPOV_data[channel].povs[pov];
		}
		
		return value;
	}
	
	public int getButtonCount(int channel){
		if(channel < 0 || channel >= MAX_PORT_COUNT)
			throw new IllegalArgumentException("Channel value should be between 0-"+MAX_PORT_COUNT);
		if(!remoteAttached())
			return 0;
		
		int value = 0;
		synchronized (joystickMutex) {
			value = joystickButton_data[channel].count;
		}
		
		return value;
	}
	public boolean getButton(int channel, int button){
		if(channel < 0 || channel >= MAX_PORT_COUNT)
			throw new IllegalArgumentException("Channel value should be between 0-"+MAX_PORT_COUNT);
		if(button < 1)
			throw new IllegalArgumentException("Button index should be positive");
		if(!remoteAttached())
			return false;
		
		boolean value = false;
		synchronized (joystickMutex) {
			if(joystickButton_data[channel].count >= button)
				value = (joystickButton_data[channel].buttons & (1 << (button - 1))) != 0;
		}
		
		return value;
	}
	
	
	@Override
	public void newData(byte[] data) {
		if(data[0] == JOYSTICK_DATA){
			synchronized (joystickMutex) {
				updated = false;
				
				int pos = 1;
				int joystick;
				byte i, value;
				short vals;
				
				while(data.length - pos >= 4){
					joystick = data[pos++];
					
					//axes data
					value = data[pos++];
					if(value != joystickAxes_cache[joystick].count){
						joystickAxes_cache[joystick].count = value;
						joystickAxes_cache[joystick].axes = new float[value];
					}
					
					if(data.length - pos >= 2 + value){
						for (i = 0; i < joystickAxes_cache[joystick].count; i++) {
							value = data[pos++];
							joystickAxes_cache[joystick].axes[i] = value < 0? value / 128.0f : value / 127.0f;
						}
					}
					
					//POV data
					value = data[pos++];
					if(value != joystickPOV_cache[joystick].count){
						joystickPOV_cache[joystick].count = value;
						joystickPOV_cache[joystick].povs = new short[value];
					}
					
					if(data.length - pos >= 1 + value){
						for (i = 0; i < joystickPOV_cache[joystick].count; i++) {
							vals = (short) (data[pos++] | (data[pos++] << 8));
							joystickPOV_cache[joystick].povs[i] = vals;
						}
					}
					
					//button data
					value = data[pos++];
					joystickButton_cache[joystick].count = value;
					
					if(data.length - pos >= 4){
						joystickButton_cache[joystick].buttons = FlashUtil.toInt(data, pos);
						pos += 4;
					}
				}
				
				updated = true;	
			}
		}
	}

	@Override
	public byte[] dataForTransmition() {
		return null;
	}
	@Override
	public boolean hasChanged() {
		return false;
	}

	@Override
	public void onConnection() {
	}
	@Override
	public void onConnectionLost() {
		for (int i = 0; i < MAX_PORT_COUNT; i++) {
			joystickAxes_data[i].count = 0;
			joystickButton_data[i].count = 0;
			joystickPOV_data[i].count = 0;
			
			joystickAxes_cache[i].count = 0;
			joystickButton_cache[i].count = 0;
			joystickPOV_cache[i].count = 0;
		}
	}

	@Override
	public void run() {
		synchronized (joystickMutex) {
			if(updated){
				
				AxesData[] axesData = joystickAxes_data;
				joystickAxes_data = joystickAxes_cache;
				joystickAxes_cache = axesData;
				
				POVData[] povData = joystickPOV_data;
				joystickPOV_data = joystickPOV_cache;
				joystickPOV_cache = povData;
				
				ButtonData[] buttonData = joystickButton_data;
				joystickButton_data = joystickButton_cache;
				joystickButton_cache = buttonData;
				
				updated = false;
			}
		}
	}
}