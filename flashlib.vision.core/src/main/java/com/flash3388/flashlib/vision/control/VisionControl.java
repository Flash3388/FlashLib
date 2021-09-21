package com.flash3388.flashlib.vision.control;

import com.castle.concurrent.service.Service;
import com.flash3388.flashlib.time.Time;
import com.flash3388.flashlib.vision.VisionResult;
import com.flash3388.flashlib.vision.control.event.VisionListener;

import java.util.Optional;

/**
 * A controller for vision processes.
 *
 * @since FlashLib 3.0.0
 */
public interface VisionControl extends Service {

    /**
     * Gets whether the vision process is running or not.
     *
     * @return <b>true</b> if running, <b>false</b> otherwise.
     * @see #start()
     * @see #stop()
     */
    @Override
    boolean isRunning();

    /**
     * Starts the vision process. If the process is already running,
     * nothing happens.
     *
     * @see #isRunning()
     */
    @Override
    void start();

    /**
     * Stops the vision process. If the process is not running,
     * nothing happens.
     *
     * @see #isRunning()
     */
    @Override
    void stop();

    /**
     * Sets a vision option to the given value.
     *
     * @param option option to set
     * @param value value to set
     * @param <T> data type of the option.
     */
    <T> void setOption(VisionOption<T> option, T value);

    /**
     * Gets the value associated with the option.
     *
     * @param option option to get value from.
     * @param <T> data type of the option.
     *
     * @return {@link Optional} with the value from the option.
     *      If the option does not have any associated value,
     *      {@link Optional#isPresent()} is <b>false</b>.
     */
    <T> Optional<T> getOption(VisionOption<T> option);

    /**
     * Gets the value associated with the option.
     *
     * @param option option to get value from.
     * @param defaultValue value to return if no value is associated
     *                     with the option.
     * @param <T> data type of the option.
     *
     * @return the value associated with the option. If no value
     *      is associated with the option, <em>defaultValue</em>
     *      is returned.
     */
    <T> T getOptionOrDefault(VisionOption<T> option, T defaultValue);

    /**
     * Gets the latest result produced by the vision process.
     *
     * @return {@link Optional} containing the latest vision result.
     *      If no result is stored, {@link Optional#isPresent()} is <b>false</b>.
     */
    Optional<VisionResult> getLatestResult();

    /**
     * Gets the latest result produced by the vision process.
     * <p>
     *     If the result is cleared, subsequent calls will yield
     *     an empty {@link Optional}.
     * </p>
     *
     * @param clear <b>true</b> to clear the result, <b>false</b> otherwise.
     *
     * @return {@link Optional} containing the latest vision result.
     *      If no result is stored, {@link Optional#isPresent()} is <b>false</b>.
     */
    Optional<VisionResult> getLatestResult(boolean clear);

    /**
     * Gets the latest result produced by the vision process.
     *
     * @param maxTimestamp timestamp limit for the result. If the stored
     *                     result is older than the given timestamp,
     *                     the call yields an empty {@link Optional}.
     *
     * @return {@link Optional} containing the latest vision result.
     *      If no result is stored, {@link Optional#isPresent()} is <b>false</b>.
     */
    Optional<VisionResult> getLatestResult(Time maxTimestamp);

    /**
     * Gets the latest result produced by the vision process.
     * <p>
     *     If the stored result does not meet the timestamp limit,
     *     and the <em>clear</em> flag is on, the stored result
     *     is still cleared.
     * </p>
     * <p>
     *     If the result is cleared, subsequent calls will yield
     *     an empty {@link Optional}.
     * </p>
     *
     * @param maxTimestamp timestamp limit for the result. If the stored
     *                     result is older than the given timestamp,
     *                     the call yields an empty {@link Optional}.
     *
     * @param clear <b>true</b> to clear the result, <b>false</b> otherwise.
     *
     * @return {@link Optional} containing the latest vision result.
     *      If no result is stored, {@link Optional#isPresent()} is <b>false</b>.
     */
    Optional<VisionResult> getLatestResult(Time maxTimestamp, boolean clear);

    /**
     * Registers a new listener to vision events.
     *
     * @param listener listener to register.
     */
    void addListener(VisionListener listener);
}
