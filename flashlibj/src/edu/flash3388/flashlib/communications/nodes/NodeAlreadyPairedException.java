package edu.flash3388.flashlib.communications.nodes;

public class NodeAlreadyPairedException extends NodeException {

    public NodeAlreadyPairedException(NodeData nodeData) {
        super(nodeData, "node already paired");
    }
}
