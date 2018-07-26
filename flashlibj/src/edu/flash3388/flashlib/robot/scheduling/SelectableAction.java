package edu.flash3388.flashlib.robot.scheduling;

import edu.flash3388.flashlib.util.FlashUtil;
import edu.flash3388.flashlib.util.beans.DoubleSource;
import edu.flash3388.flashlib.util.beans.IntegerSource;

import java.util.List;

/**
 * An action which executes a selected action when started. 
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public class SelectableAction extends Action {

	private List<Action> mActions;
	private IntegerSource mSelectionSource;

	private Action mSelectedAction;
	
	/**
	 * Creates a new selectable action. 
	 * 
	 * @param source the index source
	 * @param actions an array of scheduling to select from
	 */
	public SelectableAction(IntegerSource selectionSource, List<Action> actions){
		mSelectionSource = selectionSource;
		mActions = actions;
	}

	/**
	 * Adds a new action to the action array.
	 * 
	 * @param action action to be added
	 * @return this instance
	 */
	public SelectableAction addAction(Action action){
		mActions.add(action);
		return this;
	}

	/**
	 * Removes the action at the given array index.
	 * 
	 * @param action action to remove
	 * @return this instance
	 * @throws IndexOutOfBoundsException if the index is out of the array bounds
	 */
	public SelectableAction removeAction(Action action){
		mActions.remove(action);
		return this;
	}
	
	@Override
	protected void initialize(){ 
		int selectedIndex = mSelectionSource.get();

		if (selectedIndex < 0 || selectedIndex >= mActions.size()) {
			cancel();
		}

		mSelectedAction = mActions.get(selectedIndex);
		mSelectedAction.start();
	}

	@Override
	protected void execute(){
	}

	@Override
	protected boolean isFinished(){ 
		return !mSelectedAction.isRunning();
	}

	@Override
	protected void end(){
		if (mSelectedAction.isRunning()) {
			mSelectedAction.cancel();
		}

		mSelectedAction = null;
	}
}
