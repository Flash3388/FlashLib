package edu.flash3388.flashlib.vision;

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
				if(!runner.isRunning() || runner.getParameters() == null){
					FlashUtil.delay(5);
					continue;
				}
				
				runner.lastAnalysis[1-runner.analysisIndex] = runner.analyse();
				runner.analysisIndex ^= 1;
				FlashUtil.delay(5);
			}
		}
		public void stop(){
			stop = true;
		}
	}
	
	private Analysis[] lastAnalysis = new Analysis[2];
	private ProcessingParam parameters;
	private int analysisIndex = 0;
	private boolean running = false, remoteParam = true;
	
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
		return lastAnalysis[analysisIndex] != null;
	}
	@Override
	public Analysis getAnalysis() {
		Analysis last = lastAnalysis[analysisIndex];
		lastAnalysis[analysisIndex] = null;
		return last;
	}
	@Override
	public boolean isRunning() {
		return running;
	}
	public boolean isLocalParameters(){
		return remoteParam;
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
	public void setParameters(ProcessingParam param) {
		parameters = param;
		FlashUtil.getLog().log("Parameters set");
	}
	@Override
	public ProcessingParam getParameters() {
		return parameters;
	}

	@Override
	public void setCameraOffsetAngle(double angle) {
	}
	@Override
	public double getCameraOffsetAngle() {
		return 0;
	}
	@Override
	public void setTargetHeight(double h) {
	}
	@Override
	public double getTargetHeight() {
		return 0;
	}
	@Override
	public void setTargetWidth(double w) {
	}
	@Override
	public double getTargetWidth() {
		return 0;
	}

	@Override
	public void newData(byte[] data) {
		if(data.length < 2) return;
		if(data.length == 2){
			boolean t = data[0] == 1, r = data[1] == 1;
			if(!t){
				FlashUtil.getLog().log("Remote Parameters: "+r);
				remoteParam = r;
			}else{
				FlashUtil.getLog().log("Starting: "+r);
				if(r) start();
				else stop();
			}
		}else if(remoteParam){
			ProcessingParam param = ProcessingParam.fromBytes(data, 2, data.length);
			if(param != null)
				setParameters(param);
		}
	}
	@Override
	public byte[] dataForTransmition() {
		return getAnalysis().transmit();
	}
	@Override
	public boolean hasChanged() {
		return hasNewAnalysis();
	}
	@Override
	public void onConnection() {
	}
	@Override
	public void onConnectionLost() {
	}

	protected abstract Analysis analyse();
}
