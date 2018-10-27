package edu.flash3388.flashlib.robot.io.devices.actuators;

import java.util.*;

/**
 * A container for multiple speed controllers. Grouping controllers in such manner allows simultaneous 
 * control of several motors. When motors should be activated together, this container is extremly useful.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public class SpeedControllerGroup implements SpeedController {

	private final List<SpeedController> mControllers;
	private boolean mIsInverted;

	/**
	 * Creates a new container for a list of speed controller.
	 * 
	 * @param controllers list of controllers to be contained
	 */
	public SpeedControllerGroup(Collection<SpeedController> controllers){
		mControllers = new ArrayList<SpeedController>(controllers);

		setInverted(false);
		set(0);
	}

	public SpeedControllerGroup(SpeedController... controllers) {
	    this(Arrays.asList(controllers));
    }
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * Sets the speed value to all the speed controllers contained in this object.
	 * </p>
	 */
	@Override
	public void set(double speed) {
		for (SpeedController controller : mControllers) {
			controller.set(speed);
		}
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * Stops all the speed controllers contained in this object.
	 * </p>
	 */
	@Override
	public void stop() {
		for (SpeedController controller : mControllers) {
			controller.stop();
		}
	}

	/**
	 * {@inheritDoc}
	 *<p>
	 * Gets the average speed value of all the speed controllers contained in this object.
	 *</p>
	 */
	@Override
	public double get() {
		double totalSpeed = 0.0;

		for (SpeedController controller : mControllers) {
			totalSpeed += controller.get();
		}

		return totalSpeed / mControllers.size();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isInverted() {
		return mIsInverted;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Sets all the speed controllers contained in this object .
	 * </p>
	 */
	@Override
	public void setInverted(boolean inverted) {
		mIsInverted = inverted;

		for (SpeedController controller : mControllers) {
			controller.setInverted(inverted);
		}
	}
}
