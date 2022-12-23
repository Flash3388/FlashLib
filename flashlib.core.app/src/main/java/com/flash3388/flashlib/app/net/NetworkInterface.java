package com.flash3388.flashlib.app.net;

import com.flash3388.flashlib.net.hfcs.HfcsRegistry;
import com.flash3388.flashlib.net.obsr.ObjectStorage;

/**
 * A packaged module allowing access to network functionalities to be used by the application.
 * Depending on the implementation and the configured {@link NetworkingMode}, different protocols
 * for communication may be available. Check {@link #getMode()} before accessing different protocol interfaces
 * to ensure the protocol is indeed supported and initialized.
 *
 * @since FlashLib 3.2.0
 */
public interface NetworkInterface {

    /**
     * Gets the mode configured for the networking module.
     * This mode is configured at startup and is not modifiable.
     *
     * @return {@link NetworkingMode}
     */
    NetworkingMode getMode();

    ObjectStorage getObjectStorage();

    HfcsRegistry getHfcsRegistry();
}
