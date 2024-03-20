package com.flash3388.flashlib.net.channels.messsaging;

import com.flash3388.flashlib.net.channels.IncomingData;
import com.flash3388.flashlib.net.channels.NetAddress;
import com.flash3388.flashlib.net.channels.NetChannel;
import com.flash3388.flashlib.net.channels.nio.ChannelListener;
import com.flash3388.flashlib.net.messaging.ChannelId;
import com.flash3388.flashlib.net.messaging.Message;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.time.Time;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.util.Optional;

public class BaseChannelListener implements ChannelListener {

    private final ChannelController mController;
    private final Clock mClock;
    private final Logger mLogger;

    private final MessageReadingContext mReadingContext;
    private final MessageWritingContext mWritingContext;

    public BaseChannelListener(ChannelController controller,
                               KnownMessageTypes messageTypes,
                               ChannelId ourId,
                               Clock clock,
                               Logger logger,
                               boolean isChannelStreaming) {
        mController = controller;
        mClock = clock;
        mLogger = logger;

        mReadingContext = new MessageReadingContext(messageTypes, logger);
        mWritingContext = new MessageWritingContext(ourId, logger, isChannelStreaming);
    }

    public void reset() {
        mReadingContext.clear();
        mWritingContext.clear();
    }

    @Override
    public void onAcceptable(SelectionKey key) {

    }

    @Override
    public void onConnectable(SelectionKey key) {
        mController.onChannelConnectable();
    }

    @Override
    public void onReadable(SelectionKey key) {
        NetChannel channel = mController.getChannel();
        if (channel == null) {
            return;
        }

        try {
            IncomingData data = mReadingContext.readFromChannel(channel);
            if (data.getBytesReceived() >= 1) {
                mLogger.debug("New data from remote: {}, size={}",
                        data.getSender(),
                        data.getBytesReceived());
            }

            // send should always be the same for this kind of channel
            parseMessages(data.getSender());
        } catch (IOException e) {
            mLogger.error("Error while reading and processing new data for key={}", key, e);
            mController.resetChannel();
        }
    }

    @Override
    public void onWritable(SelectionKey key) {
        NetChannel channel = mController.getChannel();
        if (channel == null) {
            return;
        }

        try {
            // if we have an unfinished message, try finishing it now.
            if (!mWritingContext.writeToChannel(channel)) {
                // didn't finish writing the last message, wait for next write update.
                mLogger.trace("writing to channel did not finish, will try again on next update");
                return;
            }
        } catch (IOException e) {
            mLogger.error("Error while writing data for key={}", key, e);
            mController.resetChannel();
            return;
        }

        do {
            SendRequest request = mController.getNextSendRequest();
            if (request == null) {
                break;
            }

            try {
                if (request.header != null) {
                    mWritingContext.update(request.header, request.message);
                } else {
                    Time now = mClock.currentTime();
                    mWritingContext.update(now, request.message, request.isOnlyForServer);
                }
            } catch (IOException e) {
                mLogger.error("Error serializing message data for key={}", key, e);
                mController.onMessageSendingFailed(request.message, e);
                continue;
            }

            try {
                if (!mWritingContext.writeToChannel(channel)) {
                    // didn't finish writing the last message, wait for next write update.
                    mLogger.trace("writing to channel did not finish, will try again on next update");
                    break;
                }
            } catch (IOException e) {
                mLogger.error("Error while writing data for key={}", key, e);
                mController.resetChannel();
                break;
            }
        } while (true);
    }

    @Override
    public void onRequestedUpdate(SelectionKey key, Object param) {
        mController.onChannelCustomUpdate(param);
    }

    private void parseMessages(NetAddress sender) throws IOException {
        boolean hasMoreToParse;
        do {
            Optional<MessageReadingContext.ParseResult> resultOptional = mReadingContext.parse();
            if (resultOptional.isPresent()) {
                MessageReadingContext.ParseResult parseResult = resultOptional.get();

                MessageHeader header = parseResult.getHeader();
                Message message = parseResult.getMessage();

                mController.onNewMessage(sender, header, message);

                hasMoreToParse = true;
            } else {
                hasMoreToParse = false;
            }
        } while (hasMoreToParse);
    }
}
