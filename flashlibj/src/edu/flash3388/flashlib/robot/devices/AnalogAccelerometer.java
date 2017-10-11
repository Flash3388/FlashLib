package edu.flash3388.flashlib.robot.devices;

import edu.flash3388.flashlib.robot.PIDSource;
import edu.flash3388.flashlib.util.beans.DoubleSource;

public class AnalogAccelerometer implements DoubleSource, PIDSource, IOPort{

	private AnalogInput input;
	private double zeroGvoltage;
	private double voltsPerG;
	
	public AnalogAccelerometer(int port) {
		this(port, 2.5, 1.0);
	}
	public AnalogAccelerometer(int port, double zeroGVoltage, double voltsPerG) {
		this.input = IOFactory.createAnalogInputPort(port);
		this.zeroGvoltage = zeroGVoltage;
		this.voltsPerG = voltsPerG;
	}
	public AnalogAccelerometer(AnalogInput input) {
		this(input, 2.5, 1.0);
	}
	public AnalogAccelerometer(AnalogInput input, double zeroGVoltage, double voltsPerG) {
		this.input = input;
		this.zeroGvoltage = zeroGVoltage;
		this.voltsPerG = voltsPerG;
	}
	
	@Override
	public void free() {
		if(input != null)
			input.free();
		input = null;
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

	@Override
	public double pidGet() {
		return getAcceleration();
	}
	@Override
	public double get() {
		return getAcceleration();
	}
}
