package edu.flash3388.flashlib.robot;

import edu.flash3388.flashlib.math.Mathd;
import edu.flash3388.flashlib.robot.devices.Gyro;
import edu.flash3388.flashlib.vision.Vision;

public class PidController {
	
	public static class GyroPidSource implements PidSource{

		private PidType type;
		private Gyro gyro;
		
		public GyroPidSource(Gyro gyro, PidType t){
			this.gyro = gyro;
			this.type = t;
		}
		public GyroPidSource(Gyro gyro){
			this(gyro, PidType.Displacement);
		}
		
		@Override
		public double pidGet() {
			return gyro.getAngle();
		}
		@Override
		public PidType getType() {
			return type;
		}
	}
	public static class VisionPidSource implements PidSource{

		private PidType type;
		private Vision vision;
		private double previous = 0.0;
		private boolean horizontal;
		
		public VisionPidSource(Vision vision, PidType t, boolean horizontal){
			this.vision = vision;
			this.horizontal = horizontal;
			this.type = t;
		}
		public VisionPidSource(Vision vision, boolean horizontal){
			this(vision, PidType.Displacement, horizontal);
		}
		
		public void setVision(Vision vision){
			this.vision = vision;
		}
		public Vision getVision(){
			return vision;
		}
		public void setHorizontal(boolean horizontal){
			this.horizontal = horizontal;
		}
		public boolean getHorizontal(){
			return horizontal;
		}
		
		@Override
		public double pidGet() {
			if(!vision.hasNewAnalysis()) return previous;
			previous = horizontal? 
					vision.getAnalysis().horizontalDistance : 
					vision.getAnalysis().verticalDistance;
			return previous;
		}
		@Override
		public PidType getType() {
			return type;
		}
	}
	public static class DoublePidSource implements PidSource{

		private PidType type;
		private double value;
		
		public DoublePidSource(PidType t){
			this.type = t;
		}
		public DoublePidSource(){
			this(PidType.Displacement);
		}
		
		public void setValue(double val){
			this.value = val;
		}
		@Override
		public double pidGet() {
			return value;
		}
		@Override
		public PidType getType() {
			return type;
		}
	}
	
	private PidSource source;
	private double setPoint;
	private double minimumInput = -1, maximumInput = 1;
	private double minimumOutput = -1, maximumOutput = 1;
	private double kp, ki, kd;
	private double totalError, error, preError;
	private boolean enabled = true;
	
	public PidController(double kp, double ki, double kd, double setPoint, PidSource source){
		this.kp = kp;
		this.ki = ki;
		this.kd = kd;
		this.setPoint = setPoint;
		this.source = source;
	}
	public PidController(double kp, double ki, double kd, double setPoint){
		this(kp, ki, kd, setPoint, null);
	}
	public PidController(double kp, double ki, double kd){
		this(kp, ki, kd, 0);
	}
	public PidController(double kp, double kd){
		this(kp, 0, kd, 0);
	}
	public PidController(double kp){
		this(kp, 0, 0, 0);
	}
	
	public double calculate(){
		if(!enabled) return 0;
		if(source == null)
			throw new IllegalStateException("PID Source is missing!");
		
		double currentVal = source.pidGet();
		double result = 0;
		error = setPoint - currentVal;
		
		if(source.getType() == PidType.Rate){
			double pGain = (totalError + error) * kp;
			if(Mathd.limited(pGain, minimumOutput, maximumOutput))
				totalError += error;
			else if(pGain < maximumOutput)
				totalError = minimumOutput / kp;
			else 
				totalError = maximumOutput / kp;
			
			result = kp * totalError + kd * error;
		}else{//DISPLACEMENT!
			double iGain = (totalError + error) * ki;
			if(Mathd.limited(iGain, minimumOutput, maximumOutput))
				totalError += error;
			else if(iGain < maximumOutput)
				totalError = minimumOutput / ki;
			else 
				totalError = maximumOutput / ki;
			
			result = kp * error + ki * totalError + kd * (error - preError);
		}
		
		preError = error;
		result = Mathd.limit(result, minimumOutput, maximumOutput);
		return result;
	}
	
	public double getMaximumInput(){
		return maximumInput;
	}
	public double getMinimumInput(){
		return minimumInput;
	}
	public double getMaximumOutput(){
		return maximumOutput;
	}
	public double getMinimumOutput(){
		return minimumOutput;
	}
	public double getP(){
		return kp;
	}
	public double getI(){
		return ki;
	}
	public double getInegralTime(){
		return kp / ki;
	}
	public double getD(){
		return kd;
	}
	public double getDerivativeTime(){
		return kd / kp;
	}
	public double getSetPoint(){
		return setPoint;
	}
	public PidSource getSource(){
		return source;
	}
	
	public void setMaximumInput(double m){
		this.maximumInput = m;
	}
	public void setMinimumInput(double m){
		this.minimumInput = m;
	}
	public void setInputLimit(double l){
		this.maximumInput = l;
		this.minimumInput = -l;
	}
	public void setMaximumOutput(double m){
		this.maximumOutput = m;
		this.setPoint = Mathd.limit(setPoint, minimumOutput, maximumOutput);
	}
	public void setMinimumOutput(double m){
		this.minimumOutput = m;
		this.setPoint = Mathd.limit(setPoint, minimumOutput, maximumOutput);
	}
	public void setOutputLimit(double l){
		this.maximumOutput = l;
		this.minimumOutput = -l;
		this.setPoint = Mathd.limit(setPoint, minimumOutput, maximumOutput);
	}
	public void setP(double p){
		this.kp = p;
	}
	public void setI(double i){
		this.ki = i;
	}
	public void setInegralTime(double t){
		this.ki = kp / t;
	}
	public void setD(double d){
		this.kd = d;
	}
	public void setDerivativeTime(double t){
		this.kd = t * kp;
	}
	public void setPID(double p, double i, double d){
		this.kp = p;
		this.ki = i;
		this.kd = d;
	}
	public void setSetPoint(double setpoint){
		this.setPoint = Mathd.limit(setpoint, minimumOutput, maximumOutput);
	}
	public void setPIDSource(PidSource source){
		this.source = source;
	}
	
	public boolean isEnabled(){
		return enabled;
	}
	public void setEnabled(boolean enable){
		this.enabled = enable;
	}
}
