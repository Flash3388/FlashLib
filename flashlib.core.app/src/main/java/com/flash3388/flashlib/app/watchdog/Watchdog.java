package com.flash3388.flashlib.app.watchdog;

import com.flash3388.flashlib.time.Time;

public interface Watchdog {

    /**
     * Gets the name of the watchdog.
     *
     * @return name, representative of the watchdog.
     */
    String getName();

    /**
     * Gets the feed timeout of the watchdog.
     *
     * @return time
     */
    Time getTimeout();

    /**
     * Gets whether the watchdog is currently enabled.
     *
     * @return <b>true</b> if enabled, <b>false</b> otherwise.
     */
    boolean isEnabled();

    /**
     * Gets whether the feeding timer has expired. Measured according to the last
     * call to {@link #feed()}.
     *
     * @return <b>true</b> if expired, <b>false</b> otherwise.
     */
    boolean isExpired();

    /**
     * Disables the feeding check of the watchdog.
     * Should be used when the target for the watchdog is not running or executing a
     * segment which should not be measured.
     */
    void disable();

    /**
     * Enables the feeding check of the watchdog. Effectively restarting
     * each functionality.
     */
    void enable();

    /**
     * Register a timestamp report. Use this to report on the progress of the target
     * for help debugging which parts of the target cause delays in execution.
     *
     * @param key storage key identifier, should be unique for this timestamp.
     */
    void reportTimestamp(String key);

    /**
     * Feeds the watchdog, updating it that the target is still functioning. Should
     * be called periodically at specific location to indicate that the target is still
     * functioning.
     */
    void feed();
}
