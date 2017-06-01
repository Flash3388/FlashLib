package edu.flash3388.flashlib.vision;

import edu.flash3388.flashlib.communications.Sendable;
import edu.flash3388.flashlib.flashboard.FlashboardSendableType;
import edu.flash3388.flashlib.util.FlashUtil;

public class RemoteVision extends Sendable implements Vision{

	private boolean stopRemote = false, startRemote = false, localParam = true, updateForFileParam = false,
			updateForRemoteParam = false;
	private VisionProcessing processing;
	private Analysis analysis;
	private boolean send = false, newAnalysis = false, stopping = false, updateProcessing = false;
	private double camOffset, targetHeight, targetWidth;
	private long lastRec;
	
	public RemoteVision(boolean remote, double camOffset) {
		super(FlashboardSendableType.VISION);
		enableRemoteParameters(remote);
		this.camOffset = camOffset;
	}
	public RemoteVision(boolean remote){
		this(remote, 0);
	}
	public RemoteVision(){
		this(false);
	}

	private void setRobotParam(){
		updateForRemoteParam = true;
		updateForFileParam = false;
	}
	private void setRemoteParam(){
		updateForFileParam = true;
		updateForRemoteParam = false;
	}
	private void startRemote(){
		startRemote = true;
	}
	private void stopRemote(){
		stopRemote = true;
	}
	public boolean isLocalParam(){
		return localParam;
	}
	
	@Override
	public void start(){
		if(processing == null && isLocalParam())
			enableRemoteParameters(true);
		startRemote();
		send = true;
		lastRec = FlashUtil.millis();
	}
	@Override
	public void stop(){
		stopRemote();
		stopping = true;
	}
	public boolean isRunning(){
		return send;
	}
	public void enableRemoteParameters(boolean enable){
		if(enable && isLocalParam())
			setRemoteParam();
		else if(!enable && !isLocalParam())
			setRobotParam();
	}
	@Override
	public void setProcessing(VisionProcessing proc) {
		enableRemoteParameters(false);
		processing = proc;
		FlashUtil.getLog().log("Vision Prameters are set!!");
	}
	@Override
	public VisionProcessing getProcessing() {
		return processing;
	}
	@Override
	public Analysis getAnalysis(){
		newAnalysis = false;
		return analysis;
	}
	@Override
	public boolean hasNewAnalysis(){
		return newAnalysis;
	}
	
	@Override
	public void newData(byte[] data) {
		if(!send) return;
		if(stopping){
			stopping = false;
			send = false;
			return;
		}
		Analysis an = Analysis.fromBytes(data);
		
		if(an != null) {
			long t = FlashUtil.millis() - lastRec;
			if(t > 1000)
				FlashUtil.getLog().log("New analysis: "+t);
			lastRec = FlashUtil.millis();
			analysis = an;
			newAnalysis = true;
		}
	}
	@Override
	public byte[] dataForTransmition() {
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
			System.out.println("Vision: Robot Parameters");
			return new byte[]{0,0};
		}
		
		if(localParam) return null;
		FlashUtil.getLog().log("Sending Vision parameters");
		updateProcessing = false;
		return processing.toBytes();
	}
	@Override
	public boolean hasChanged() {
		return send && ((updateProcessing && processing != null) || stopRemote || startRemote || 
				updateForFileParam || updateForRemoteParam);
	}
	@Override
	public void onConnection() {
		updateProcessing = true;
		if(!localParam && !updateForFileParam) {
			updateForFileParam = true;
			System.out.println("File param");
		}
		else if(localParam && !updateForRemoteParam) {
			updateForRemoteParam = true;
			System.out.println("Remote param");
		}
		if(isRunning()) startRemote = true;
		else stopRemote = true;
		
		FlashUtil.getLog().log("Re connection: "+localParam+" :: "+isRunning());
	}
	@Override
	public void onConnectionLost() {
	}

	@Override
	public void setCameraOffsetAngle(double angle) {
		camOffset = angle;
	}
	@Override
	public double getCameraOffsetAngle() {
		return camOffset;
	}
	@Override
	public void setTargetHeight(double h) {
		targetHeight = h;
	}
	@Override
	public double getTargetHeight() {
		return targetHeight;
	}
	@Override
	public void setTargetWidth(double w) {
		targetWidth = w;
	}
	@Override
	public double getTargetWidth() {
		return targetWidth;
	}
}
