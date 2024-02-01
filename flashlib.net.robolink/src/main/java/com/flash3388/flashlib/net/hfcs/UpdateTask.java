package com.flash3388.flashlib.net.hfcs;

import com.flash3388.flashlib.net.channels.messsaging.BaseMessagingChannel;
import com.flash3388.flashlib.net.channels.messsaging.MessagingChannel;
import com.flash3388.flashlib.time.Time;

public class UpdateTask implements Runnable {

    private static final Time MAX_SLEEP_TIME = Time.seconds(1);

    private final BaseMessagingChannel mChannel;
    private final HfcsContext mContext;

    public UpdateTask(MessagingChannel channel, HfcsContext context) {
        mChannel = channel;
        mContext = context;
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            try {
                if (!mContext.isAttached()) {
                    Thread.sleep(MAX_SLEEP_TIME.valueAsMillis());
                    continue;
                }

                mContext.updateIncoming();
                mContext.queueNewData(mChannel);

                Time wantedSleepTime = mContext.getWaitTimeToNextSend();
                long sleepTimeMs = wantedSleepTime.isValid() && wantedSleepTime.before(MAX_SLEEP_TIME) ?
                    wantedSleepTime.valueAsMillis() :
                    MAX_SLEEP_TIME.valueAsMillis();
                Thread.sleep(sleepTimeMs);
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}
