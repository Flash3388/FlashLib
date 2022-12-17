package com.flash3388.flashlib.net.message;

import com.flash3388.flashlib.net.LocalNetInfo;
import com.flash3388.flashlib.net.data.InternalRemote;
import com.flash3388.flashlib.net.data.InternalRemoteStorage;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.time.Time;

import java.io.DataInput;
import java.io.IOException;

public class NewDataHandler {

    public static class Result {
        public final MessageHeader header;
        public final Message message;
        public final MessageInfo info;

        private Result(MessageHeader header, Message message, MessageInfo info) {
            this.header = header;
            this.message = message;
            this.info = info;
        }
    }

    private final InternalRemoteStorage mRemotesStorage;
    private final LocalNetInfo mOurInfo;
    private final MessageSerializer mSerializer;
    private final Clock mClock;

    public NewDataHandler(InternalRemoteStorage remotesStorage,
                          LocalNetInfo ourInfo,
                          MessageSerializer serializer,
                          Clock clock) {
        mRemotesStorage = remotesStorage;
        mOurInfo = ourInfo;
        mSerializer = serializer;
        mClock = clock;
    }

    public Result handle(DataInput input) throws IOException {
        MessageHeader header = mSerializer.readHeader(input);
        Message message = mSerializer.read(input, header);

        if (header.getSenderId().equals(mOurInfo.getId())) {
            throw new MessageSentByUsException();
        }

        Time now = mClock.currentTime();
        InternalRemote remote = mRemotesStorage.getOrCreateRemote(header.getSenderId());
        remote.updateLastSeen(now);

        MessageInfo info = new MessageInfoImpl(remote, now);

        return new Result(header, message, info);
    }
}
