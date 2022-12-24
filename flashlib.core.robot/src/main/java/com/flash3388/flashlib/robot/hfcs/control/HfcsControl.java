package com.flash3388.flashlib.robot.hfcs.control;

import com.beans.Property;
import com.beans.properties.atomic.AtomicProperty;
import com.flash3388.flashlib.net.hfcs.HfcsRegistry;
import com.flash3388.flashlib.robot.modes.RobotMode;
import com.flash3388.flashlib.time.Time;

import java.util.function.Supplier;

public class HfcsControl {

    private HfcsControl() {}

    public static Supplier<? extends RobotMode> registerReceiver(HfcsRegistry registry) {
        Property<RobotMode> modeProperty = new AtomicProperty<>(RobotMode.DISABLED);

        registry.registerIncoming(new ControlDataInType())
                .addListener((event)-> {
                    modeProperty.set(event.getData().getMode());
                });

        return modeProperty;
    }

    public static Property<ControlData> registerProvider(HfcsRegistry registry, Time period) {
        ControlData initialData = new ControlData(RobotMode.DISABLED);
        Property<ControlData> controlDataProperty = new AtomicProperty<>(initialData);
        registry.registerOutgoing(new ControlDataType(), period, controlDataProperty);

        return controlDataProperty;
    }
}
