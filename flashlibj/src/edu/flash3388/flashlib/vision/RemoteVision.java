package edu.flash3388.flashlib.vision;

import java.util.ArrayList;
import java.util.Arrays;

import edu.flash3388.flashlib.communications.Sendable;
import edu.flash3388.flashlib.flashboard.FlashboardSendableType;
import edu.flash3388.flashlib.util.FlashUtil;

public class RemoteVision extends Sendable implements Vision{

	public static final byte REMOTE_STOP = 0xe;
	public static final byte REMOTE_START = 0x5;
	
	public static final byte REMOTE_RUN_MODE = 0x10;
	public static final byte REMOTE_SELECT_MODE = 0x1;
	public static final byte REMOTE_ANALYSIS_MODE = 0xf;
	public static final byte REMOTE_PROC_MODE = 0x5;
	
	private boolean stopRemote = false, startRemote = false;
	private ArrayList<VisionProcessing> processing = new ArrayList<VisionProcessing>();
	private int currentProcessing = -1, sendProc = 0, procCount = 0;
	private Analysis analysis;
	private boolean running = false, updateProcessing = false,
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
		running = true;
		lastRec = FlashUtil.millisInt();
	}
	@Override
	public void stop(){
		stopRemote();
	}
	public boolean isRunning(){
		return running;
	}
	@Override
	public void addProcessing(VisionProcessing proc) {
		processing.add(proc);
		sendProps = true;
		if(currentProcessing < 0)
			selectProcessing(0);
	}
	@Override
	public void selectProcessing(int index) {
		if(index < 0 || index >= procCount)
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
	public int getSelectedProcessingIndex() {
		return currentProcessing;
	}
	@Override
	public VisionProcessing getProcessing() {
		return null;//getProcessing(currentProcessing);
	}
	@Override
	public int getProcessingCount() {
		return procCount;
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
		if(data.length < 2) return;
		
		if(data[0] == REMOTE_RUN_MODE){
			if(data[1] == REMOTE_START){
				running = true;
			}else if(data[1] == REMOTE_STOP){
				running = false;
			}
		}
		else if(data[0] == REMOTE_SELECT_MODE){
			currentProcessing = data[1];
		}
		else if(data[0] == REMOTE_PROC_MODE){
			procCount = data[1];
		}
		else if(data[0] == REMOTE_ANALYSIS_MODE){
			Analysis an = Analysis.fromBytes(Arrays.copyOfRange(data, 1, data.length));
			
			if(an != null) {
				lastRec = FlashUtil.millisInt();
				analysis = an;
			}
		}
	}
	@Override
	public byte[] dataForTransmition() {
		if(sendProps){
			byte[] data = processing.get(sendProc).toBytes();
			byte[] send = new byte[data.length+1];
			send[0] = REMOTE_PROC_MODE;
			System.arraycopy(data, 0, send, 1, data.length);
			if((++sendProc) >= processing.size())
				sendProps = false;
			return send;
		}
		if(stopRemote){
			stopRemote = false;
			FlashUtil.getLog().log("Vision: Stop");
			return new byte[]{REMOTE_RUN_MODE,REMOTE_STOP};
		}
		if(startRemote){
			startRemote = false;
			FlashUtil.getLog().log("Vision: Start");
			return new byte[]{REMOTE_RUN_MODE,REMOTE_START};
		}
		
		if(!updateProcessing) return null;
		updateProcessing = false;
		return new byte[]{REMOTE_SELECT_MODE, (byte) currentProcessing};
	}
	@Override
	public boolean hasChanged() {
		return updateProcessing || stopRemote || startRemote || 
				sendProps;
	}
	@Override
	public void onConnection() {
		updateProcessing = true;
		if(isRunning()) startRemote = true;
		else stopRemote = true;
		sendProc = 0;
	}
	@Override
	public void onConnectionLost() {
		procCount = 0;
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
