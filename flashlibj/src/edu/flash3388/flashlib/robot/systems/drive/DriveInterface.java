package edu.flash3388.flashlib.robot.systems.drive;

import edu.flash3388.flashlib.robot.systems.Rotatable;
import edu.flash3388.flashlib.robot.systems.YAxisMovableInterface;

/**
 * Interface for drive systems. Extends several different interfaces:
 * <ul>
 * 	<li> {@link Rotatable} </li>
 *  <li> {@link YAxisMovableInterface} </li>
 * </ul>
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public interface DriveInterface extends Rotatable, YAxisMovableInterface {
}
