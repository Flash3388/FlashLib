package com.flash3388.flashlib.robot.net;

/**
 * A packaged module allowing access to networking functionalities to be used by the robot.
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
}
