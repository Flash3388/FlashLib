package edu.flash3388.flashlib.vision;

import java.util.ArrayList;

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
				
				runner.lastAnalysis[1-runner.analysisIndex] = runner.analyse();
				FlashUtil.delay(5);
			}
		}
		public void stop(){
			stop = true;
		}
	}
	
	private boolean newSelection = false;
	private Analysis[] lastAnalysis = new Analysis[2];
	private ArrayList<VisionProcessing> processing = new ArrayList<VisionProcessing>();
	private int currentProcessing = -1;
	private int analysisIndex = 0;
	private boolean running = false;
	
	private Thread visionThread;
	private VisionRunnerTask runTask;
	
	public VisionRunner(String name, int id) {
		super(name, id, FlashboardSendableType.VISION);
		FlashUtil.validateInit();
		
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
		return lastAnalysis[analysisIndex] != null;
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
		if(data.length == 2){
			if(data[0] == 1){
				FlashUtil.getLog().log("Starting: "+(data[1] == 1));
				if(data[1] == 1) start();
				else stop();
			}else if(data[0] == 2){
				currentProcessing = data[1];
			}
			
			return;
		}
		VisionProcessing proc = VisionProcessing.createFromBytes(data);
		if(proc != null)
			addProcessing(proc);
	}
	@Override
	public byte[] dataForTransmition() {
		if(newSelection){
			newSelection = false;
			return new byte[]{2, (byte) currentProcessing};
		}
		return getAnalysis().transmit();
	}
	@Override
	public boolean hasChanged() {
		return hasNewAnalysis() || newSelection;
	}
	@Override
	public void onConnection() {
	}
	@Override
	public void onConnectionLost() {
	}

	protected abstract Analysis analyse();
}
