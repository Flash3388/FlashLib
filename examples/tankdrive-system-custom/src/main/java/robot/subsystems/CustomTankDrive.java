package robot.subsystems;

import com.flash3388.flashlib.io.devices.SpeedController;
import com.flash3388.flashlib.robot.systems.drive.TankDrive;
import com.flash3388.flashlib.robot.systems.drive.TankDriveSpeed;
import com.flash3388.flashlib.robot.systems.drive.algorithms.DriveAlgorithms;
import com.flash3388.flashlib.scheduling.Subsystem;

public class CustomTankDrive extends Subsystem implements TankDrive {
    // This system, is a simple recreation of the TankDriveSystem.
    // To implement a system, we first extend Subsystem.
    //
    // Note the use of implements TankDrive. This interface defines what a tank drive system should
    // do (what methods it should have). Implementing it, allows tells us which methods we should provide in
    // the system. But that's not all, because we use it, we will be able to use this system with pre-made
    // actions from flashlib. See the robot class to see how.

    private final SpeedController mRight;
    private final SpeedController mLeft;
    private final DriveAlgorithms mDriveAlgorithms;

    // In the constructor, we receive the devices and parameters we need to control the system.
    // Here we need the speed controllers.
    //
    // Note that SpeedController can be either one or multiple speed controllers, this depends on what the creators
    // of the instance provide us. For us it doesn't really matter, since we move all controllers in the
    // same side, together.
    //
    // DriveAlgorithms is a flashlib utility which provides some motion algorithm calculations.
    // We will need it here later.
    public CustomTankDrive(SpeedController right, SpeedController left) {
        mRight = right;
        mLeft = left;

        mDriveAlgorithms = new DriveAlgorithms();
    }

    // tankDrive is one of the methods defined in TankDrive. It should move the robot using two values:
    // one for the right side, another for the left side.
    // Implementing it is easy, we simply pass the values to the right and left speed controllers.
    @Override
    public void tankDrive(double right, double left) {
        mRight.set(right);
        mLeft.set(left);
    }

    // arcadeDrive is another method from TankDrive. It should move the robot using two values:
    // one defines forward-backward motion, the other defines rotation motion.
    //
    // Actually implementing the calculation of values to move the robot from that isn't that easy,
    // so we use DriveAlgorithms to do that. It will return us TankDriveSpeed, which we can
    // pass to an overload of tankDrive present in the TankDrive interface.
    @Override
    public void arcadeDrive(double moveValue, double rotateValue) {
        TankDriveSpeed tankDriveSpeed = mDriveAlgorithms.arcadeDrive(moveValue, rotateValue);
        tankDrive(tankDriveSpeed);
    }

    // stop is another method from TankDrive. It should stop all motion in the system.
    // It's pretty easy to accomplish, we simply call the stop methods of the speed controllers.
    @Override
    public void stop() {
        mRight.stop();
        mLeft.stop();
    }
}
