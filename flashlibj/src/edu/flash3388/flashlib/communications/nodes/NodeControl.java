package edu.flash3388.flashlib.communications.nodes;

import edu.flash3388.flashlib.communications.message.Message;

import java.util.concurrent.atomic.AtomicReference;

public class NodeControl {

    private final NodeData mNodeData;
    private final CommunicationNode mCommunicationNode;
    private final AtomicReference<NodeSession> mNodeSessionReference;

    public NodeControl(NodeData nodeData, CommunicationNode communicationNode) {
        mNodeData = nodeData;
        mCommunicationNode = communicationNode;
        mNodeSessionReference = new AtomicReference<>(null);
    }

    public NodeData getNodeData() {
        return mNodeData;
    }

    public CommunicationNode getCommunicationNode() {
        return mCommunicationNode;
    }

    public boolean openNodeSession(NodeData remoteNodeData, NodeMessenger nodeMessenger) {
        synchronized (mNodeSessionReference) {
            if (hasOpenNodeSession()) {
                return false;
            }

            NodeSession nodeSession = mCommunicationNode.onPair(nodeMessenger);
            mNodeSessionReference.set(nodeSession);
            return true;
        }
    }

    public boolean hasOpenNodeSession() {
        return mNodeSessionReference.get() != null;
    }

    public void onMessageReceived(Message message) {
        NodeSession nodeSession = mNodeSessionReference.get();
        if (nodeSession != null) {
            synchronized (mNodeSessionReference) {
                nodeSession.onMessageReceived(message);
            }
        }
    }

    public void closeNodeSession() {
        NodeSession nodeSession = mNodeSessionReference.get();
        if (nodeSession != null) {
            synchronized (mNodeSessionReference) {
                nodeSession.close();
            }
        }
    }
}
