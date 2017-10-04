package edu.flash3388.flashlib.flashboard;

/**
 * Type values for Flashboard controls.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public class FlashboardSendableType {
	private FlashboardSendableType(){}
	
	public static final byte ESTOP = 0x00;
	public static final byte CHOOSER = 0x01;
	public static final byte ACTIVATABLE = 0x02;
	public static final byte JOYSTICK = 0x03;
	public static final byte PIDTUNER = 0x04;
	public static final byte LABEL = 0x05;
	public static final byte DIR_INDICATOR = 0x06;
	public static final byte BOOL_INDICATOR = 0x07;
	public static final byte LOG = 0x08;
	public static final byte TESTER = 0x09;
	public static final byte MOTOR = 0x0a;
	public static final byte VISION = 0x0b;
	public static final byte MODE_SELECTOR = 0x0c;
	public static final byte PDP = 0x0e;
	public static final byte INPUT = 0x0f;
	public static final byte SLIDER = 0x10;
	public static final byte CHECKBOX = 0x11;
}
