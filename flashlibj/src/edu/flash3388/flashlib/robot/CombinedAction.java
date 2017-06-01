package edu.flash3388.flashlib.robot;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Vector;

public abstract class CombinedAction extends Action{

	public static abstract class ActionPart{
		protected abstract void initialize();
		public abstract double getExecute();
		protected abstract boolean isFinished();
		protected abstract void end();
	}
	
	private Vector<ActionPart> actions = new Vector<ActionPart>();
	private ArrayList<ActionPart> currentActions = new ArrayList<ActionPart>();
	
	public void add(ActionPart part){
		actions.add(part);
	}
	public ActionPart get(int index){
		return actions.get(index);
	}
	public void remove(ActionPart part){
		actions.remove(part);
	}
	public void remove(int index){
		actions.remove(index);
	}
	
	protected void initialize(){ 
		for (Enumeration<ActionPart> parts = actions.elements(); parts.hasMoreElements();) {
			ActionPart part = parts.nextElement();
			part.initialize();
			currentActions.add(part);
		}
	}
	
	protected boolean isFinished(){ 
		boolean is = true;
		for (int i = currentActions.size() - 1; i >= 0; i--) {
			ActionPart part = currentActions.get(i);
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
