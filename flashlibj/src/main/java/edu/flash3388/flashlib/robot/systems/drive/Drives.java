package edu.flash3388.flashlib.robot.systems.drive;

import edu.flash3388.flashlib.robot.io.devices.actuators.SpeedController;

public class Drives {

    private Drives() {}

    public static TankDrive tankDriveSystem(SpeedController rightController, SpeedController leftController) {
        return new TankDriveSystem(rightController, leftController);
    }

    public static OmniDrive omniDriveSystem(SpeedController frontController, SpeedController rightController,
                                      SpeedController backController, SpeedController leftController) {
        return new OmniDriveSystem(frontController, rightController, backController, leftController);
    }

    public static MecanumDrive mecanumDriveSystem(SpeedController frontRightController, SpeedController backRightController,
                                                  SpeedController frontLeftController, SpeedController backLeftController) {
        return new MecanumDriveSystem(frontRightController, backRightController, frontLeftController, backLeftController);
    }
}
