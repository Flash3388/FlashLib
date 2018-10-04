package edu.flash3388.flashlib.communications.nodes;

import java.io.Serializable;

public class NodeData implements Serializable {

    private final int mId;
    private final int mType;

    public NodeData(int id, int type) {
        mId = id;
        mType = type;
    }

    public int getId() {
        return mId;
    }

    public int getType() {
        return mType;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof NodeData) {
            return ((NodeData)obj).mId == mId;
        }

        return false;
    }

    @Override
    public int hashCode() {
        return mId;
    }
}
