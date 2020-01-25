package robot;

import com.flash3388.flashlib.robot.io.IoChannel;

public class RobotMap {

    private RobotMap() {}

    public static final IoChannel DRIVE_MOTOR_FRONT = new IoChannel.Stub();
    public static final IoChannel DRIVE_MOTOR_RIGHT = new IoChannel.Stub();
    public static final IoChannel DRIVE_MOTOR_BACK = new IoChannel.Stub();
    public static final IoChannel DRIVE_MOTOR_LEFT = new IoChannel.Stub();
    public static final IoChannel SHOOTER_MOTOR = new IoChannel.Stub();
    public static final IoChannel TURRET_MOTOR = new IoChannel.Stub();
    public static final IoChannel TURRET_GYRO = new IoChannel.Stub();
}
