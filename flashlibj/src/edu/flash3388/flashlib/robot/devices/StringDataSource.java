package edu.flash3388.flashlib.robot.devices;

@FunctionalInterface
public interface StringDataSource {
	public static class VarDataSource implements StringDataSource{
		
		private String var;
		
		public VarDataSource(String initialVal){
			var = initialVal;
		}
		public VarDataSource(){
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
	
	String get();
}
