package com.flash3388.flashlib.robot.hfcs.control;

import com.beans.Property;
import com.beans.properties.atomic.AtomicProperty;
import com.flash3388.flashlib.net.hfcs.ConnectionEvent;
import com.flash3388.flashlib.net.hfcs.DataReceivedEvent;
import com.flash3388.flashlib.net.hfcs.HfcsInListener;
import com.flash3388.flashlib.net.hfcs.HfcsRegistry;
import com.flash3388.flashlib.net.hfcs.HfcsRegisteredIncoming;
import com.flash3388.flashlib.net.hfcs.TimeoutEvent;
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

        HfcsRegisteredIncoming<TargetedControlData> incoming = registry.registerIncoming(
                new ControlDataInType(), RECEIVE_TIMEOUT);
        incoming.addListener(new InListenerImpl(ourId, modeProperty, initialData));

        return modeProperty;
    }

    public static Property<RobotControlData> registerProvider(HfcsRegistry registry, Time period, InstanceId targetId) {
        RobotControlData initialData = new RobotControlData(RobotMode.DISABLED);
        Property<RobotControlData> controlDataProperty = new AtomicProperty<>(initialData);
        registry.registerOutgoing(new ControlDataType(), period,
                ()-> new TargetedControlData(targetId, controlDataProperty.get()));

        return controlDataProperty;
    }

    private static class InListenerImpl implements HfcsInListener<TargetedControlData> {

        private final InstanceId mOurId;
        private final Property<RobotControlData> mModeProperty;
        private final RobotControlData mInitialData;

        private InListenerImpl(InstanceId ourId, Property<RobotControlData> modeProperty, RobotControlData initialData) {
            mOurId = ourId;
            mModeProperty = modeProperty;
            mInitialData = initialData;
        }

        @Override
        public void onConnect(ConnectionEvent event) {

        }

        @Override
        public void onDisconnect(ConnectionEvent event) {

        }

        @Override
        public void onReceived(DataReceivedEvent<TargetedControlData> event) {
            InstanceId targetId = event.getData().getTargetId();
            if (!targetId.equals(mOurId)) {
                LOGGER.debug("Received control data for another instance id={}", targetId);
                return;
            }

            mModeProperty.set(event.getData().getData());
        }

        @Override
        public void onTimeout(TimeoutEvent<TargetedControlData> event) {
            LOGGER.warn("Control Data from HFCS not received in a while and packet has timed out. Resetting data");
            mModeProperty.set(mInitialData);
        }
    }
}
