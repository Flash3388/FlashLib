package edu.flash3388.flashlib.robot.sbc;

import java.util.Arrays;

import edu.flash3388.flashlib.communications.Sendable;
import edu.flash3388.flashlib.util.FlashUtil;

public final class SbcControlStation{
	
	public static class CsStateSelector implements StateSelector{

		private SbcControlStation cs;
		
		public CsStateSelector(SbcControlStation cs){
			this.cs = cs;
		}
		
		@Override
		public byte getState() {
			return cs.getState();
		}
	}
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
	private static class CsSendable extends Sendable{
		
		private SbcControlStation cs;
		private boolean confirm = false;
		private boolean updateData = true;
		private Object recieveObject = new Object();
		
		public CsSendable(SbcControlStation cs){
			super("ControlStation", SbcSendableType.CONSTROL_STATION);
			this.cs = cs;
		}
		
		@Override
		public void newData(byte[] data) {
			if(!updateData) return;
			
			if(data.length != cs.controllersData.length)
				cs.controllersData = Arrays.copyOf(data, data.length);
			else System.arraycopy(data, 0, cs.controllersData, 0, data.length);
			
			synchronized (recieveObject) {
				recieveObject.notifyAll();	
			}
		}
		@Override
		public byte[] dataForTransmition() {
			return new byte[]{cs.stateByte};
		}
		@Override
		public boolean hasChanged() {
			return confirm;
		}

		@Override
		public void onConnection() {
			cs.attached = true;
			cs.stateByte = StateSelector.STATE_DISABLED;
		}
		@Override
		public void onConnectionLost() {
			cs.attached = false;
			cs.stateByte = StateSelector.STATE_DISABLED;
		}
	}
	
	public static final byte MAX_CONTROLLERS = 3;
	public static final byte CONTROLLER_AXES = 6;
	static final byte CONTROLLER_DATA_SIZE = (5 + CONTROLLER_AXES) * MAX_CONTROLLERS + 2;
	
	private static SbcControlStation instance;
	
	private byte[][] controllerAxes = new byte[MAX_CONTROLLERS][CONTROLLER_AXES];
	private short[] controllerPovs = new short[MAX_CONTROLLERS];
	private ControllerButtons[] controllerButtons = new ControllerButtons[MAX_CONTROLLERS];
	
	private byte[] controllersData = new byte[CONTROLLER_DATA_SIZE];
	private byte connectedControllers = 0;
	private byte stateByte = 0;
	private boolean stop = false;
    private boolean attached = false;
	
	private Object waitObject = new Object();
	
	private UpdateTask upTask;
	private Thread csThread;
	private CsSendable csSendable;
	
	SbcControlStation() {
		for (int i = 0; i < controllerButtons.length; i++)
			controllerButtons[i] = new ControllerButtons();
		
		csSendable = new CsSendable(this);
		upTask = new UpdateTask(this);
		csThread = new Thread(upTask, "CS-Update");
		csThread.setPriority((Thread.MAX_PRIORITY + Thread.NORM_PRIORITY) / 2);
		csThread.start();
	}
	void stop(){
		stop = true;
		stateByte = 0;
	}
	Sendable getSendable(){
		return csSendable;
	}
	
	private void task(){
		while(!stop){
			synchronized (csSendable.recieveObject) {
				try {
					csSendable.recieveObject.wait();
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
		csSendable.updateData = false;
		int pos = 0;
		
		byte b = controllersData[pos++];
		if(stateByte != b){
			stateByte = b;
			csSendable.confirm = true;
		}
		
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
			controllerButtons[i].buttons |= (controllersData[pos++] << 8);
			
			controllerPovs[i] = ((short) controllersData[pos++]);
			controllerPovs[i] |= (controllersData[pos++] << 8);
		}
		
		csSendable.updateData = true;
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
	public byte getState(){
		return stateByte;
	}
	
	static void init(){
		instance = new SbcControlStation();
	}
	public static SbcControlStation getInstance(){
		if(instance == null)
			throw new IllegalStateException("Sbc control station was not initialized");
		return instance;
	}
}
