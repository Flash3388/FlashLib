package edu.flash3388.flashlib.math;

import edu.flash3388.flashlib.math.Mathd.Function;

public class PidFunction implements Function{

	private static class ErrorFunction implements Function{
		private Function error;
		private double setPoint;
		
		public ErrorFunction(Function error, double setpoint){
			this.error = error;
			this.setPoint = setpoint;
		}
		
		public void setErrorFunction(Function error){
			this.error = error;
		}
		public Function getErrorFunction(){
			return error;
		}
		public void setSetpoint(double sp){
			this.setPoint = sp;
		}
		public double getSetpoint(){
			return setPoint;
		}
		
		@Override
		public double f(double x) {
			return setPoint - error.f(x);
		}
	}
	
	private ErrorFunction errorFunction;
	private double kp;
	private double ki;
	private double kd;
	
	public PidFunction(double kp, double ki, double kd, double setPoint, Function errorFunction){
		this.kp = kp;
		this.ki = ki;
		this.kd = kd;
		this.errorFunction = new ErrorFunction(errorFunction, setPoint);
	}
	
	public void setP(double p){
		kp = p;
	}
	public void setI(double i){
		ki = i;
	}
	public void setD(double d){
		kd = d;
	}
	public void setPID(double p, double i, double d){
		kp = p;
		ki = i;
		kd = d;
	}
	public double getP(){
		return kp;
	}
	public double getI(){
		return ki;
	}
	public double getD(){
		return kd;
	}
	
	@Override
	public double f(double x) {
		return kp * errorFunction.f(x) + ki * Mathd.integrate(errorFunction, 0, x) + 
				kd * Mathd.derive(errorFunction, x);
	}
}
