package robot;

import com.flash3388.flashlib.hid.HidChannel;
import com.flash3388.flashlib.io.IoChannel;

public class RobotMap {

    private RobotMap() {}

    public static final IoChannel SHOOTER_SOLENOID1 = new IoChannel.Stub();
    public static final IoChannel SHOOTER_SOLENOID2 = new IoChannel.Stub();
    public static final IoChannel SHOOTER_MOTOR = new IoChannel.Stub();

    public static final HidChannel STICK = new HidChannel.Stub();
}
