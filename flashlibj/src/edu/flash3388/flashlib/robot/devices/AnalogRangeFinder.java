package edu.flash3388.flashlib.robot.devices;

public class AnalogRangeFinder implements RangeFinder{
	
	private AnalogInput input;
	private double voltageToCM;
	
	public AnalogRangeFinder(AnalogInput input, double vtocm) {
		this.input = input;
		this.voltageToCM = vtocm;
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
