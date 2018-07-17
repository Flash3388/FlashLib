package edu.flash3388.flashlib.communications.sendable.manager;

import edu.flash3388.flashlib.communications.sendable.SendableData;

import java.util.Map;
import java.util.function.Consumer;

class SendableDiscoveryRunner implements Runnable {

    private Map<Integer, AttachedSendable> mSendables;
    private Consumer<SendableData> mPairRequestSender;

    SendableDiscoveryRunner(Map<Integer, AttachedSendable> sendables, Consumer<SendableData> pairRequestSender) {
        mSendables = sendables;
        mPairRequestSender = pairRequestSender;
    }

    @Override
    public void run() {
        for (AttachedSendable attachedSendable : mSendables.values()) {
            if (attachedSendable.getDiscoveryOption() == PairDiscoveryOption.SEND_PAIR_REQUESTS) {
                mPairRequestSender.accept(attachedSendable.getSendableData());
            }
        }
    }
}
