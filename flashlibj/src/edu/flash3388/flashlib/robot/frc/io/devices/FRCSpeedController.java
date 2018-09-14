package edu.flash3388.flashlib.robot.frc.io.devices;

import edu.flash3388.flashlib.robot.io.devices.actuators.FlashSpeedController;

public class FRCSpeedController implements FlashSpeedController {

    private final edu.wpi.first.wpilibj.SpeedController mSpeedController;

    public FRCSpeedController(edu.wpi.first.wpilibj.SpeedController speedController) {
        mSpeedController = speedController;
    }

    @Override
    public void set(double speed) {
        mSpeedController.set(speed);
    }

    @Override
    public void stop() {
        mSpeedController.stopMotor();
    }

    @Override
    public double get() {
        return mSpeedController.get();
    }

    @Override
    public boolean isInverted() {
        return mSpeedController.getInverted();
    }

    @Override
    public void setInverted(boolean inverted) {
        mSpeedController.setInverted(true);
    }
}
