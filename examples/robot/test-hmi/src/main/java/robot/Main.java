package robot;

import com.beans.observables.Observables;
import com.beans.observables.properties.ObservableBooleanProperty;
import com.beans.observables.properties.ObservableIntProperty;
import com.beans.observables.properties.ObservableProperty;
import com.castle.net.Connector;
import com.castle.net.StreamConnection;
import com.castle.net.tcp.TcpServerConnector;
import com.flash3388.flashlib.hmi.HmiContainer;
import com.flash3388.flashlib.hmi.HmiDescriptor;
import com.flash3388.flashlib.hmi.HmiObject;
import com.flash3388.flashlib.hmi.comm.impl.MessageChannelImpl;
import com.flash3388.flashlib.hmi.comm.impl.MessagingService;
import com.flash3388.flashlib.hmi.impl.HmiContainerImpl;
import com.flash3388.flashlib.util.logging.LogLevel;
import com.flash3388.flashlib.util.logging.LoggerBuilder;
import com.notifier.Controllers;
import com.notifier.EventController;
import org.slf4j.Logger;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        Logger logger = new LoggerBuilder("robot")
                .setLogLevel(LogLevel.DEBUG)
                .enableConsoleLogging(true)
                .build();

        EventController eventController = Controllers.newSingleThreadController();
        try (Connector<StreamConnection> connector = new TcpServerConnector(1000, 500);
             MessagingService service = new MessagingService(new MessageChannelImpl(connector), eventController)) {
            service.start();
            HmiContainer container = new HmiContainerImpl(service);

            BasicObj basicObj = new BasicObj();
            container.put("basicObj", basicObj);



        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    private static class BasicObj implements HmiObject {

        private final ObservableBooleanProperty mValue = Observables.factory().newBooleanProperty(false);

        @Override
        public void onHmiRegistration(HmiDescriptor descriptor) {
            descriptor.registerField("mValue", mValue);
        }
    }

    private static class SubObj implements HmiObject {

        private final ObservableIntProperty mValue = Observables.factory().newIntProperty(4);

        @Override
        public void onHmiRegistration(HmiDescriptor descriptor) {
            descriptor.registerReadOnlyField("mValue2", mValue);
        }
    }
}
