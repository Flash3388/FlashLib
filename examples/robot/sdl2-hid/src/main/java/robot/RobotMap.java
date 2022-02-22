package robot;

import com.flash3388.flashlib.hid.HidChannel;
import com.flash3388.flashlib.hid.generic.GenericHidChannel;

public class RobotMap {

    private RobotMap() {}

    public static final HidChannel XBOX = new GenericHidChannel(0);
}
