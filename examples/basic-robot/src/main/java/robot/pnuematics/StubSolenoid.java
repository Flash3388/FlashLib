package robot.pnuematics;

import com.flash3388.flashlib.io.IoChannel;
import com.flash3388.flashlib.io.devices.valve.Solenoid;
import com.flash3388.flashlib.time.Time;

public class StubSolenoid implements Solenoid {

    private final IoChannel mChannel;

    public StubSolenoid(IoChannel channel) {
        mChannel = channel;
    }

    @Override
    public void set(boolean open) {

    }

    @Override
    public boolean get() {
        return false;
    }

    @Override
    public void pulse(Time duration) {

    }
}
