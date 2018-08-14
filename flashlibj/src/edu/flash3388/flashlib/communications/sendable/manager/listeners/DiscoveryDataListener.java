package edu.flash3388.flashlib.communications.sendable.manager.listeners;

import edu.flash3388.flashlib.communications.message.event.MessageEvent;
import edu.flash3388.flashlib.communications.message.event.MessageListener;
import edu.flash3388.flashlib.communications.sendable.SendableData;
import edu.flash3388.flashlib.communications.sendable.manager.RemoteSendablesStatus;
import edu.flash3388.flashlib.communications.sendable.manager.messages.DiscoveryDataMessage;
import edu.flash3388.flashlib.io.PrimitiveSerializer;

import java.util.Collection;

public class DiscoveryDataListener implements MessageListener {

    private RemoteSendablesStatus mRemoteSendablesStatus;
    private PrimitiveSerializer mSerializer;

    public DiscoveryDataListener(RemoteSendablesStatus remoteSendablesStatus, PrimitiveSerializer serializer) {
        mRemoteSendablesStatus = remoteSendablesStatus;
        mSerializer = serializer;
    }

    @Override
    public void onMessageReceived(MessageEvent e) {
        DiscoveryDataMessage discoveryDataMessage = DiscoveryDataMessage.fromMessage(e.getMessage(), mSerializer);
        Collection<SendableData> sendableDataCollection = discoveryDataMessage.getSendables();

        if (discoveryDataMessage.getState() == DiscoveryDataMessage.State.ATTACHED) {
            mRemoteSendablesStatus.updateAttached(sendableDataCollection);
        } else {
            mRemoteSendablesStatus.updateDetached(sendableDataCollection);
        }
    }
}
