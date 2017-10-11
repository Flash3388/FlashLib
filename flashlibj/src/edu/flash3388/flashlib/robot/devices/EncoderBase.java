package edu.flash3388.flashlib.robot.devices;

import edu.flash3388.flashlib.util.FlashUtil;

public abstract class EncoderBase implements Encoder{

	private static final int DEFAULT_REST_TIMEOUT = 200;
	
	private EncoderDataType pidType = EncoderDataType.Rate;
	
	private int lastCount;
	private int lastCheckTime;
	private int restTimeout = DEFAULT_REST_TIMEOUT;
	private boolean checkRest = false;
	
	public void enableRestCheck(boolean restCheck){
		checkRest = restCheck;
		if(restCheck){
			lastCheckTime = 0;
			lastCount = 0;
		}
	}
	public boolean isRestCheckEnabled(){
		return checkRest;
	}
	
	public void setRestTimeout(int timeout){
		restTimeout = timeout;
	}
	public int getRestTimeout(){
		return restTimeout;
	}
	
	@Override
	public void reset() {
		lastCheckTime = 0;
		lastCount = 0;
	}
	
	@Override
	public EncoderDataType getDataType() {
		return pidType;
	}
	@Override
	public void setDataType(EncoderDataType type) {
		pidType = type;
	}
	
	protected void checkRest(){
		if(checkRest){
			int time = FlashUtil.millisInt();
			int count = getRaw();
			
			if(lastCheckTime == 0)
				lastCheckTime = time;
			if(lastCount == count && time - lastCheckTime >= restTimeout)
				reset();
			
			lastCheckTime = time;
			lastCount = count;
		}
	}
}
