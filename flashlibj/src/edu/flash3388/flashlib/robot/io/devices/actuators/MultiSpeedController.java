package edu.flash3388.flashlib.robot.io.devices.actuators;

import java.util.*;

/**
 * A container for multiple speed controllers. Grouping controllers in such manner allows simultaneous 
 * control of several motors. When motors should be activated together, this container is extremly useful.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public class MultiSpeedController implements FlashSpeedController {

	private List<FlashSpeedController> mControllers;
	private boolean mIsInverted;
	
	/**
	 * Creates a new container for an array of speed controller.
	 * 
	 * @param controllers array of controllers to be contained
	 */
	public MultiSpeedController(FlashSpeedController...controllers) {
		this(Arrays.asList(controllers));
	}
	/**
	 * Creates a new container for a list of speed controller.
	 * 
	 * @param controllers list of controllers to be contained
	 */
	public MultiSpeedController(Collection<FlashSpeedController> controllers){
		mControllers = new ArrayList<FlashSpeedController>(controllers);

		setInverted(false);
		set(0);
	}
	
	/**
	 * Gets a controller held in this container by the index
	 *
	 * @param index the index of the controller
	 * @return a controller from the container
	 */
	public FlashSpeedController getController(int index){
		return mControllers.get(index);
	}

	/**
	 * Gets the amount of controllers held in this container.
	 * @return the amount of controllers
	 */
	public int getControllerCount(){
		return mControllers.size();
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * Sets the speed value to all the speed controllers contained in this object.
	 * </p>
	 */
	@Override
	public void set(double speed) {
		for (FlashSpeedController controller : mControllers) {
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
		for (FlashSpeedController controller : mControllers) {
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

		for (FlashSpeedController controller : mControllers) {
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

		for (FlashSpeedController controller : mControllers) {
			controller.setInverted(inverted);
		}
	}
}
