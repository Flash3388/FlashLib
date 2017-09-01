package edu.flash3388.flashlib.robot.hid;

/**
 * Representing a button of a POV such as a D-Pad.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public class DPadButton extends HIDButton {
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
			if(value == POV.value)
				return degrees != -1;
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
		/**
		 * Represents the entire POV
		 */
		public static final Type POV = new Type(-5, 0, 360);
	}
	
	private Type type;
	
	/**
	 * Creates a new instance of POVButton. The created button is configured to the given type.
	 * 
	 * @param hid the HID
	 * @param num the pov number
	 * @param t The type of the POVButton.
	 */
	public DPadButton(HID hid, int num, Type t) {
		super(hid, num);
		type = t;
	}
	
	/**
	 * Gets the current button state
	 */
	@Override
	public boolean get() {
		return type.get(getHID().getRawPOV(getButtonNumber()));
	}
}
