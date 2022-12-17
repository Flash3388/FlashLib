package com.flash3388.flashlib.net.old.hfc.impl;

import com.flash3388.flashlib.net.old.hfc.io.PacketInfoImpl;
import com.flash3388.flashlib.net.old.hfc.io.HfcChannel;
import com.notifier.EventController;
import org.slf4j.Logger;

import java.io.DataInput;

public class UpdatesTask implements Runnable {

    private final HfcChannel mChannel;
    private final Logger mLogger;
    private final UpdatesHandler mUpdatesHandler;

    public UpdatesTask(HfcChannel channel, EventController eventController, Logger logger) {
        mChannel = channel;
        mLogger = logger;
        mUpdatesHandler = new UpdatesHandler(eventController);
    }

    @Override
    public void run() {
        while (true) {
            try {
                mLogger.debug("UpdatesTask handling data");
                mChannel.handleUpdates(mUpdatesHandler);
            } catch (InterruptedException e) {
                break;
            } catch (Throwable t) {
                mLogger.error("Error in UpdatesTask", t);
            }
        }
    }

    private static class UpdatesHandler implements HfcChannel.UpdateHandler {

        private final EventController mEventController;

        private UpdatesHandler(EventController eventController) {
            mEventController = eventController;
        }

        @Override
        public void onPacketReceived(PacketInfoImpl packetInfo, DataInput contentInfo, boolean isFirstPacketFromSender) {
            /*
            mEventController.fire(
                    new NewPacketEvent(packet),
                    NewPacketEvent.class,
                    DataListener.class,
                    DataListener::onNewPacketReceived
            );
             */
        }
    }
}
