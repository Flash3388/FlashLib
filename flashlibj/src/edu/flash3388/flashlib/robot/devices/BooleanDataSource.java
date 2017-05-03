package edu.flash3388.flashlib.robot.devices;

@FunctionalInterface
public interface BooleanDataSource {
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
	
	boolean get();
}
