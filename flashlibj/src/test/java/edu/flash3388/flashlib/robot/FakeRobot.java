package edu.flash3388.flashlib.robot;

import edu.flash3388.flashlib.robot.hid.HidInterface;
import edu.flash3388.flashlib.robot.modes.RobotModeSupplier;

import static org.mockito.Mockito.mock;

public class FakeRobot extends Robot {

    private final RobotModeSupplier mRobotModeSupplier;
    private final HidInterface mHidInterface;

    public FakeRobot() {
        mRobotModeSupplier = mock(RobotModeSupplier.class);
        mHidInterface = mock(HidInterface.class);
    }

    @Override
    protected void robotInit() throws RobotInitializationException {

    }

    @Override
    protected void robotMain() {

    }

    @Override
    protected void robotShutdown() {

    }

    @Override
    public RobotModeSupplier getModeSupplier() {
        return mRobotModeSupplier;
    }

    @Override
    public HidInterface getHidInterface() {
        return mHidInterface;
    }
}
