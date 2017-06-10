package edu.flash3388.flashlib.vision;

import java.util.ArrayList;
import java.util.Arrays;

import edu.flash3388.flashlib.communications.Sendable;
import edu.flash3388.flashlib.flashboard.FlashboardSendableType;
import edu.flash3388.flashlib.util.FlashUtil;

public abstract class VisionRunner extends Sendable implements Vision{

	private static class VisionRunnerTask implements Runnable{
		private boolean stop = false;
		private VisionRunner runner;
		
		public VisionRunnerTask(VisionRunner v){
			this.runner = v;
		}
		
		@Override
		public void run() {
			while(!stop){
				if(!runner.isRunning() || runner.getProcessing() == null){
					FlashUtil.delay(5);
					continue;
				}
				
				Analysis an = runner.analyse();
				runner.lastAnalysis[1-runner.analysisIndex] = an;
				if(an != null)
					runner.lastRec = FlashUtil.millisInt();
				
				FlashUtil.delay(5);
			}
		}
		public void stop(){
			stop = true;
		}
	}
	
	private boolean newSelection = false, newProcessing = false, newRunMode = false;
	private Analysis[] lastAnalysis = new Analysis[2];
	private ArrayList<VisionProcessing> processing = new ArrayList<VisionProcessing>();
	private int currentProcessing = -1;
	private int analysisIndex = 0;
	private int recTimeout = 1000, lastRec;
	private boolean running = false;
	
	private Thread visionThread;
	private VisionRunnerTask runTask;
	
	public VisionRunner(String name, int id) {
		super(name, id, FlashboardSendableType.VISION);
		
		runTask = new VisionRunnerTask(this);
		visionThread = new Thread(runTask, name);
	}
	public VisionRunner(String name){
		this(name, -1);
	}
	public VisionRunner(){
		this("VisionRunner");
	}

	@Override
	public boolean hasNewAnalysis() {
		return lastAnalysis[analysisIndex] != null && FlashUtil.millisInt() - lastRec < recTimeout;
	}
	@Override
	public Analysis getAnalysis() {
		Analysis last = lastAnalysis[analysisIndex];
		lastAnalysis[analysisIndex] = null;
		analysisIndex ^= 1;
		return last;
	}
	@Override
	public boolean isRunning() {
		return running;
	}
	
	@Override
	public void start() {
		if(!visionThread.isAlive())
			visionThread.start();
		running = true;
		lastRec = FlashUtil.millisInt();
	}
	@Override
	public void stop() {
		running = false;
	}
	public void close(){
		stop();
		runTask.stop();
	}
	
	@Override
	public void addProcessing(VisionProcessing proc) {
		processing.add(proc);
		newProcessing = true;
		
		if(currentProcessing < 0)
			selectProcessing(0);
	}
	@Override
	public void selectProcessing(int index) {
		if(index < 0 || index >= processing.size())
			return;
		currentProcessing = index;
		newSelection = true;
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
		return getProcessing(currentProcessing);
	}
	@Override
	public int getProcessingCount() {
		return processing.size();
	}

	@Override
	public void newData(byte[] data) {
		if(data.length < 2) return;
		System.out.println("data: "+data.length);
		
		if(data[0] == RemoteVision.REMOTE_RUN_MODE){
			if(data[1] == RemoteVision.REMOTE_START){
				start();
				FlashUtil.getLog().log("Starting vision");
			}
			else if(data[1] == RemoteVision.REMOTE_STOP){
				stop();
				FlashUtil.getLog().log("Stopping vision");
			}
		}else if(data[0] == RemoteVision.REMOTE_SELECT_MODE){
			currentProcessing = data[1];
			System.out.println("Current changed");
		}else if(data[0] == RemoteVision.REMOTE_PROC_MODE){
			VisionProcessing proc = VisionProcessing.createFromBytes(Arrays.copyOfRange(data, 1, data.length));
			if(proc != null)
				addProcessing(proc);
		}
	}
	@Override
	public byte[] dataForTransmition() {
		if(newProcessing){
			System.out.println("Sending new proc data");
			newProcessing = false;
			return new byte[]{RemoteVision.REMOTE_PROC_MODE, (byte) (processing.size())};
		}
		if(newSelection){
			newSelection = false;
			System.out.println("Updating selection");
			return new byte[]{RemoteVision.REMOTE_SELECT_MODE, (byte) currentProcessing};
		}
		
		byte[] data = getAnalysis().transmit();
		byte[] send = Arrays.copyOf(data, data.length+1);
		send[0] = RemoteVision.REMOTE_ANALYSIS_MODE;
		return send;
	}
	@Override
	public boolean hasChanged() {
		return hasNewAnalysis() || newSelection || newProcessing;
	}
	@Override
	public void onConnection() {
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
	
	protected abstract Analysis analyse();
}
