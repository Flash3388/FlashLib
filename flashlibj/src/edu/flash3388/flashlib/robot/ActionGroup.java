package edu.flash3388.flashlib.robot;

import java.util.Enumeration;
import java.util.Vector;

import edu.flash3388.flashlib.util.FlashUtil;
import edu.flash3388.flashlib.util.Queue;

public class ActionGroup extends Action{

	private static class Entry{
		Action action;
		boolean sequential;
	}
	
	private Vector<Entry> actions = new Vector<Entry>(5);
	private int index = -1;
	private Vector<Entry> current = new Vector<Entry>(5);
	private Queue<Entry> entries = new Queue<Entry>();
	
	public ActionGroup(){}
	public ActionGroup(Action...actions){
		this(true, actions);
	}
	public ActionGroup(boolean sequential, Action...actions){
		if(sequential)
			addSequential(actions);
		else addParallel(actions);
	}
	
	public ActionGroup addSequential(Action action, double timeout){
		addSequential(action);
		action.setTimeOut((long) (timeout * 1000));
		return this;
	}
	public ActionGroup addSequential(Action action){
		Entry entry = new Entry();
		entry.action = action;
		entry.sequential = true;
		this.actions.add(entry);
		return this;
	}
	public ActionGroup addSequential(Action... actions){
		for(Action action : actions)
			addSequential(action);
		return this;
	}
	public ActionGroup addWaitAction(double seconds){
		addSequential(new TimedAction(Action.EMPTY, seconds));
		return this;
	}
	
	public ActionGroup addParallel(Action action, double timeout){
		addParallel(action);
		action.setTimeOut((long) (timeout * 1000));
		return this;
	}
	public ActionGroup addParallel(Action action){
		Entry entry = new Entry();
		entry.action = action;
		entry.sequential = false;
		this.actions.add(entry);
		return this;
	}
	public ActionGroup addParallel(Action... actions){
		for(Action action : actions)
			addParallel(action);
		return this;
	}
	
	@Override
	protected void initialize(){
		index = 0;
		Entry c = actions.elementAt(index);
		FlashUtil.getLog().log("Stating action-"+c.action.getClass().getName());
		c.action.start();
		current.addElement(c);
	}
	@Override
	protected void execute() {
		Entry c = actions.elementAt(index); boolean next = false;
		if(!c.action.isRunning()){
			current.removeElement(c);
			index++;
			next = true;
		}
		else if(!c.sequential){
			index++;
			next = true;
		}
		
		for(Enumeration<Entry> en = current.elements(); en.hasMoreElements();){
			 Entry entry = en.nextElement();
			 if(!entry.action.isRunning()){
				 current.remove(entry);
			 }
		}
		
		if(next && index < actions.size()){
			Entry toRun = actions.elementAt(index);
			FlashUtil.getLog().log("Starting action-"+toRun.action.getClass().getName());
			toRun.action.start();
			current.add(toRun);
		}
	}
	@Override
	protected boolean isFinished() {
		return index >= actions.size();
	}
	@Override
	protected void end() {
		for(Enumeration<Entry> en = current.elements(); en.hasMoreElements();){
			Entry entry = en.nextElement();
			if(entry.action.isRunning())
				entry.action.cancel();
		}
		current.clear();
		index = -1;
	}
}
