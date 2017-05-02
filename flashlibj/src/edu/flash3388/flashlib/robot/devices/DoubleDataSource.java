package edu.flash3388.flashlib.robot.devices;

@FunctionalInterface
public interface DoubleDataSource {
	public static class VarDataSource implements DoubleDataSource{
		private double var;
		
		public VarDataSource(double initialVal){
			var = initialVal;
		}
		public VarDataSource(){
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
	
	double get();
}
