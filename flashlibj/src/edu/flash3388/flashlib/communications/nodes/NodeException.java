package edu.flash3388.flashlib.communications.nodes;

public class NodeException extends Exception {

    public NodeException(NodeData nodeData, String message) {
        super(String.format("%d: %s", nodeData.getId(), message));
    }
}
