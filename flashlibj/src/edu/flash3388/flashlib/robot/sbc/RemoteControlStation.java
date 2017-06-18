package edu.flash3388.flashlib.robot.sbc;

import java.util.ArrayList;

import edu.flash3388.flashlib.communications.Sendable;
import edu.flash3388.flashlib.util.FlashUtil;
import net.java.games.input.Component;
import net.java.games.input.Component.Identifier;
import net.java.games.input.Controller;

public class RemoteControlStation extends Sendable{
	
	public static interface Listener{
		void onStateConfirm(byte state);
	}
	private static class ControllerData{
		private Controller controller;
		private Component[] axes, buttons;
		private Component pov;
		
		ControllerData(Controller controller){
			this.controller = controller;
			
			ArrayList<Component> axes = new ArrayList<Component>();
			ArrayList<Component> buttons = new ArrayList<Component>();
			
			for (Component component : controller.getComponents()) {
				if(component.getIdentifier() == Identifier.Axis.POV)
					pov = component;
				else if(component.isAnalog())
					axes.add(component);
				else buttons.add(component);
			}
			
			this.axes = new Component[axes.size()];
			axes.toArray(this.axes);
			
			this.buttons = new Component[buttons.size()];
			buttons.toArray(this.buttons);
		}
		
		void update(){
			controller.poll();
		}
		
		int getAxesCount(){
			return axes.length < SbcControlStation.CONTROLLER_AXES? axes.length : SbcControlStation.CONTROLLER_AXES;
		}
		int getButtonCount(){
			return buttons.length;
		}
		
		byte[] getAxesData(){
			byte[] axes = new byte[SbcControlStation.CONTROLLER_AXES];
			int size = getAxesCount();
			for (int i = 0; i < size; i++) 
				axes[i] = (byte) (this.axes[i].getPollData() * 127);
			return axes;
		}
		short getButtonsData(){
			int count = getButtonCount();
			short data = 0;
			for (int i = 0; i < count; i++)
				data |= (((buttons[i].getPollData() == 1.0f)? 1 : 0) << (i));
			return data;
		}
		short getPovData(){
			return (short) (pov.getPollData() * 360);
		}
	}
	private static class UpdateTask implements Runnable{

		private RemoteControlStation cs;
		
		public UpdateTask(RemoteControlStation cs){
			this.cs = cs;
		}
		
		@Override
		public void run() {
			cs.task();
		}
	}

	private ControllerData[] controllers = new ControllerData[SbcControlStation.MAX_CONTROLLERS];
	private byte[] controllersData = new byte[SbcControlStation.CONTROLLER_DATA_SIZE];
	private byte stateByte = 0;
	private byte controllerCount = 0;
	private boolean updateData = true;
	private boolean stop = false;
	private boolean attached = false;
	
	private UpdateTask upTask;
	private Thread csThread;
	
	private Listener listener;
	
	public RemoteControlStation(String name) {
		super(name, -1, SbcSendableType.CONSTROL_STATION);
		
		upTask = new UpdateTask(this);
		csThread = new Thread(upTask, "CS-Update");
		csThread.setPriority((Thread.MAX_PRIORITY + Thread.NORM_PRIORITY) / 2);
		csThread.start();
	}
	
	private void task(){
		while(!stop){
			synchronized (controllersData) {
				updateControllerData();
			}
			FlashUtil.delay(50);
		}
	}
	void stop(){
		stop = true;
		stateByte = 0;
	}
	
	private byte getControlCountByte(){
		byte count = 0;
		for (int i = 0; i < controllers.length; i++) {
			if(controllers[i] != null)
				count |= 1 << i;
		}
		return count;
	}
	private void updateControllerData(){
		updateData = false;
		int pos = 0;
		controllersData[pos++] = stateByte;
		controllersData[pos++] = getControlCountByte();
		for (int i = 0; i < SbcControlStation.MAX_CONTROLLERS; i++) {
			if((controllersData[1] & (0x01 << (i + 1))) == 0)
				continue;
			
			controllers[i].update();
			byte[] axesData = controllers[i].getAxesData(); 
			for(int j = 0; j < axesData.length; j++)
				controllersData[pos++] = axesData[j];
			
			controllersData[pos++] = (byte) controllers[i].getButtonCount();
			short data = controllers[i].getButtonsData();
			controllersData[pos++] = (byte) data;
			controllersData[pos++] = (byte) (data >> 8);
			
			data = controllers[i].getPovData();
			controllersData[pos++] = (byte) data;
			controllersData[pos++] = (byte) (data >> 8);
		}
		updateData = true;
	}
	
	@Override
	public void newData(byte[] data) {
		if(data[0] == stateByte && listener != null)
			listener.onStateConfirm(stateByte);
	}
	@Override
	public byte[] dataForTransmition() {
		updateData = false;
		return FlashUtil.copy(controllersData);
	}
	@Override
	public boolean hasChanged() {
		return updateData;
	}

	@Override
	public void onConnection() {
		attached = true;
	}
	@Override
	public void onConnectionLost() {
		attached = false;
	}
	
	public boolean isAttached(){
		return attached;
	}

	public void setState(byte state){
		this.stateByte = state;
	}
	public byte getState(){
		return stateByte;
	}
	
	public int getControllersCount(){
		return controllerCount;
	}
	public void setController(Controller controller, int i){
		if(i < 0 || i > SbcControlStation.MAX_CONTROLLERS-1)
			throw new IllegalArgumentException("Controller index out of bounds");
		
		controllers[i] = new ControllerData(controller);
		controllerCount++;
	}
	public void removeController(int i){
		if(i < 0 || i > SbcControlStation.MAX_CONTROLLERS-1)
			throw new IllegalArgumentException("Controller index out of bounds");
		
		controllers[i] = null;
		controllerCount--;
	}
	
	public void setListener(Listener listener){
		this.listener = listener;
	}
}
