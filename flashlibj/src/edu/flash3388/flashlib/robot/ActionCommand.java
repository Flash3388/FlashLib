package edu.flash3388.flashlib.robot;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Subsystem;

/**
 * Wraps an action inside a WPILib command object
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public class ActionCommand extends Command{
	
	private Action action;
	
	public ActionCommand(Action action, Subsystem...systems){
		this.action = action;
		
		if(systems != null){
			for(Subsystem system : systems)
				requires(system);
		}
		if(action.getTimeOut() > 0)
			setTimeout(action.getTimeOut());
	}
	public ActionCommand(Action action, Subsystem system){
		this.action = action;
		
		if(system != null)
			requires(system);
		
		if(action.getTimeOut() > 0)
			setTimeout(action.getTimeOut());
	}
	public ActionCommand(Action action){
		this(action, (Subsystem[])null);
	}
	
	public Action getAction(){
		return action;
	}
	
	@Override
	protected void initialize() {
		action.initialize();
	}
	@Override
	protected void execute() {
		action.execute();
	}
	@Override
	protected boolean isFinished() {
		return action.isFinished();
	}
	@Override
	protected void end() {
		action.end();
	}
	@Override
	protected void interrupted() {
		action.interrupted();
	}
}
