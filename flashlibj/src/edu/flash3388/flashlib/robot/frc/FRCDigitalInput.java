package edu.flash3388.flashlib.robot.frc;

import edu.flash3388.flashlib.robot.devices.DigitalInput;

public class FRCDigitalInput implements DigitalInput{

	private edu.wpi.first.wpilibj.DigitalInput port;
	
	public FRCDigitalInput(edu.wpi.first.wpilibj.DigitalInput port) {
		this.port = port;
	}
	public FRCDigitalInput(int port) {
		this(new edu.wpi.first.wpilibj.DigitalInput(port));
	}
	
	@Override
	public void free() {
		if(port != null)
			port.free();
		port = null;
	}

	@Override
	public boolean get() {
		return port.get();
	}
}
