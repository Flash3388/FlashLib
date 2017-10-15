package edu.flash3388.flashlib.robot.frc;

import edu.flash3388.flashlib.flashboard.FlashboardControl;
import edu.flash3388.flashlib.flashboard.FlashboardSendableType;
import edu.flash3388.flashlib.util.FlashUtil;
import edu.wpi.first.wpilibj.PowerDistributionPanel;

/**
 * Wrapper for the PowerDistributionPanel. Allows attachment to the flashboard for data tracking.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public class PDP extends FlashboardControl{
	
	public static final int CHANNELS = 16;
	
	private boolean send = false, sendAll = false;
	private byte[] minimumD = new byte[26], allD = new byte[26 + CHANNELS*8];
	private long lastSend = 0;
	
	private PowerDistributionPanel pdp;
	private double voltage = 0;
	
	/**
	 * Initializes the wrapper for a PDP at the given module.
	 * @param module the module
	 */
	public PDP(int module){
		super("PDP"+module, FlashboardSendableType.PDP);
		pdp = new PowerDistributionPanel(module);
		
		voltage = pdp.getVoltage();
	}
	/**
	 * Initializes the wrapper for a PDP at the default module: 0.
	 */
	public PDP(){
		this(0);
	}

	/**
	 * Gets the wrapped {@link PowerDistributionPanel} object.
	 * @return the wrapped object
	 */
	public PowerDistributionPanel getPanel(){
		return pdp;
	}
	
	/**
	 * Gets the voltage measured by the PDP.
	 * @return voltage in volts
	 * @see PowerDistributionPanel#getVoltage()
	 */
	public double getVoltage(){
		try{
			voltage = pdp.getVoltage();
		}catch(Throwable t){}
		return voltage;
	}
	/**
	 * Gets the total current measured by the PDP.
	 * @return current in Ampere
	 * @see PowerDistributionPanel#getTotalCurrent()
	 */
	public double getTotalCurrent(){
		return pdp.getTotalCurrent();
	}
	/**
	 * Gets the total power measured by the PDP.
	 * @return power in watts
	 * @see PowerDistributionPanel#getTotalPower()
	 */
	public double getTotalPower(){
		return pdp.getTotalPower();
	}
	/**
	 * Gets the total energy measured by the PDP.
	 * @return energy in joules
	 * @see PowerDistributionPanel#getTotalEnergy()
	 */
	public double getTotalEnergy(){
		return pdp.getTotalEnergy();
	}
	/**
	 * Gets the current measured by the PDP for a channel.
	 * @param channel the channel index
	 * @return current in Ampere
	 * @see PowerDistributionPanel#getCurrent(int)
	 */
	public double getCurrent(int channel){
		return pdp.getCurrent(channel);
	}
	/**
	 * Gets the voltage measured by the PDP.
	 * @return temperature in celsius
	 * @see PowerDistributionPanel#getTemperature()
	 */
	public double getTemperature(){
		return pdp.getTemperature();
	}
	
	@Override
	public void newData(byte[] data) {
		if(data.length < 1) return;
		if(data[0] == 1)
			this.setMinSend();
		else if(data[0] == 2)
			this.setMaxSend();
		else this.stopSend();
	}
	private int fillWithMin(byte[] minD, int pos){
		FlashUtil.fillByteArray(pdp.getVoltage(), pos, minD); pos+=8;
		FlashUtil.fillByteArray(pdp.getTemperature(), pos, minD); pos+=8;
		FlashUtil.fillByteArray(pdp.getTotalCurrent(), pos, minD); pos+=8;
		return pos;
	}
	private int fillWithMax(byte[] maxD, int pos){
		for(int i = 0; i < CHANNELS; i++){
			FlashUtil.fillByteArray(pdp.getCurrent(i), pos, maxD);
			pos += 8;
		}
		return pos;
	}
	
	@Override
	public byte[] dataForTransmition() {
		byte[] data = null;
		if(send){
			minimumD[0] = 0;
			minimumD[1] = 0;
			fillWithMin(minimumD, 2);
			data = minimumD;
		}else{
			allD[0] = 1;
			allD[1] = CHANNELS;
			int pos = fillWithMin(allD, 2);
			fillWithMax(allD, pos);
			data = allD;
		}
		lastSend = FlashUtil.millis();
		return data;
	}
	@Override
	public boolean hasChanged() {
		return (send || sendAll) && FlashUtil.millis() - lastSend >= 100;
	}
	@Override
	public void onConnection() {
		send = false;
		sendAll = false;
	}
	@Override
	public void onConnectionLost() {
	}
	
	public void setMinSend(){
		send = true;
		sendAll = false;
	}
	public void setMaxSend(){
		send = false;
		sendAll = true;
	}
	public void stopSend(){
		send = sendAll = false;
	}
}
