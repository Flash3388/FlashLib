package edu.flash3388.flashlib.communications.nodes;

import edu.flash3388.flashlib.communications.message.Messenger;

import java.util.Map;

public class NodesPairingController {

    private final Map<NodeData, NodeControl> mNodesMap;
    private final Messenger mMessenger;

    public NodesPairingController(Map<NodeData, NodeControl> nodesMap, Messenger messenger) {
        mNodesMap = nodesMap;
        mMessenger = messenger;
    }

    public void pairNode(NodeData local, NodeData remote) throws NodeAlreadyPairedException, NoSuchNodeException {
        NodeControl nodeControl = getNodeStatusFromData(local);

        NodeMessenger nodeMessenger = new NodeMessenger(mMessenger, local, remote);
        if (!nodeControl.openNodeSession(remote, nodeMessenger)) {
            throw new NodeAlreadyPairedException(local);
        }
    }

    public void unpairNode(NodeData local, NodeData remote) throws NoSuchNodeException {
        NodeControl nodeControl = getNodeStatusFromData(local);
        nodeControl.closeNodeSession();
    }

    private NodeControl getNodeStatusFromData(NodeData nodeData) throws NoSuchNodeException {
        NodeControl nodeControl = mNodesMap.get(nodeData);
        if (nodeControl == null) {
            throw new NoSuchNodeException(nodeData);
        }

        return nodeControl;
    }
}
