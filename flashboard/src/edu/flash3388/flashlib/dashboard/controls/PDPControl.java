package edu.flash3388.flashlib.dashboard.controls;

import java.util.Enumeration;
import java.util.Vector;

import edu.flash3388.flashlib.util.FlashUtil;
import edu.flash3388.flashlib.communications.SendableException;
import edu.flash3388.flashlib.dashboard.Displayable;
import edu.flash3388.flashlib.flashboard.FlashboardSendableType;
import edu.flash3388.flashlib.math.Mathf;

public class PDPControl extends Displayable{

	private static Vector<PDPControl> pdps = new Vector<PDPControl>();
	
	private double[] channelsCurrent = new double[16];
	private double allVoltage = 0, allCurrent = 0, temperature = 0;
	private boolean minimumSend = false, maximumSend = false, updatesend = false;
	private byte[] sendBytes = new byte[1];
	
	public PDPControl(String name) {
		super(name, FlashboardSendableType.PDP);
		pdps.addElement(this);
	}

	public double getAllCurrent(){
		return allCurrent;
	}
	public double getAllVoltage(){
		return allVoltage;
	}
	public double getTemperature(){
		return temperature;
	}
	public double getCurrent(int channel){
		return channelsCurrent[channel];
	}
	public int getChannels(){
		return channelsCurrent.length;
	}
	public void updateSend(boolean full){
		minimumSend = !full;
		maximumSend = full;
		updatesend = true;
	}
	public void disableSend(){
		minimumSend = maximumSend = false;
		updatesend = true;
	}
	
	@Override
	public void newData(byte[] bytes) throws SendableException {
		if(bytes.length < 1) return;
		if(bytes.length <= 26 && bytes[0] == 0){
			int pos = 2;
			double v = FlashUtil.toDouble(bytes, pos); pos+=8;
			double t = FlashUtil.toDouble(bytes, pos); pos+=8;
			double c = FlashUtil.toDouble(bytes, pos);
			allVoltage = Mathf.roundDecimal(v);
			temperature = Mathf.roundDecimal(t);
			allCurrent = Mathf.roundDecimal(c);
		}
		else if(bytes.length <= 154 && bytes[0] == 1){
			int channels = bytes[1];
			if(channels != channelsCurrent.length)
				channelsCurrent = new double[channels];
			int pos = 2;
			double v = Mathf.roundDecimal(FlashUtil.toDouble(bytes, pos)); pos+=8;
			double t = Mathf.roundDecimal(FlashUtil.toDouble(bytes, pos)); pos+=8;
			double c = Mathf.roundDecimal(FlashUtil.toDouble(bytes, pos)); pos+=8;
			
			for (int i = 0; i < channelsCurrent.length; i++) {
				channelsCurrent[i] = Mathf.roundDecimal(FlashUtil.toDouble(bytes, pos));
				pos += 8;
			}
			
			allVoltage = v;
			temperature = t;
			allCurrent = c;
		}
	}
	@Override
	public byte[] dataForTransmission() throws SendableException {
		if(maximumSend) sendBytes[0] = 2;
		else if (minimumSend) sendBytes[1] = 1;
		else sendBytes[0] = 0;
		updatesend = false;
		return sendBytes;
	}
	@Override
	public boolean hasChanged() {
		return updatesend;
	}
	@Override
	public void onConnection() {}
	@Override
	public void onConnectionLost() {}
	
	
	public static Enumeration<PDPControl> getBoards(){
		return pdps.elements();
	}
	public static PDPControl get(int i){
		return pdps.get(i);
	}
	public static void resetBoards(){
		for(Enumeration<PDPControl> pdpEnum = pdps.elements(); pdpEnum.hasMoreElements();)
			pdpEnum.nextElement().disableSend();
		pdps.clear();
	}
}
