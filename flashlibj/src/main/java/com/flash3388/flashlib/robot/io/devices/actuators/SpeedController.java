package com.flash3388.flashlib.robot.io.devices.actuators;

import com.flash3388.flashlib.robot.control.Invertable;
import com.flash3388.flashlib.robot.motion.Direction;
import com.flash3388.flashlib.robot.motion.Stoppable;

/**
 * Interface for speed controllers.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public interface SpeedController extends Stoppable, Invertable {

	/**
	 * Sets the speed of the motor controller by this object. The speed is a percentage known as 
	 * percent voltage bus (vbus), which describes a percentage of the currently available voltage to
	 * be supplied to the motor. The sign of the speed describes the direction of rotation.
	 * <p>
	 * If the motor controller is set to inverted directions, the directions are switched.
	 * </p>
	 * 
	 * @param speed [-1 to 1] describing the percent vbus
	 */
	void set(double speed);

    /**
     * Sets the speed of the motor controller by this object. The speed is a percentage known as
     * percent voltage bus (vbus), which describes a percentage of the currently available voltage to
     * be supplied to the motor.
     * <p>
     * If the motor controller is set to inverted directions, the directions are switched.
     * </p>
     * <p>
     * The default implementation calls {@link #set(double)} and passes it the speed value augmented by the given direction
     * </p>
     *
     * @param speed [0 to 1] describing the absolute percent vbus
     * @param direction the direction of motion
     */
	default void set(double speed, Direction direction) {
	    set(speed * direction.sign());
    }
	
	/**
	 * Stops the motor by setting the speed controller to a stop value.
	 * <p>
	 * Default implementation calls {@link #set(double)}, passing it 0.0.
	 */
	@Override
	default void stop(){
		set(0.0);
	}
	
	/**
	 * Gets the currently set percent vbus in the motor controller.
	 * @return used percent vbus
	 */
	double get();

    /**
     * Sets the reversing of directions by the motor controller.
     * @param inverted true to reverse directions, false otherwise
     */
    @Override
    void setInverted(boolean inverted);

	/**
	 * Gets whether or not the directions of the motor are inverted
	 * @return true if the motor is inverted, false otherwise.
	 */
	@Override
	boolean isInverted();

    /**
     * Sets the reversing of directions by the motor controller.
     * @param inverted true to reverse directions, false otherwise.
     * @return this instance.
     */
	default SpeedController inverted(boolean inverted) {
	    setInverted(inverted);
	    return this;
    }
}
