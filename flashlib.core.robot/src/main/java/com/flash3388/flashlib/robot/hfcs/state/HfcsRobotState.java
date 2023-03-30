package com.flash3388.flashlib.robot.hfcs.state;

import com.beans.Property;
import com.beans.properties.atomic.AtomicProperty;
import com.flash3388.flashlib.net.hfcs.HfcsRegistry;
import com.flash3388.flashlib.robot.RobotControl;
import com.flash3388.flashlib.robot.modes.RobotMode;
import com.flash3388.flashlib.time.Time;
import com.flash3388.flashlib.util.unique.InstanceId;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class HfcsRobotState {

    private HfcsRobotState() {}

    public static void registerReceiver(HfcsRegistry registry, BiConsumer<InstanceId, RobotStateData> consumer) {
        registry.registerIncoming(new StateDataInType())
                .addListener((event)-> {
                    consumer.accept(event.getSender(), event.getData());
                });
    }

    public static Supplier<RobotStateData> registerReceiver(HfcsRegistry registry) {
        RobotStateData initialData = new RobotStateData(RobotMode.DISABLED, Time.milliseconds(0));
        Property<RobotStateData> stateDataProperty = new AtomicProperty<>(initialData);
        registry.registerIncoming(new StateDataInType())
                .addListener((event)-> {
                    stateDataProperty.set(event.getData());
                });

        return stateDataProperty;
    }

    public static void registerProvider(RobotControl control, Time period) {
        HfcsRegistry registry = control.getNetworkInterface().getHfcsRegistry();

        registry.registerOutgoing(new StateDataType(), period, new StateDataSupplier(control));
    }
}
