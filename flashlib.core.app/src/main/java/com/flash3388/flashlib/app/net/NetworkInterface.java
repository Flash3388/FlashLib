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

    /**
     * Gets the {@link ObjectStorage} associated with this network interface.
     * This networking protocol provides a distributed object storage for sharing
     * information between instances/processes.
     *
     * Make sure to check before end that this networking featuring is indeed enabled
     * by querying {@link #getMode()} and {@link NetworkingMode#isObjectStorageEnabled()}.
     *
     * @return object storage
     */
    ObjectStorage getObjectStorage();

    /**
     * Gets the {@link HfcsRegistry} associated with this networking interface.
     * This networking protocol provides a continuous transmission of control and state
     * data packets between instances.
     *
     * Make sure to check before end that this networking featuring is indeed enabled
     * by querying {@link #getMode()} and {@link NetworkingMode#isHfcsEnabled()}.
     *
     * @return HFCS registry.
     */
    HfcsRegistry getHfcsRegistry();
}