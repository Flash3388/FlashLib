package com.flash3388.flashlib.vision.control;

import com.beans.observables.RegisteredListener;
import com.castle.util.closeables.Closeables;
import com.flash3388.flashlib.net.messaging.MessageType;
import com.flash3388.flashlib.net.messaging.Messenger;
import com.flash3388.flashlib.net.messaging.NewMessageEvent;
import com.flash3388.flashlib.vision.control.message.NewAnalysisMessage;
import com.flash3388.flashlib.vision.control.message.OptionChangeMessage;
import com.flash3388.flashlib.vision.control.message.RunStatusMessage;
import com.flash3388.flashlib.vision.control.message.StartMessage;
import com.flash3388.flashlib.vision.control.message.StopMessage;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class RemoteBackend implements VisionBackend {

    private final Messenger mMessenger;

    private final RegisteredListener mMessengerListener;

    private final AtomicBoolean mIsRunning;
    private final AtomicReference<Listener> mListener;

    public RemoteBackend(Messenger messenger, KnownVisionOptionTypes optionTypes) {
        mMessenger = messenger;

        mIsRunning = new AtomicBoolean();
        mListener = new AtomicReference<>();
        mMessengerListener = mMessenger.addListener(
                new MessageListener(optionTypes, mIsRunning, mListener));
    }

    @Override
    public void setListener(Listener listener) {
        mListener.set(listener);
    }

    @Override
    public void start() {
        mMessenger.send(new StartMessage());
    }

    @Override
    public void stop() {
        mMessenger.send(new StopMessage());
    }

    @Override
    public <T> void setOption(VisionOption<T> option, T value) {
        mMessenger.send(new OptionChangeMessage(option.name(), value));
    }

    @Override
    public void close() throws Exception {
        mMessengerListener.remove();
    }

    private static class MessageListener implements com.flash3388.flashlib.net.messaging.MessageListener {

        private final KnownVisionOptionTypes mOptionTypes;
        private final AtomicBoolean mIsRunning;
        private final AtomicReference<Listener> mListener;

        private MessageListener(KnownVisionOptionTypes optionTypes,
                                AtomicBoolean isRunning,
                                AtomicReference<Listener> listener) {
            mOptionTypes = optionTypes;
            mIsRunning = isRunning;
            mListener = listener;
        }

        @Override
        public void onNewMessage(NewMessageEvent event) {
            MessageType type = event.getMetadata().getType();
            if (RunStatusMessage.TYPE.equals(type)) {
                RunStatusMessage message = (RunStatusMessage) event.getMessage();
                boolean wasRunning = mIsRunning.getAndSet(message.isRunning());

                Listener listener = mListener.get();
                if (listener != null) {
                    if (message.isRunning() !=  wasRunning) {
                        if (message.isRunning()) {
                            listener.onStarted();
                        } else {
                            listener.onStopped();
                        }
                    }
                }
            } else if (OptionChangeMessage.TYPE.equals(type)) {
                OptionChangeMessage message = (OptionChangeMessage) event.getMessage();
                //noinspection rawtypes
                VisionOption option = mOptionTypes.get(message.getName());

                Listener listener = mListener.get();
                if (listener != null) {
                    //noinspection unchecked
                    listener.onOptionChanged(option, message.getValue());
                }
            } else if (NewAnalysisMessage.TYPE.equals(type)) {
                NewAnalysisMessage message = (NewAnalysisMessage) event.getMessage();

                Listener listener = mListener.get();
                if (listener != null) {
                    listener.onNewAnalysis(message.getAnalysis());
                }
            }
        }
    }
}
