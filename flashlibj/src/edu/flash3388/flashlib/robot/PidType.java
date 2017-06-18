package edu.flash3388.flashlib.robot;

/**
 * Holds the types of data that can be returned from a PID loop feedback sensor: 
 * <ul>
 * 	<li> {@link PidType#Displacement}: position or rotation</li>
 * 	<li> {@link PidType#Rate}: speed of position or rotation</li>
 * </ul>
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 * @see PidSource
 */
public enum PidType {
	Rate, Displacement
}
