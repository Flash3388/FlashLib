package edu.flash3388.flashlib.robot.devices;

import edu.flash3388.flashlib.util.FlashUtil;

public class AnalogGyro implements Gyro{
	
	private static final double DEFAULT_SENSITIVITY = 0.007;
	private static final double CALIBRATION_TIME = 0.5;
	
	private GyroDataType pidType;
	private AnalogInput inputPort;
	private AnalogAccumulator accumulator;
	
	private double offset;
	private double sensitivity;
	private int center;
	
	public AnalogGyro(int port){
		this.inputPort = IOFactory.createAnalogInputPort(port);
		this.accumulator = inputPort.getAccumulator();
		if(accumulator == null)
			throw new IllegalStateException("Failed to retreive accumulator for port, cannot use analog gyro");
		
		calibrate();
	}
	public AnalogGyro(int port, int center, double offset){
		this(port, DEFAULT_SENSITIVITY, center, offset);
	}
	public AnalogGyro(int port, double sensetivity, int center, double offset) {
		this.inputPort = IOFactory.createAnalogInputPort(port);
		this.sensitivity = sensetivity;
		this.center = center;
		this.offset = offset;
		
		this.accumulator = inputPort.getAccumulator();
		if(accumulator == null)
			throw new IllegalStateException("Failed to retreive accumulator for port, cannot use analog gyro");
	}
	
	public AnalogGyro(AnalogInput port){
		this.inputPort = port;
		this.accumulator = port.getAccumulator();
		if(accumulator == null)
			throw new IllegalStateException("Failed to retreive accumulator for port, cannot use analog gyro");
		
		calibrate();
	}
	public AnalogGyro(AnalogInput port, int center, double offset){
		this(port, DEFAULT_SENSITIVITY, center, offset);
	}
	public AnalogGyro(AnalogInput port, double sensetivity, int center, double offset) {
		this.inputPort = port;
		this.sensitivity = sensetivity;
		this.center = center;
		this.offset = offset;
		
		this.accumulator = port.getAccumulator();
		if(accumulator == null)
			throw new IllegalStateException("Failed to retreive accumulator for port, cannot use analog gyro");
	}
	
	public void calibrate(){
		accumulator.reset();
		
		FlashUtil.delay(CALIBRATION_TIME);
		
		long value = accumulator.getValue();
		int count = accumulator.getCount();
		
		center = (int)((double)value / (double)count);
		offset = ((double)value / (double)count) - center;
		
		accumulator.setCenter(center);
		accumulator.reset();
	}
	
	public void setSensitivity(double sensitivity){
		this.sensitivity = sensitivity;
	}
	public double getSensitivity(){
		return sensitivity;
	}
	
	public void setOffset(double offset){
		this.offset = offset;
	}
	public double getOffset(){
		return offset;
	}
	
	public void setCenter(int center){
		this.center = center;
	}
	public void setCenterVoltage(double center){
		this.center = (int) ((center * inputPort.getMaxValue()) / inputPort.getMaxVoltage());
	}
	public int getCenter(){
		return center;
	}
	
	@Override
	public void free() {
		if(inputPort != null)
			inputPort.free();
		inputPort = null;
		accumulator = null;
	}
	@Override
	public void reset() {
		accumulator.reset();
	}

	@Override
	public double getAngle() {
		long value = accumulator.getValue() - (long)(accumulator.getCount() * offset);
		
		double scaledValue = ((value * inputPort.getMaxVoltage()) / inputPort.getMaxValue()) / 
				(inputPort.getSampleRate() * sensitivity);
		return scaledValue;
	}
	@Override
	public double getRate() {
		double value = ((inputPort.getValue() - (center + offset)) * inputPort.getMaxVoltage()) / 
				inputPort.getMaxVoltage();
		return (value / sensitivity);
	}

	@Override
	public GyroDataType getDataType() {
		return pidType;
	}
	@Override
	public void setDataType(GyroDataType type) {
		pidType = type;
	}
}
