package edu.flash3388.flashlib.robot;

import edu.flash3388.flashlib.robot.hid.HidInterface;
import edu.flash3388.flashlib.robot.modes.RobotModeSupplier;

public class FakeRobotBase extends RobotBase {
    
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
        return null;
    }

    @Override
    public HidInterface getHidInterface() {
        return null;
    }
}
