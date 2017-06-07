package edu.flash3388.flashlib.robot;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Vector;

public abstract class CombinedAction extends Action{
	
	private Vector<Action> actions = new Vector<Action>();
	private ArrayList<Action> currentActions = new ArrayList<Action>();
	
	public void add(Action part){
		if(part == null)
			 return;
		actions.add(part);
	}
	public Action get(int index){
		return actions.get(index);
	}
	public void remove(Action part){
		if(part == null)
			 return;
		actions.remove(part);
	}
	public void remove(int index){
		actions.remove(index);
	}
	
	protected void initialize(){ 
		for (Enumeration<Action> parts = actions.elements(); parts.hasMoreElements();) {
			Action part = parts.nextElement();
			part.initialize();
			currentActions.add(part);
		}
	}
	
	protected boolean isFinished(){ 
		boolean is = true;
		for (int i = currentActions.size() - 1; i >= 0; i--) {
			Action part = currentActions.get(i);
			if(part.isFinished())
				currentActions.remove(i);
			else if(is) 
				is = false;
		}
		return is;
	}
	@Override
	protected void end(){
		for (int i = currentActions.size() - 1; i >= 0; i--) {
			currentActions.get(i).end();
			currentActions.remove(i);
		}
	}

}
