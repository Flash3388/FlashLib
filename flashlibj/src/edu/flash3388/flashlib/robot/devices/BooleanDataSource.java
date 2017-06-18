package edu.flash3388.flashlib.robot.devices;

/**
 * A wrapper for boolean data.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
@FunctionalInterface
public interface BooleanDataSource {
	/**
	 * A boolean data wrapper which contains a variable. 
	 * 
	 * @author Tom Tzook
	 * @since FlashLib 1.0.0
	 */
	public static class VarDataSource implements BooleanDataSource{
		private boolean var;
		
		public VarDataSource(boolean initialVal){
			var = initialVal;
		}
		public VarDataSource(){
			this(false);
		}
		
		public boolean switchValue(){
			var = !var;
			return var;
		}
		public void set(boolean b){
			var = b;
		}
		@Override
		public boolean get() {
			return var;
		}
		
	}
	
	/**
	 * Gets the boolean data in the wrapper
	 * @return boolean data
	 */
	boolean get();
}
