package edu.flash3388.flashlib.vision;

public abstract class FilterParam {
	
	public static class DoubleParam extends FilterParam{

		private double value;
		
		public DoubleParam(String name, double value) {
			super(name);
			this.value = value;
		}

		@Override
		public String getValue() {
			return String.valueOf(value);
		}
		@Override
		public String getType() {
			return "Double";
		}
		
	}
	public static class IntParam extends FilterParam{

		private int value;
		
		public IntParam(String name, int value) {
			super(name);
			this.value = value;
		}

		@Override
		public String getValue() {
			return String.valueOf(value);
		}
		@Override
		public String getType() {
			return "Int";
		}
		
	}
	public static class BooleanParam extends FilterParam{

		private boolean value;
		
		public BooleanParam(String name, boolean value) {
			super(name);
			this.value = value;
		}

		@Override
		public String getValue() {
			return String.valueOf(value);
		}
		@Override
		public String getType() {
			return "Boolean";
		}
		
	}
	
	private String name;
	
	public FilterParam(String name){
		this.name = name;
	}
	
	public String getName(){
		return name;
	}
	
	public abstract String getValue();
	public abstract String getType();
	
	public static double getDoubleValue(FilterParam param){
		if(param == null || !(param instanceof DoubleParam))
			return 0.0;
		return ((DoubleParam)param).value;
	}
	public static int getIntValue(FilterParam param){
		if(param == null || !(param instanceof IntParam))
			return 0;
		return ((IntParam)param).value;
	}
	public static boolean getBooleanValue(FilterParam param){
		if(param == null || !(param instanceof BooleanParam))
			return false;
		return ((BooleanParam)param).value;
	}
	
	public static FilterParam createParam(String name, String type, String value){
		if(name == null)
			throw new RuntimeException("Name value missing");
		if(value == null)
			throw new RuntimeException("Value value missing: "+name);
		if(type == null)
			throw new RuntimeException("Type value missing: "+name);
		
		if(type.equalsIgnoreCase("Int")){
			int val = 0;
			try {
				val = Integer.parseInt(value);
			} catch (NumberFormatException e) {
				throw new RuntimeException("Could not parse param "+name+" to int: "+value);
			}
			return new IntParam(name, val);
		}
		if(type.equalsIgnoreCase("Double")){
			double val = 0;
			try {
				val = Double.parseDouble(value);
			} catch (NumberFormatException e) {
				throw new RuntimeException("Could not parse param "+name+" to double: "+value);
			}
			return new DoubleParam(name, val);
		}
		if(type.equalsIgnoreCase("Boolean")){
			boolean val = false;
			try {
				val = Boolean.parseBoolean(value);
			} catch (NumberFormatException e) {
				throw new RuntimeException("Could not parse param "+name+" to boolean: "+value);
			}
			return new BooleanParam(name, val);
		}
		return null;
	}
}
