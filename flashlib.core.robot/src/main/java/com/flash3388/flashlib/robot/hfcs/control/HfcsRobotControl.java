package com.flash3388.flashlib.robot.hfcs.control;

import com.beans.Property;
import com.beans.properties.atomic.AtomicProperty;
import com.flash3388.flashlib.net.hfcs.HfcsRegistry;
import com.flash3388.flashlib.net.hfcs.RegisteredIncoming;
import com.flash3388.flashlib.robot.modes.RobotMode;
import com.flash3388.flashlib.time.Time;
import com.flash3388.flashlib.util.logging.Logging;
import com.flash3388.flashlib.util.unique.InstanceId;
import org.slf4j.Logger;

import java.util.function.Supplier;

public class HfcsRobotControl {

    private HfcsRobotControl() {}

    private static final Logger LOGGER = Logging.getMainLogger();
    private static final Time RECEIVE_TIMEOUT = Time.seconds(1);

    public static Supplier<RobotControlData> registerReceiver(HfcsRegistry registry, InstanceId ourId) {
        RobotControlData initialData = new RobotControlData(RobotMode.DISABLED);
        Property<RobotControlData> modeProperty = new AtomicProperty<>(initialData);

        RegisteredIncoming<TargetedControlData> incoming = registry.registerIncoming(
                new ControlDataInType(), RECEIVE_TIMEOUT);
        incoming.addListener((event)-> {
                    if (!event.getData().getTargetId().equals(ourId)) {
                        return;
                    }

                    modeProperty.set(event.getData().getData());
                });
        incoming.addTimeoutListener((event)-> {
            LOGGER.warn("Control Data from HFCS not received in a while and packet has timed out. Resetting data");
            modeProperty.set(initialData);
        });

        return modeProperty;
    }

    public static Property<RobotControlData> registerProvider(HfcsRegistry registry, Time period, InstanceId targetId) {
        RobotControlData initialData = new RobotControlData(RobotMode.DISABLED);
        Property<RobotControlData> controlDataProperty = new AtomicProperty<>(initialData);
        registry.registerOutgoing(new ControlDataType(), period,
                ()-> new TargetedControlData(targetId, controlDataProperty.get()));

        return controlDataProperty;
    }
}
