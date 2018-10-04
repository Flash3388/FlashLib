package edu.flash3388.flashlib.communications.nodes;

public class NoSuchNodeException extends NodeException {

    public NoSuchNodeException(NodeData nodeData) {
        super(nodeData, "no such node");
    }
}
