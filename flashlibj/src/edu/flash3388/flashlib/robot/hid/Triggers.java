package edu.flash3388.flashlib.robot.hid;

import edu.flash3388.flashlib.robot.RobotFactory;

public class Triggers{
	
	public static abstract class Trigger extends Button{

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
	}
	
	private double combined = 0;
	public Trigger Right, Left;
	
	public Triggers(int stick, int numberL, int numberR){ 
		Right = RobotFactory.createTrigger(stick, numberR);
		Left = RobotFactory.createTrigger(stick, numberL);
		combine();
	}
	
	public void combine(){ combined = Right.getValue() - Left.getValue(); }
	
	/**
	 * Gets the value of both triggers
	 * @return
	 */
	public double getCombined() { return combined; }
	
	public void refresh() {
		Left.refresh();
		Right.refresh();
	}
}
