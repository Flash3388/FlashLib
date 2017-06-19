package edu.flash3388.flashlib.robot.systems;

import edu.flash3388.flashlib.robot.devices.ModableMotor;

/**
 * Interface for drive systems. Extends several different interfaces:
 * <ul>
 * 	<li> {@link Rotatable} </li>
 *  <li> {@link XAxisMovable} </li>
 *  <li> {@link YAxisMovable} </li>
 *  <li> {@link ModableMotor} </li>
 *  <li> {@link VoltageScalable} </li>
 * </ul>
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public interface DriveSystem extends Rotatable, XAxisMovable, YAxisMovable, ModableMotor, VoltageScalable{
}
