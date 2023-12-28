package robot.actions;

import com.flash3388.flashlib.robot.systems.actions.Rotate;
import com.flash3388.flashlib.scheduling.ActionControl;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.time.Time;
import robot.subsystems.FeederSystem;

import java.util.function.DoubleSupplier;

public class Feed extends Rotate {

    private final FeederSystem.Interface mFeeder;
    private final Clock mClock;
    private final Time mDelayUntilStop;

    private Time mStopTime;

    public Feed(FeederSystem.Interface feeder, DoubleSupplier speedSupplier, Clock clock, Time delayUntilStop) {
        super(feeder, speedSupplier);
        mFeeder = feeder;
        mClock = clock;
        mDelayUntilStop = delayUntilStop;
    }

    @Override
    public void initialize(ActionControl control) {
        super.initialize(control);

        mStopTime = Time.INVALID;
    }

    @Override
    public void execute(ActionControl control) {
        super.execute(control);

        Time now = mClock.currentTime();
        if (!mFeeder.hasBalls() && !mStopTime.isValid()) {
            // NO BALLS! let's give the system some time to shoot the last ball
            mStopTime = now.add(mDelayUntilStop);
        }

        if (now.after(mStopTime)) {
            control.finish();
        }
    }
}
