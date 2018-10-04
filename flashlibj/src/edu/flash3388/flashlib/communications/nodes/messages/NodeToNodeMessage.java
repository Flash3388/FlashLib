package edu.flash3388.flashlib.communications.nodes.messages;

import edu.flash3388.flashlib.communications.message.Message;
import edu.flash3388.flashlib.communications.nodes.NodeData;

public class NodeToNodeMessage implements Message {

    private final NodeData mFrom;
    private final NodeData mTo;
    private final Message mMessage;

    public NodeToNodeMessage(NodeData from, NodeData to, Message message) {
        mFrom = from;
        mTo = to;
        mMessage = message;
    }

    @Override
    public int getHeader() {
        return 0;
    }
}
