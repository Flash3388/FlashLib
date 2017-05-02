package edu.flash3388.flashlib.vision;

import edu.flash3388.flashlib.communications.SendableData;
import edu.flash3388.flashlib.util.FlashUtil;

public class VisionSendableData implements SendableData{

	private Vision vision;
	private ProcessingParam param;
	private ProcessingParam copy;
	private boolean stopRemote = false, startRemote = false, localParam = true, updateForFileParam = false,
			updateForRemoteParam = false;
	
	public VisionSendableData(Vision v){
		this.vision = v;
	}
	
	public void setRoborioParam(){
		updateForRemoteParam = true;
		updateForFileParam = false;
	}
	public void setRemoteParam(){
		updateForFileParam = true;
		updateForRemoteParam = false;
	}
	public void startRemote(){
		startRemote = true;
	}
	public void stopRemote(){
		stopRemote = true;
	}
	public void setParam(ProcessingParam p){
		this.param = p;
	}
	public boolean isLocalParam(){
		return localParam;
	}
	
	@Override
	public byte[] get() {
		if(stopRemote){
			stopRemote = false;
			System.out.println("Vision: Stop");
			return new byte[]{1,0};
		}
		if(startRemote){
			startRemote = false;
			System.out.println("Vision: Start");
			return new byte[]{1,1};
		}
		if(updateForFileParam){
			updateForFileParam = false;
			updateForRemoteParam = false;
			localParam = false;
			System.out.println("Vision: Remote Parameters");
			return new byte[]{0, 1};
		}
		if(updateForRemoteParam){
			updateForRemoteParam = false;
			updateForFileParam = false;
			localParam = true;
			System.out.println("Vision: RoboRIO Parameters");
			return new byte[]{0,0};
		}
		
		if(localParam) return null;
		FlashUtil.getLog().log("Sending Vision parameters");
		copy = param.copy();
		return param.toBytes();
	}
	@Override
	public boolean hasChanged() {
		return (param != null && !param.equals(copy)) || stopRemote || startRemote || updateForFileParam || updateForRemoteParam;
	}
	@Override
	public void onConnection() {
		copy = null;
		if(!localParam && !updateForFileParam) {
			updateForFileParam = true;
			System.out.println("File param");
		}
		else if(localParam && !updateForRemoteParam) {
			updateForRemoteParam = true;
			System.out.println("Remote param");
		}
		if(vision.isRunning()) startRemote = true;
		else stopRemote = true;
		
		FlashUtil.getLog().log("Re connection: "+localParam+" :: "+vision.isRunning());
	}
	@Override
	public void onConnectionLost() {
	}
}
