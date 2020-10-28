package robot;

import com.flash3388.flashlib.hid.HidChannel;
import com.flash3388.flashlib.io.IoChannel;

public class RobotMap {

    private RobotMap() {}

    public static final IoChannel DRIVE_MOTOR_RIGHT_FRONT = new IoChannel.Stub();
    public static final IoChannel DRIVE_MOTOR_RIGHT_BACK = new IoChannel.Stub();
    public static final IoChannel DRIVE_MOTOR_LEFT_FRONT = new IoChannel.Stub();
    public static final IoChannel DRIVE_MOTOR_LEFT_BACK = new IoChannel.Stub();

    public static final HidChannel XBOX = new HidChannel.Stub();
}
