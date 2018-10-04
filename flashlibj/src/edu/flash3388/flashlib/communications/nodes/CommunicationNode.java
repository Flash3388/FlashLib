package edu.flash3388.flashlib.communications.nodes;

public interface CommunicationNode {

    NodeSession onPair(NodeMessenger nodeMessenger);
    void onUnpair();
}
