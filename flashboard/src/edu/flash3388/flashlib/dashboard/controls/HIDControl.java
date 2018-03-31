package edu.flash3388.flashlib.dashboard.controls;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import edu.flash3388.flashlib.communications.Sendable;
import edu.flash3388.flashlib.flashboard.FlashboardSendableType;
import edu.flash3388.flashlib.flashboard.FlashboardHIDControl;
import edu.flash3388.flashlib.util.FlashUtil;

import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.Controller.Type;
import net.java.games.input.ControllerEnvironment;

public class HIDControl extends Sendable implements Runnable{

	private static class HIDData{
		String name;
		Controller controller;
		
		HIDAxis[] axes;
		HIDButton[] buttons;
		HIDPOV[] povs;
		
		public HIDData(String name, HIDAxis[] axes, HIDButton[] buttons, HIDPOV[] povs) {
			this.axes = axes;
			this.buttons = buttons;
			this.povs = povs;
			this.name = name;
		}
	}
	private static class HIDAxis{
		private Component component;
		
		public HIDAxis(Component component) {
			this.component = component;
		}
		
		public float getValue(){
			return component.getPollData();
		}
	}
	private static class HIDButton{
		private Component component;
		
		public HIDButton(Component component) {
			this.component = component;
		}
		
		public boolean getValue(){
			return component.getPollData() == 1.0f;
		}
	}
	private static class HIDPOV{
		private Component component;
		
		public HIDPOV(Component component) {
			this.component = component;
		}
		
		public int getValue(){
			float data = component.getPollData();
			if(data < 0)
				return -1;
			return (int) (data * 360);
		}
	}
	
	private static final int UPDATE_RATE = 50;
	
	private ArrayList<Byte> dataList = new ArrayList<Byte>();
	private byte[] sendDataBuffer = new byte[0];
	
	private HIDData[] hidData = new HIDData[FlashboardHIDControl.MAX_PORT_COUNT];
	private int controllerCount = 0;
	
	private long lastUpdate = -1;
	
	private boolean updateData = false;
	private boolean sendData = false;
	
	private int times = 0;
	
	private Constructor<ControllerEnvironment> controllerEnvironmentConstructor;
	
	public HIDControl() {
		super("hid control", FlashboardSendableType.JOYSTICK);
		
		loadControllers();
	}

	@SuppressWarnings("unchecked")
	private ControllerEnvironment createDefaultEnvironment(){
		if(controllerEnvironmentConstructor == null){
			try {
				controllerEnvironmentConstructor = (Constructor<ControllerEnvironment>)
						Class.forName("net.java.games.input.DefaultControllerEnvironment")
						.getDeclaredConstructors()[0];
				
				controllerEnvironmentConstructor.setAccessible(true);
			} catch (SecurityException | ClassNotFoundException e) {
			}
		}
		
		
		
		try {
			return controllerEnvironmentConstructor.newInstance();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			return null;
		}
	}
	
	private void updateControllers(){
		for (int i = 0; i < hidData.length; i++) {
			if(hidData[i] == null)
				continue;
			if(!hidData[i].controller.poll()){
				hidData[i] = null;
				controllerCount--;
			}
		}
	}
	private void loadControllers(){
		Controller[] controllers = createDefaultEnvironment().getControllers();
		
		for (int i = 0; i < hidData.length; i++) {
			hidData[i] = null;
		}
		
		controllerCount = 0;
		for (int i = 0; i < controllers.length; i++) {
			if(!controllers[i].getType().equals(Type.GAMEPAD) && 
					!controllers[i].getType().equals(Type.STICK))
				continue;
			
			hidData[controllerCount++] = loadController(controllers[i]);
			if(controllerCount == hidData.length)
				break;
		}
	}
	private HIDData loadController(Controller controller){
		ArrayList<HIDAxis> axes = new ArrayList<HIDAxis>();
		ArrayList<HIDButton> buttons = new ArrayList<HIDButton>();
		ArrayList<HIDPOV> povs = new ArrayList<HIDPOV>();
		
		Component[] componenets = controller.getComponents();
		
		for (int i = 0; i < componenets.length; i++) {
			if(componenets[i].isAnalog()){
				if(componenets[i].getIdentifier().equals(Component.Identifier.Axis.POV)){
					povs.add(new HIDPOV(componenets[i]));
				}else{
					axes.add(new HIDAxis(componenets[i]));
				}
			}else{
				buttons.add(new HIDButton(componenets[i]));
			}
		}
		
		HIDData data = new HIDData(
				controller.getName(),
				axes.toArray(new HIDAxis[axes.size()]),
				buttons.toArray(new HIDButton[buttons.size()]),
				povs.toArray(new HIDPOV[povs.size()])
				);
		data.controller = controller;
		return data;
	}
	private void catchDataForSending(){
		dataList.clear();
		byte[] buttonData = new byte[4];
		
		dataList.add(FlashboardHIDControl.JOYSTICK_DATA);
		for (int i = 0; i < hidData.length; i++) {
			if(hidData[i] == null)
				continue;
			
			dataList.add((byte) i);
			
			//axes data
			dataList.add((byte) hidData[i].axes.length);
			for (int j = 0; j < hidData[i].axes.length; j++) {
				float val = hidData[i].axes[j].getValue();
				dataList.add((byte) (val < 0? val * 128 : val * 127));
			}
			
			//pov data
			dataList.add((byte) hidData[i].povs.length);
			for (int j = 0; j < hidData[i].povs.length; j++) {
				int val = hidData[i].povs[j].getValue();
				
				dataList.add((byte) (val & 0xff));
				dataList.add((byte) ((val >> 8) & 0xff));
			}
			
			//button data
			dataList.add((byte) hidData[i].buttons.length);
			int buttons = 0;
			for (int j = 0; j < hidData[i].buttons.length; j++) {
				buttons |= ((hidData[i].buttons[j].getValue()? 1 : 0) << j);
			}
			FlashUtil.fillByteArray(buttons, buttonData);
			
			for (int j = 0; j < buttonData.length; j++) {
				dataList.add(buttonData[j]);
			}
		}
		
		if(dataList.size() != sendDataBuffer.length){
			sendDataBuffer = new byte[dataList.size()];
		}
		
		for (int i = 0; i < sendDataBuffer.length; i++) {
			sendDataBuffer[i] = dataList.get(i);
		}
	}
	
	public void setEnabled(boolean enabled){
		this.updateData = enabled;
	}
	
	public String getHIDName(int channel){
		if(channel < 0 || channel >= hidData.length)
			return null;
		return hidData[channel].name;
	}
	
	public boolean isHIDConnected(int channel){
		if(channel < 0 || channel >= hidData.length)
			return false;
		return hidData[channel] != null;
	}
	
	public int getHIDAxisCount(int channel){
		if(channel < 0 || channel >= hidData.length)
			return 0;
		return hidData[channel].axes.length;
	}
	public int getHIDButtonCount(int channel){
		if(channel < 0 || channel >= hidData.length)
			return 0;
		return hidData[channel].buttons.length;
	}
	public int getHIDPOVCount(int channel){
		if(channel < 0 || channel >= hidData.length)
			return 0;
		return hidData[channel].povs.length;
	}
	
	public double getHIDAxis(int channel, int axis){
		if(channel < 0 || channel >= hidData.length)
			return 0.0;
		if(axis < 0 || axis >= hidData[channel].axes.length)
			return 0.0;
		
		return hidData[channel].axes[axis].getValue();
	}
	public boolean getHIDButton(int channel, int button){
		if(channel < 0 || channel >= hidData.length)
			return false;
		if(button < 0 || button >= hidData[channel].buttons.length)
			return false;
		
		return hidData[channel].buttons[button].getValue();
	}
	public int getHIDPOV(int channel, int pov){
		if(channel < 0 || channel >= hidData.length)
			return 0;
		if(pov < 0 || pov >= hidData[channel].povs.length)
			return 0;
		
		return hidData[channel].povs[pov].getValue();
	}
	
	@Override
	public void run() {
		if(!updateData && !isRemoteAttached())
			return;
		
		if(lastUpdate < 1 || FlashUtil.millis() - lastUpdate >= UPDATE_RATE){
			if(++times >= 10){
				loadControllers();
				times = 0;
			}
			updateControllers();
			
			lastUpdate = FlashUtil.millis();
			
			
			if(isRemoteAttached()){
				catchDataForSending();
				sendData = true;
			}
		}
	}
	@Override
	public void newData(byte[] data) {
	}
	@Override
	public byte[] dataForTransmition() {
		sendData = false;
		return sendDataBuffer;
	}
	@Override
	public boolean hasChanged() {
		return sendData;
	}

	@Override
	public void onConnection() {
		sendData = false;
	}
	@Override
	public void onConnectionLost() {
	}
}

/*if(controllerCount > 0){
for (int i = 0; i < hidData.length; i++) {
	if(hidData[i] == null)
		continue;
	
	boolean inarray = false;
	for (int j = 0; j < controllers.length; j++) {
		if(controllers[j] == null)
			continue;
		if(!controllers[j].getType().equals(Type.GAMEPAD) && !controllers[j].getType().equals(Type.STICK)){
			controllers[j] = null;
			continue;
		}
		
		if(controllers[j].getPortNumber() == hidData[i].port){
			inarray = true;
			controllers[j] = null;
			break;
		}
	}
	
	if(!inarray){
		hidData[i] = null;
		controllerCount--;
	}
}
}

if(controllerCount < hidData.length){
for (int i = 0; i < controllers.length; i++) {
	if(controllers[i] == null)
		continue;
	if(!controllers[i].getType().equals(Type.GAMEPAD) && !controllers[i].getType().equals(Type.STICK))
		continue;
	
	int index = 0;
	for (int j = 0; j < hidData.length; j++) {
		if(hidData[j] == null){
			index = j;
			break;
		}
	}
	
	hidData[index] = loadController(controllers[i]);
	controllerCount++;
}
}*/
