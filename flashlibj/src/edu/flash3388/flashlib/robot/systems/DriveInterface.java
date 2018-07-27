package edu.flash3388.flashlib.robot.systems;

/**
 * Interface for drive systems. Extends several different interfaces:
 * <ul>
 * 	<li> {@link Rotatable} </li>
 *  <li> {@link YAxisDriveInterface} </li>
 * </ul>
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public interface DriveInterface extends Rotatable, YAxisDriveInterface {
}
