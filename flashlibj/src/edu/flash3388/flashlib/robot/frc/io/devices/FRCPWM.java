package edu.flash3388.flashlib.robot.frc.io.devices;

import edu.flash3388.flashlib.robot.io.PWM;
import edu.wpi.first.wpilibj.SensorBase;
import edu.wpi.first.wpilibj.hal.DIOJNI;

public class FRCPWM implements PWM {

	private edu.wpi.first.wpilibj.PWM port;
	
	public FRCPWM(edu.wpi.first.wpilibj.PWM port) {
		this.port = port;
	}
	public FRCPWM(int port) {
		this(new edu.wpi.first.wpilibj.PWM(port));
	}
	
	@Override
	public void free() {
		if(port != null)
			port.free();
		port = null;
	}

	@Override
	public void setDuty(double duty) {
		setRaw((int) (duty * 255));
	}
	@Override
	public void setRaw(int raw) {
		port.setRaw(raw);
	}

	@Override
	public double getDuty() {
		return getRaw() / 255.0;
	}
	@Override
	public int getRaw() {
		return port.getRaw();
	}

	@Override
	public void setFrequency(double frequency) {
		
	}
	@Override
	public double getFrequency() {
		return (SensorBase.kSystemClockTicksPerMicrosecond * 1e3) / DIOJNI.getLoopTiming();
	}
}
