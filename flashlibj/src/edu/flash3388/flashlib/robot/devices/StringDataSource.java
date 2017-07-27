package edu.flash3388.flashlib.robot.devices;

/**
 * A wrapper for string data.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
@FunctionalInterface
public interface StringDataSource {
	
	/**
	 * A string data wrapper which contains a variable. 
	 * 
	 * @author Tom Tzook
	 * @since FlashLib 1.0.0
	 */
	public static class StringVarDataSource implements StringDataSource{
		
		private String var;
		
		public StringVarDataSource(String initialVal){
			var = initialVal;
		}
		public StringVarDataSource(){
			this("");
		}
		
		public void set(String var){
			this.var = var;
		}
		@Override
		public String get(){
			return var;
		}
	}
	
	/**
	 * Gets the string data in the wrapper
	 * @return string data
	 */
	String get();
}
