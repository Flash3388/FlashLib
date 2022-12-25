package com.flash3388.flashlib.robot.hfcs.control;

import com.beans.Property;
import com.beans.properties.atomic.AtomicProperty;
import com.flash3388.flashlib.net.hfcs.HfcsRegistry;
import com.flash3388.flashlib.robot.modes.RobotMode;
import com.flash3388.flashlib.time.Time;

import java.util.function.Supplier;

public class HfcsRobotControl {

    private HfcsRobotControl() {}

    public static Supplier<RobotControlData> registerReceiver(HfcsRegistry registry) {
        RobotControlData initialData = new RobotControlData(RobotMode.DISABLED);
        Property<RobotControlData> modeProperty = new AtomicProperty<>(initialData);

        registry.registerIncoming(new ControlDataInType())
                .addListener((event)-> {
                    modeProperty.set(event.getData());
                });

        return modeProperty;
    }

    public static Property<RobotControlData> registerProvider(HfcsRegistry registry, Time period) {
        RobotControlData initialData = new RobotControlData(RobotMode.DISABLED);
        Property<RobotControlData> controlDataProperty = new AtomicProperty<>(initialData);
        registry.registerOutgoing(new ControlDataType(), period, controlDataProperty);

        return controlDataProperty;
    }
}
