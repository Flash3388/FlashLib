package edu.flash3388.flashlib.communications.sendable.manager;

import edu.flash3388.flashlib.communications.sendable.SendableData;

public class SendableMatcher {

    public boolean doMatch(SendableData local, SendableData remote) {
        return local.getId() == remote.getId();
    }
}
