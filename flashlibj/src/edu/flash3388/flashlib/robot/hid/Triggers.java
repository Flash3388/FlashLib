package edu.flash3388.flashlib.robot.hid;

import edu.flash3388.flashlib.robot.RobotFactory;

public class Triggers{
	
	public static class Trigger extends Button{

		private double val = 0, sensitivity = 1;
		
		public Trigger(int stick, int number) {
			super(stick, number);
		}
		
		public void setButtonSensitivity(double sensitivity){
			if(sensitivity < 0) sensitivity *= -1;
			else if(sensitivity == 0) 
				throw new IllegalArgumentException("sensitivity cannot be 0");
			
			this.sensitivity = sensitivity;
		}
		
		public double getValue(){
			return val;
		}
		public double getSensitivity(){
			return sensitivity;
		}
		protected void setValue(double val){
			this.val = val;
		}
		
		@Override
		public void refresh(){
			setValue(RobotFactory.getStickAxis(getJoystick(), getNumber()));
			set(getValue() >= getSensitivity());
		}
	}
	
	public final Trigger Right, Left;
	
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
	
	public void refresh() {
		Left.refresh();
		Right.refresh();
	}
}
