package edu.flash3388.flashlib.robot.devices;

public class AnalogRangeFinder implements RangeFinder{
	
	private AnalogInput input;
	private double voltageToCM;
	
	public AnalogRangeFinder(int port, double vtocm) {
		this.input = IOProvider.createAnalogInput(port);
		this.voltageToCM = vtocm;
	}
	public AnalogRangeFinder(AnalogInput input, double vtocm) {
		this.input = input;
		this.voltageToCM = vtocm;
	}
	
	@Override
	public void free() {
		if(input != null)
			input.free();
		input = null;
	}
	
	public void setVoltToCMRatio(double vtocm){
		this.voltageToCM = vtocm;
	}
	public double getVoltToCMRatio(){
		return voltageToCM;
	}

	@Override
	public double getRangeCM() {
		return input.getVoltage() * voltageToCM;
	}
}
