package edu.flash3388.flashlib.robot.frc;

import edu.flash3388.flashlib.robot.frc.hid.FRCHIDInterface;
import edu.flash3388.flashlib.robot.frc.modes.FRCRobotModeSupplier;
import edu.flash3388.flashlib.robot.hid.HIDInterface;
import edu.flash3388.flashlib.robot.modes.RobotModeSupplier;
import edu.flash3388.flashlib.robot.RobotInterface;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.SampleRobot;

public abstract class FRCRobotBase extends RobotBase implements RobotInterface {
	
	private final RobotModeSupplier mRobotModeSupplier;
	private final HIDInterface mHidInterface;

	public FRCRobotBase() {
	    // m_ds -> from super -> protected final DriverStation m_ds
        mRobotModeSupplier = new FRCRobotModeSupplier(m_ds);
        mHidInterface = new FRCHIDInterface(m_ds);
    }

	@Override
	public RobotModeSupplier getModeSupplier() {
		return mRobotModeSupplier;
	}

	@Override
	public HIDInterface getHIDInterface() {
		return mHidInterface;
	}
}
