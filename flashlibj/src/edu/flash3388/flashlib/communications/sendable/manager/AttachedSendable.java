package edu.flash3388.flashlib.communications.sendable.manager;

import edu.flash3388.flashlib.communications.sendable.Sendable;
import edu.flash3388.flashlib.communications.sendable.SendableData;

class AttachedSendable {

    private SendableData mSendableData;
    private Sendable mSendable;
    private PairDiscoveryOption mDiscoveryOption;

    AttachedSendable(SendableData sendableData, Sendable sendable, PairDiscoveryOption discoveryOption) {
        mSendableData = sendableData;
        mSendable = sendable;
        mDiscoveryOption = discoveryOption;
    }

    public SendableData getSendableData() {
        return mSendableData;
    }

    public Sendable getSendable() {
        return mSendable;
    }

    public PairDiscoveryOption getDiscoveryOption() {
        return mDiscoveryOption;
    }
}
