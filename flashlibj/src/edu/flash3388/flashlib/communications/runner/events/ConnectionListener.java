package edu.flash3388.flashlib.communications.runner.events;

import edu.flash3388.flashlib.event.Listener;

public interface ConnectionListener extends Listener {

    void onConnection(ConnectionEvent e);
    void onDisconnection(DisconnectionEvent e);
}
