package edu.flash3388.flashlib.robot.hid;


/**
 * Representing a button of a POV such as a D-Pad.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public class POVButton extends Button {
	/**
	 * Represents the type of the button. If the POV is a D-Pad, This could be useful.
	 * 
	 * @author Tom Tzook
	 */
	public static class Type{
		public final byte value;
		/**
		 * The minimum degree to be considered as the button being pressed.
		 */
		public final int minDegree;
		/**
		 * The maximum degree to be considered as the button being pressed.
		 */
		public final int maxDegree;
		
		private Type(int val, int min, int max){ 
			value = (byte) val;
			minDegree = min;
			maxDegree = max;
		}
		
		public boolean get(int degrees){
			if(value == UP.value) 
				return (degrees >= minDegree || degrees <= maxDegree) && degrees >= 0 && degrees <= 360;
			return degrees >= minDegree && degrees <= maxDegree && degrees >= 0 && degrees <= 360;
		}
		
		/**
		 * The Up button on a D-Pad.
		 */
		public static final Type UP = new Type(-1, 315, 45);
		/**
		 * The Down button on a D-Pad.
		 */
		public static final Type DOWN = new Type(-2, 135, 255);
		/**
		 * The Right button on a D-Pad.
		 */
		public static final Type RIGHT = new Type(-3, 45, 135);
		/**
		 * The Left button on a D-Pad.
		 */
		public static final Type LEFT = new Type(-4, 255, 315);
		
		public static final Type ALL = new Type(-5, 0, 360);
	}
	
	private Type type;
	private POVEvent eventPov;
	
	/**
	 * Creates a new instance of POVButton. The created button is configured to the given type.
	 * 
	 * @param name The name of the button, just for representation.
	 * @param stick The joystick the button belongs to.
	 * @param num the pov number
	 * @param t The type of the POVButton.
	 */
	public POVButton(String name, int stick, int num, Type t) {
		super(name, stick, num);
		type = t;
		eventPov = new POVEvent(name, stick, num, t);
		super.event = eventPov;
	}
	
	public void set(int degrees){
		eventPov.degrees = degrees;
		super.set(type.get(degrees));
	}
}
