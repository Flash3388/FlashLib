package edu.flash3388.flashlib.communication.runner;

import edu.flash3388.flashlib.io.serialization.JavaObjectSerializer;
import edu.flash3388.flashlib.io.serialization.Serializer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class CommunicationRunnerFactory {

    private CommunicationRunnerFactory() {}

    public static CommunicationRunner create(Logger logger) {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        Serializer serializer = new JavaObjectSerializer();

        return new CommunicationRunner(executorService, serializer, logger);
    }
}
