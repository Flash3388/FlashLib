package edu.flash3388.flashlib.robot.devices;

public class AnalogAccelerometer{

	private AnalogInput input;
	private double zeroGvoltage;
	private double voltsPerG;
	
	public AnalogAccelerometer(AnalogInput input, double zeroGVoltage, double voltsPerG) {
		this.input = input;
		this.zeroGvoltage = zeroGVoltage;
		this.voltsPerG = voltsPerG;
	}
	
	public void setSensitivity(double voltsPerG){
		this.voltsPerG = voltsPerG;
	}
	public double getSensitivity(){
		return voltsPerG;
	}
	
	public void setZeroVoltage(double volts){
		this.zeroGvoltage = volts;
	}
	public double getZeroVoltage(){
		return zeroGvoltage;
	}
	
	public double getAcceleration(){
		return (input.getVoltage() - zeroGvoltage) / voltsPerG;
	}
}
