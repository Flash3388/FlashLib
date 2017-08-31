package edu.flash3388.flashlib.robot.hid;

import edu.flash3388.flashlib.robot.RobotFactory;

/**
 * Represents trigger on a console controller.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public class Triggers{
	
	/**
	 * A single trigger wrapper. Extends the button class. The button is considered pushed when the
	 * value of the trigger exceeds a certain limit.
	 * 
	 * @author Tom Tzook
	 * @since FlashLib 1.0.0
	 */
	public static class Trigger extends Button{

		private double sensitivity = 1;
		
		/**
		 * Creates a new trigger.
		 * @param stick the device index
		 * @param number the axis index
		 */
		public Trigger(int stick, int number) {
			super(stick, number);
		}
		
		/**
		 * Sets the sensitivity of the axis. When exceeded, the button is considered pushed.
		 * @param sensitivity  the sensitivity of the object
		 */
		public void setButtonSensitivity(double sensitivity){
			if(sensitivity < 0) sensitivity *= -1;
			else if(sensitivity == 0) 
				throw new IllegalArgumentException("sensitivity cannot be 0");
			
			this.sensitivity = sensitivity;
		}
		
		/**
		 * Gets the value of the trigger.
		 * @return the value of the trigger
		 */
		public double getValue(){
			return RobotFactory.getImplementation().getHIDInterface().getHIDAxis(getJoystick(), getNumber());
		}
		/**
		 * Gets the sensitivity of the axis. When exceeded, the button is considered pushed.
		 * @return the sensitivity of the object
		 */
		public double getSensitivity(){
			return sensitivity;
		}
		
		/**
		 * Refreshes the trigger. Used to determine whether or not to run 
		 * actions attached to those wrapped. 
		 */
		@Override
		public void refresh(){
			set(getValue() >= getSensitivity());
		}
	}
	
	public final Trigger Right, Left;
	
	/**
	 * Creates a new object for controller triggers
	 * @param stick the stick index
	 * @param numberL the index of the left trigger
	 * @param numberR the index of the right trigger
	 */
	public Triggers(int stick, int numberL, int numberR){ 
		Right = new Trigger(stick, numberR);
		Left =  new Trigger(stick, numberL);
	}
	
	/**
	 * Gets the value of both triggers
	 * @return Combined value of both triggers
	 */
	public double getCombined() { 
		return Right.getValue() - Left.getValue();
	}
	
	/**
	 * Refreshes the triggers. Used to determine whether or not to run 
	 * actions attached to those wrapped. 
	 */
	public void refresh() {
		Left.refresh();
		Right.refresh();
	}
}
