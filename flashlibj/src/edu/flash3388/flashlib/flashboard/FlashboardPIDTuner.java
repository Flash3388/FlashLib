package edu.flash3388.flashlib.flashboard;

import edu.flash3388.flashlib.robot.PIDSource;
import edu.flash3388.flashlib.util.FlashUtil;
import edu.flash3388.flashlib.util.beans.DoubleProperty;

/**
 * PIDTuner provides real-time tracking and tuning of pid controllers and loops. It is possible to 
 * edit only specific pid controller values using the tuner window on the flashboard.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.1
 */
public class FlashboardPIDTuner extends FlashboardControl{

	public static final byte K_UPDATE = 0x1;
	public static final byte SP_UPDATE = 0x2;
	public static final byte CV_UPDATE = 0x3;
	public static final byte RUN_UPDATE = 0x4;
	public static final byte SLIDER_UPDATE = 0x5;
	
	private DoubleProperty kp, ki, kd, kf, setpoint;
	private PIDSource currentValue;
	private double maxValue;
	private int ticks;
	
	private boolean update = false, sliderValuesUpdated = false;
	private double lastP, lastI, lastD, lastF, lastSetPoint, lastValue;
	
	public FlashboardPIDTuner(String name, DoubleProperty kp, DoubleProperty ki, DoubleProperty kd, DoubleProperty kf, 
			DoubleProperty setPoint, PIDSource currentValue,
			double maxValue, int ticks) {
		super(name, FlashboardSendableType.PIDTUNER);
		
		this.kp = kp;
		this.ki = ki;
		this.kd = kd;
		this.kf = kf;
		this.setpoint = setPoint;
		this.currentValue = currentValue;
		this.maxValue = maxValue;
		this.ticks = ticks;
	}
	public FlashboardPIDTuner(String name, DoubleProperty kp, DoubleProperty ki, DoubleProperty kd, DoubleProperty kf, 
			DoubleProperty setPoint, PIDSource currentValue){
		this(name, kp, ki, kd, kf, setPoint, currentValue, 10.0, 1000);
	}

	public boolean isEnabled(){
		return update;
	}
	
	public DoubleProperty kpProperty(){
		return kp;
	}
	public DoubleProperty kiProperty(){
		return ki;
	}
	public DoubleProperty kdProperty(){
		return kd;
	}
	public DoubleProperty kfProperty(){
		return kf;
	}
	public DoubleProperty setPointProperty(){
		return setpoint;
	}
	
	public PIDSource valueSource(){
		return currentValue;
	}
	
	@Override
	public void newData(byte[] data) {
		if(data[0] == K_UPDATE){
			lastP = FlashUtil.toDouble(data, 1);
			lastI = FlashUtil.toDouble(data, 9);
			lastD = FlashUtil.toDouble(data, 17);
			lastF = FlashUtil.toDouble(data, 25);
			
			kp.set(lastP);
			ki.set(lastI);
			kd.set(lastD);
			kf.set(lastF);
		}
		else if(data[0] == SP_UPDATE){
			lastSetPoint = FlashUtil.toDouble(data, 1);
			setpoint.set(lastSetPoint);
		}
		else if(data[0] == RUN_UPDATE){
			update = data[1] == 1;
		}
	}
	@Override
	public byte[] dataForTransmition() {
		if(sliderValuesUpdated){
			sliderValuesUpdated = false;
			byte[] data = new byte[13];
			data[0] = SLIDER_UPDATE;
			FlashUtil.fillByteArray(maxValue, 1, data);
			FlashUtil.fillByteArray(ticks, 9, data);
			return data;
		}
		
		double checkvalue = 0.0;
		
		checkvalue = setpoint.get();
		if(lastSetPoint != checkvalue){
			lastSetPoint = checkvalue;
			
			byte[] data = new byte[9];
			data[0] = SP_UPDATE;
			FlashUtil.fillByteArray(lastSetPoint, 1, data);
			return data;
		}
		
		checkvalue = currentValue.pidGet();
		if(lastValue != checkvalue){
			lastValue = checkvalue;
			
			byte[] data = new byte[9];
			data[0] = CV_UPDATE;
			FlashUtil.fillByteArray(lastValue, 1, data);
			return data;
		}
		if(lastP != kp.get() || lastI != ki.get() || lastD != kd.get() || lastF != kf.get()){
			lastP = kp.get();
			lastI = ki.get();
			lastD = kd.get();
			lastF = kf.get();
			
			byte[] data = new byte[33];
			data[0] = K_UPDATE;
			FlashUtil.fillByteArray(lastP, 1, data);
			FlashUtil.fillByteArray(lastI, 9, data);
			FlashUtil.fillByteArray(lastD, 17, data);
			FlashUtil.fillByteArray(lastF, 25, data);
			
			return data;
		}

		return null;
	}
	@Override
	public boolean hasChanged() {
		return update || sliderValuesUpdated;
	}
	@Override
	public void onConnection() {
		update = false;
		
		lastP = kp.get() - 1;
		lastI = ki.get() - 1;
		lastD = kd.get() - 1;
		lastF = kf.get() - 1;
		
		lastSetPoint = setpoint.get() - 1;
		lastValue = currentValue.pidGet() - 1;
		
		sliderValuesUpdated = true;
	}
	@Override
	public void onConnectionLost() {
		update = false;
	}
}
