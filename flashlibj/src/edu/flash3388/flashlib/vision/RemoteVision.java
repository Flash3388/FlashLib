package edu.flash3388.flashlib.vision;

import java.util.ArrayList;

import edu.flash3388.flashlib.communications.Sendable;
import edu.flash3388.flashlib.flashboard.FlashboardSendableType;
import edu.flash3388.flashlib.util.FlashUtil;

public class RemoteVision extends Sendable implements Vision{

	private boolean stopRemote = false, startRemote = false;
	private ArrayList<VisionProcessing> processing = new ArrayList<VisionProcessing>();
	private int currentProcessing = -1;
	private Analysis analysis;
	private boolean send = false, stopping = false, updateProcessing = false,
			sendProps = false;
	private int lastRec, recTimeout = 1000;
	
	public RemoteVision() {
		super(FlashboardSendableType.VISION);
	}

	private void startRemote(){
		startRemote = true;
	}
	private void stopRemote(){
		stopRemote = true;
	}
	
	@Override
	public void start(){
		startRemote();
		send = true;
		lastRec = FlashUtil.millisInt();
	}
	@Override
	public void stop(){
		stopRemote();
		stopping = true;
	}
	public boolean isRunning(){
		return send;
	}
	@Override
	public void addProcessing(VisionProcessing proc) {
		processing.add(proc);
		
		if(currentProcessing < 0)
			selectProcessing(0);
	}
	@Override
	public void selectProcessing(int index) {
		if(index < 0 || index >= processing.size())
			return;
		currentProcessing = index;
		updateProcessing = true;
	}
	@Override
	public VisionProcessing getProcessing(int index) {
		if(index < 0 || index >= processing.size())
			return null;
		return processing.get(index);
	}
	@Override
	public VisionProcessing getProcessing() {
		return getProcessing(currentProcessing);
	}
	@Override
	public int getProcessingCount() {
		return processing.size();
	}
	
	@Override
	public Analysis getAnalysis(){
		return analysis;
	}
	@Override
	public boolean hasNewAnalysis(){
		return analysis != null && FlashUtil.millisInt() - lastRec < recTimeout;
	}
	
	@Override
	public void newData(byte[] data) {
		if(!send) return;
		if(stopping){
			stopping = false;
			send = false;
			return;
		}
		if(data.length == 2){
			if (data[0] == 2)
				currentProcessing = data[1];
			return;
		}
		
		Analysis an = Analysis.fromBytes(data);
		
		if(an != null) {
			lastRec = FlashUtil.millisInt();
			analysis = an;
		}
	}
	@Override
	public byte[] dataForTransmition() {
		if(sendProps){
			VisionProcessing props = processing.get(processing.size()-1);
			sendProps = false;
			return props.toBytes();
		}
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
		
		if(!updateProcessing) return null;
		FlashUtil.getLog().log("Sending Vision parameters");
		updateProcessing = false;
		return new byte[]{2, (byte) currentProcessing};
	}
	@Override
	public boolean hasChanged() {
		return send && ((updateProcessing && processing != null) || stopRemote || startRemote || 
				sendProps);
	}
	@Override
	public void onConnection() {
		updateProcessing = true;
		if(isRunning()) startRemote = true;
		else stopRemote = true;
	}
	@Override
	public void onConnectionLost() {
	}

	@Override
	public void setNewAnalysisTimeout(int timeout) {
		recTimeout = timeout;
	}
	@Override
	public int getNewAnalysisTimeout() {
		return recTimeout;
	}
}
