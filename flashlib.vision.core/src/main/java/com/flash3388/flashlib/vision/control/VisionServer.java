package com.flash3388.flashlib.vision.control;

import com.castle.concurrent.service.Service;
import com.castle.exceptions.ServiceException;
import com.flash3388.flashlib.net.messaging.MessageType;
import com.flash3388.flashlib.net.messaging.Messenger;
import com.flash3388.flashlib.net.messaging.NewMessageEvent;
import com.flash3388.flashlib.vision.analysis.Analysis;
import com.flash3388.flashlib.vision.control.message.NewAnalysisMessage;
import com.flash3388.flashlib.vision.control.message.OptionChangeMessage;
import com.flash3388.flashlib.vision.control.message.RunStatusMessage;
import com.flash3388.flashlib.vision.control.message.StartMessage;
import com.flash3388.flashlib.vision.control.message.StopMessage;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public class VisionServer implements AutoCloseable {

    private final Messenger mMessenger;
    private final VisionRunner mRunner;
    private final VisionOptions mVisionOptions;

    public VisionServer(Messenger messenger,
                        Function<Consumer<Analysis>, AutoCloseable> runnerFactory,
                        KnownVisionOptionTypes optionTypes) {
        mMessenger = messenger;
        mRunner = new VisionRunner(runnerFactory, this::newAnalysis);
        mVisionOptions = new VisionOptions();

        mMessenger.addListener(new MessageListener(messenger, mRunner, optionTypes, mVisionOptions));
    }

    public void start() {
        try {
            mRunner.start();
        } catch (ServiceException e) {
            throw new RuntimeException(e);
        }
    }

    public void stop() {
        mRunner.stop();
    }

    public boolean isRunning() {
        return mRunner.isRunning();
    }

    public <T> void setOption(VisionOption<T> option, T value) {
        mVisionOptions.put(option, value);
        mMessenger.send(new OptionChangeMessage(
                option.name(),
                value
        ));
    }

    public <T> Optional<T> getOption(VisionOption<T> option) {
        return mVisionOptions.get(option);
    }

    public <T> T getOptionOrDefault(VisionOption<T> option, T defaultValue) {
        return mVisionOptions.getOrDefault(option, defaultValue);
    }

    @Override
    public void close() throws Exception {
        mRunner.close();
    }

    private void newAnalysis(Analysis analysis) {
        mMessenger.send(new NewAnalysisMessage(analysis));
    }

    private static class MessageListener implements com.flash3388.flashlib.net.messaging.MessageListener {

        private final Messenger mMessenger;
        private final Service mService;
        private final KnownVisionOptionTypes mOptionTypes;
        private final VisionOptions mVisionOptions;

        private MessageListener(Messenger messenger,
                                Service service,
                                KnownVisionOptionTypes optionTypes,
                                VisionOptions visionOptions) {
            mMessenger = messenger;
            mService = service;
            mOptionTypes = optionTypes;
            mVisionOptions = visionOptions;
        }

        @Override
        public void onNewMessage(NewMessageEvent event) {
            MessageType type = event.getMetadata().getType();
            if (StartMessage.TYPE.equals(type)) {
                if (!mService.isRunning()) {
                    try {
                        mService.start();
                    } catch (ServiceException e) {
                    }

                    mMessenger.send(new RunStatusMessage(true));
                }
            } else if (StopMessage.TYPE.equals(type)) {
                if (mService.isRunning()) {
                    mService.stop();
                    mMessenger.send(new RunStatusMessage(false));
                }
            } else if (OptionChangeMessage.TYPE.equals(type)) {
                OptionChangeMessage message = (OptionChangeMessage) event.getMessage();
                //noinspection rawtypes
                VisionOption option = mOptionTypes.get(message.getName());
                //noinspection unchecked
                mVisionOptions.put(option, message.getValue());
            }
        }
    }
}
