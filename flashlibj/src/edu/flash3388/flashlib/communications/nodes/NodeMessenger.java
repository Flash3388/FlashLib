package edu.flash3388.flashlib.communications.nodes;

import edu.flash3388.flashlib.communications.message.Message;
import edu.flash3388.flashlib.communications.message.Messenger;
import edu.flash3388.flashlib.communications.message.WriteException;
import edu.flash3388.flashlib.communications.nodes.messages.NodeToNodeMessage;

public class NodeMessenger {

    private final Messenger mMessenger;
    private final NodeData mFrom;
    private final NodeData mTo;

    public NodeMessenger(Messenger messenger, NodeData from, NodeData to) {
        mMessenger = messenger;
        mFrom = from;
        mTo = to;
    }

    public void sendMessage(Message message) throws WriteException {
        mMessenger.writeMessage(new NodeToNodeMessage(mFrom, mTo, message));
    }
}
