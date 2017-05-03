package edu.flash3388.flashlib.robot.sbc;

import java.util.Arrays;

import edu.flash3388.flashlib.communications.Sendable;
import edu.flash3388.flashlib.util.FlashUtil;

public final class SbcControlStation extends Sendable{
	
	private static class ControllerButtons{
		byte count;
		short buttons;
	}
	private static class UpdateTask implements Runnable{

		private SbcControlStation cs;
		
		public UpdateTask(SbcControlStation cs){
			this.cs = cs;
		}
		
		@Override
		public void run() {
			cs.task();
		}
	}
	
	public static final byte MAX_CONTROLLERS = 3;
	public static final byte CONTROLLER_AXES = 6;
	private static final byte CONTROLLER_DATA_SIZE = (5 + CONTROLLER_AXES) * MAX_CONTROLLERS + 2;
	
	private byte[][] controllerAxes = new byte[MAX_CONTROLLERS][CONTROLLER_AXES];
	private short[] controllerPovs = new short[MAX_CONTROLLERS];
	private ControllerButtons[] controllerButtons = new ControllerButtons[MAX_CONTROLLERS];
	
	private byte[] controllersData = new byte[CONTROLLER_DATA_SIZE];
	private byte connectedControllers = 0;
	private byte stateByte = 0;
	private boolean updateData = true;
	private boolean stop = false;
    private boolean attached = false;
	
	private Object recieveObject = new Object(), waitObject = new Object();
	
	private UpdateTask upTask;
	private Thread csThread;
	
	SbcControlStation() {
		super("CS-"+SbcBot.getBoardName(), SbcSendableType.CONSTROL_STATION);
		
		for (int i = 0; i < controllerButtons.length; i++)
			controllerButtons[i] = new ControllerButtons();
		upTask = new UpdateTask(this);
		csThread = new Thread(upTask, "CS-Update");
		csThread.setPriority((Thread.MAX_PRIORITY + Thread.NORM_PRIORITY) / 2);
		csThread.start();
	}
	void stop(){
		stop = true;
		stateByte = 0;
	}
	
	private void task(){
		while(!stop){
			synchronized (recieveObject) {
				try {
					recieveObject.wait();
				} catch (InterruptedException e) {
				}
			}
			synchronized (controllersData) {
				update();
			}
			synchronized (waitObject) {
				waitObject.notifyAll();
			}
		}
	}
	private void update(){
		updateData = false;
		int pos = 0;
		
		stateByte = controllersData[pos++];
		connectedControllers = controllersData[pos++];
		int j = 0;
		for(int i = 0; i < MAX_CONTROLLERS; i++){
			if((connectedControllers & (0x01 << (i + 1))) == 0)
				continue;
			for(j = 0; j < CONTROLLER_AXES; j++)
				controllerAxes[i][j] = controllersData[pos++];
			
			controllerButtons[i].count = controllersData[pos++];
			controllerButtons[i].buttons = 0;
			controllerButtons[i].buttons = ((short) controllersData[pos++]);
			controllerButtons[i].buttons |= (controllersData[pos++] << (controllerButtons[i].count >> 2));
			
			controllerPovs[i] = ((short) controllersData[pos++]);
			controllerPovs[i] |= (controllersData[pos++] << 4);
		}
		
		updateData = true;
	}
	
	@Override
	public void newData(byte[] data) {
		if(!updateData) return;
		
		if(data.length != controllersData.length)
			controllersData = Arrays.copyOf(data, data.length);
		else System.arraycopy(data, 0, controllersData, 0, data.length);
		
		synchronized (recieveObject) {
			recieveObject.notifyAll();	
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
		attached = true;
	}
	@Override
	public void onConnectionLost() {
		attached = false;
	}
	
	public void waitForData(){
		waitForData(0);
	}
	public void waitForData(long timeout){
		synchronized (waitObject) {
			try {
				waitObject.wait(timeout);
			} catch (InterruptedException e) {
			}	
		}
	}
	
	public boolean isStickConnected(int stick){
		if(stick < 0 || stick >= MAX_CONTROLLERS)
			throw new IndexOutOfBoundsException("Stick index is out of bounds: "+stick);
		return (connectedControllers & (0x01 << (stick + 1))) != 0;
	}
	public int getStickCount(){
		int count = 0;
		for (int i = 1; i <= MAX_CONTROLLERS; i++) {
			if((connectedControllers & (0x01 << i)) != 0)
				count++;
		}
		return count;
	}
	public double getStickAxis(int stick, int axis){
		if(!isStickConnected(stick)){
			FlashUtil.getLog().reportWarning("Controller "+stick+" is not connected");
			return 0;
		}
		if(axis < 0 || axis >= CONTROLLER_AXES)
			throw new IndexOutOfBoundsException("Axis index is out of bounds: "+axis);
		
		byte data = controllerAxes[stick][axis];
		return data < 0? data / 128.0 : data / 127.0;
	}
	public boolean getStickButton(int stick, byte button){
		if(!isStickConnected(stick)){
			FlashUtil.getLog().reportWarning("Controller "+stick+" is not connected");
			return false;
		}
		if(button < 1)
			throw new IndexOutOfBoundsException("Button index is out of bounds: "+button);
		if(button > controllerButtons[stick].count){
			FlashUtil.getLog().reportWarning("Button "+button+" on controller "+stick+" is unavailable");
			return false;
		}
		
		return (controllerButtons[stick].buttons & (0x01 << button)) != 0;
	}
	public int getButtonsCount(int stick){
		if(!isStickConnected(stick)){
			FlashUtil.getLog().reportWarning("Controller "+stick+" is not connected");
			return 0;
		}
		
		return controllerButtons[stick].count;
	}
	public int getStickPOV(int stick){
		if(!isStickConnected(stick)){
			FlashUtil.getLog().reportWarning("Controller "+stick+" is not connected");
			return 0;
		}
		
		return (int)controllerPovs[stick];
	}
	
	public boolean isDisabled(){
		return stateByte == 0 || (!attached);
	}
	public boolean isCSAttached(){
		return attached;
	}
	public int getState(){
		return stateByte;
	}
}
