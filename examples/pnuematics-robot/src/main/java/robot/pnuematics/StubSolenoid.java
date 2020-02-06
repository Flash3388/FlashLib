package robot.pnuematics;

import com.flash3388.flashlib.robot.io.IoChannel;
import com.flash3388.flashlib.robot.io.devices.pneumatics.Solenoid;
import com.flash3388.flashlib.time.Time;

public class StubSolenoid implements Solenoid {

    private final IoChannel mChannel;

    public StubSolenoid(IoChannel channel) {
        mChannel = channel;
    }

    @Override
    public void set(boolean on) {

    }

    @Override
    public boolean get() {
        return false;
    }

    @Override
    public void pulse(Time duration) {

    }
}
