package robot;

import com.flash3388.flashlib.hid.HidChannel;
import com.flash3388.flashlib.io.IoChannel;

public class RobotMap {

    private RobotMap() {}

    public static final IoChannel DRIVE_MOTOR_RIGHT = new IoChannel.Stub();
    public static final IoChannel DRIVE_MOTOR_LEFT = new IoChannel.Stub();

    public static final HidChannel HID_RIGHT = new HidChannel.Stub();
    public static final HidChannel HID_LEFT = new HidChannel.Stub();
}
