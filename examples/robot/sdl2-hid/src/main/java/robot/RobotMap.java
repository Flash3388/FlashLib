package robot;

import com.flash3388.flashlib.hid.HidChannel;
import com.flash3388.flashlib.hid.sdl2.Sdl2Hid;

public class RobotMap {

    private RobotMap() {}

    public static final HidChannel XBOX = Sdl2Hid.newChannel(0);
}
