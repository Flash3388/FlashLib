package robot;

import com.flash3388.flashlib.hid.HidChannel;
import com.flash3388.flashlib.io.IoChannel;

public class RobotMap {

    private RobotMap() {}

    public static final IoChannel DRIVE_MOTOR_FRONT = new IoChannel.Stub();
    public static final IoChannel DRIVE_MOTOR_RIGHT = new IoChannel.Stub();
    public static final IoChannel DRIVE_MOTOR_BACK = new IoChannel.Stub();
    public static final IoChannel DRIVE_MOTOR_LEFT = new IoChannel.Stub();
    public static final IoChannel SHOOTER_MOTOR = new IoChannel.Stub();
    public static final IoChannel SHOOTER_MOTOR2 = new IoChannel.Stub();
    public static final IoChannel SHOOTER_SOLENOID = new IoChannel.Stub();

    public static final HidChannel XBOX = new HidChannel.Stub();
}
