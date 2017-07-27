package edu.flash3388.flashlib.robot.devices;

/**
 * A wrapper for double data.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
@FunctionalInterface
public interface DoubleDataSource {
	/**
	 * A double data wrapper which contains a variable. 
	 * 
	 * @author Tom Tzook
	 * @since FlashLib 1.0.0
	 */
	public static class DoubleVarDataSource implements DoubleDataSource{
		
		private double var;
		
		public DoubleVarDataSource(double initialVal){
			var = initialVal;
		}
		public DoubleVarDataSource(){
			this(0);
		}
		
		public void set(double var){
			this.var = var;
		}
		@Override
		public double get(){
			return var;
		}
	}
	
	/**
	 * Gets the double data in the wrapper
	 * @return double data
	 */
	double get();
}
