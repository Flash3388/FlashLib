package edu.flash3388.flashlib.flashboard;

import edu.flash3388.flashlib.communications.Sendable;
import edu.flash3388.flashlib.robot.devices.DoubleDataSource;
import edu.flash3388.flashlib.util.ConstantsHandler;
import edu.flash3388.flashlib.util.FlashUtil;

public class PidTuner extends Sendable{

	public static final byte K_UPDATE = 0x1;
	public static final byte SP_UPDATE = 0x2;
	public static final byte CV_UPDATE = 0x3;
	public static final byte RUN_UPDATE = 0x4;
	
	private String kp, ki, kd, setpoint;
	private DoubleDataSource pval, ival, dval, spval, currentValue;
	
	private boolean update = false;
	private double lastP, lastI, lastD, lastSetPoint, lastValue;
	
	public PidTuner(String name, String kp, String ki, String kd, String setPoint, DoubleDataSource currentValue) {
		super(name, FlashboardSendableType.PIDTUNER);
		
		this.kp = kp;
		this.ki = ki;
		this.kd = kd;
		this.setpoint = setPoint;
		this.currentValue = currentValue;
		
		pval = ConstantsHandler.addNumber(kp, 0);
		ival = ConstantsHandler.addNumber(ki, 0);
		dval = ConstantsHandler.addNumber(kd, 0);
		spval = ConstantsHandler.addNumber(setPoint, 0);
	}

	public boolean isEnabled(){
		return update;
	}
	
	@Override
	public void newData(byte[] data) {
		if(data[0] == K_UPDATE){
			lastP = FlashUtil.toDouble(data, 1);
			lastI = FlashUtil.toDouble(data, 9);
			lastD = FlashUtil.toDouble(data, 17);
			
			ConstantsHandler.putNumber(kp, lastP);
			ConstantsHandler.putNumber(ki, lastI);
			ConstantsHandler.putNumber(kd, lastD);
		}
		else if(data[0] == SP_UPDATE){
			lastSetPoint = FlashUtil.toDouble(data, 1);
			ConstantsHandler.putNumber(setpoint, lastSetPoint);
		}
		else if(data[0] == RUN_UPDATE){
			update = data[1] == 1;
		}
	}
	@Override
	public byte[] dataForTransmition() {
		if(lastSetPoint != spval.get()){
			lastSetPoint = spval.get();
			
			byte[] data = new byte[9];
			data[0] = SP_UPDATE;
			FlashUtil.fillByteArray(lastSetPoint, 1, data);
			return data;
		}
		if(lastValue != currentValue.get()){
			lastValue = currentValue.get();
			
			byte[] data = new byte[9];
			data[0] = CV_UPDATE;
			FlashUtil.fillByteArray(lastValue, 1, data);
			return data;
		}
		if(lastP != pval.get() || lastI != ival.get() || lastD != dval.get()){
			lastP = pval.get();
			lastI = ival.get();
			lastD = dval.get();
			
			byte[] data = new byte[25];
			data[0] = K_UPDATE;
			FlashUtil.fillByteArray(lastP, 1, data);
			FlashUtil.fillByteArray(lastI, 9, data);
			FlashUtil.fillByteArray(lastD, 17, data);
			
			return data;
		}

		return null;
	}
	@Override
	public boolean hasChanged() {
		return update;
	}
	@Override
	public void onConnection() {
		update = false;
		
		lastP = pval.get() - 1;
		lastI = ival.get() - 1;
		lastD = dval.get() - 1;
		
		lastSetPoint = spval.get() - 1;
		lastValue = currentValue.get() - 1;
	}
	@Override
	public void onConnectionLost() {
		update = false;
	}
}
