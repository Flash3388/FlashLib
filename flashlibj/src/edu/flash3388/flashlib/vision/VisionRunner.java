package edu.flash3388.flashlib.vision;

import java.util.ArrayList;
import java.util.Arrays;

import edu.flash3388.flashlib.communications.Sendable;
import edu.flash3388.flashlib.flashboard.FlashboardSendableType;
import edu.flash3388.flashlib.util.FlashUtil;

/**
 * Provides a base for running vision processing. The processing is executed in a separate thread which receives new analysis
 * and stores them for use. It is possible to control this runner through a {@link RemoteVision} object.
 * <p>
 * When extending it is required to implement {@link #analyse()} which returns an {@link Analysis} object. Which 
 * library is used and how images are received does not influence the operation of the base.
 * <p>
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 * @see RemoteVision
 * @see Vision
 */
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
	
	private boolean newSelection = false, newProcessing = false;
	private Analysis[] lastAnalysis = new Analysis[2];
	private ArrayList<VisionProcessing> processing = new ArrayList<VisionProcessing>();
	private int currentProcessing = -1;
	private int analysisIndex = 0;
	private int recTimeout = 1000, lastRec;
	private boolean running = false;
	
	private Thread visionThread;
	private VisionRunnerTask runTask;
	
	/**
	 * Creates a base for running vision. When using {@link RemoteVision} it is required to use this constructor, otherwise
	 * the sendable id is not usable. Uses {@link FlashboardSendableType#VISION} as a sendable type.
	 * 
	 * @param name the name of the runner
	 * @param id the sendable id for the runner
	 */
	public VisionRunner(String name, int id) {
		super(name, id, FlashboardSendableType.VISION);
		
		runTask = new VisionRunnerTask(this);
		visionThread = new Thread(runTask, name);
	}
	/**
	 * Creates a base for running vision. When using the runner for local vision and not remote, this constructor is
	 * usable. For remote vision, see {@link #VisionRunner(String, int)}.
	 * 
	 * @param name the name of the runner
	 */
	public VisionRunner(String name){
		this(name, -10);
	}
	/**
	 * Creates a base for running vision. When using the runner for local vision and not remote, this constructor is
	 * usable. For remote vision, see {@link #VisionRunner(String, int)}. The name of the runner is "VisionRunner".
	 */
	public VisionRunner(){
		this("VisionRunner");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasNewAnalysis() {
		return lastAnalysis[analysisIndex] != null && FlashUtil.millisInt() - lastRec < recTimeout;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Analysis getAnalysis() {
		Analysis last = lastAnalysis[analysisIndex];
		lastAnalysis[analysisIndex] = null;
		analysisIndex ^= 1;
		return last;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isRunning() {
		return running;
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * Starts the vision thread if it is not running.
	 * </p>
	 */
	@Override
	public void start() {
		if(isRunning()) return;
		if(!visionThread.isAlive())
			visionThread.start();
		running = true;
		lastRec = FlashUtil.millisInt();
	}
	/**
	 * {@inheritDoc}
	 * <p>
	 * Pauses the vision thread, if it is running.
	 * </p>
	 */
	@Override
	public void stop() {
		running = false;
	}
	/**
	 * Closes the vision thread. Call to this method disables this runner object from further use.
	 */
	public void close(){
		stop();
		runTask.stop();
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * If connected to a remote vision controller, it is updated.
	 * </p>
	 */
	@Override
	public void addProcessing(VisionProcessing proc) {
		processing.add(proc);
		newProcessing = true;
		
		if(currentProcessing < 0)
			selectProcessing(0);
	}
	/**
	 * {@inheritDoc}
	 * <p>
	 * If connected to a remote vision controller, it is updated.
	 * </p>
	 */
	@Override
	public void selectProcessing(int index) {
		if(index < 0 || index >= processing.size())
			return;
		currentProcessing = index;
		newSelection = true;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public VisionProcessing getProcessing(int index) {
		if(index < 0 || index >= processing.size())
			return null;
		return processing.get(index);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getSelectedProcessingIndex() {
		return currentProcessing;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public VisionProcessing getProcessing() {
		return getProcessing(currentProcessing);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getProcessingCount() {
		return processing.size();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void newData(byte[] data) {
		if(data.length < 2) return;
		
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
		}else if(data[0] == RemoteVision.REMOTE_PROC_MODE){
			VisionProcessing proc = VisionProcessing.createFromBytes(Arrays.copyOfRange(data, 1, data.length));
			if(proc != null)
				addProcessing(proc);
		}
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public byte[] dataForTransmition() {
		if(newProcessing){
			newProcessing = false;
			return new byte[]{RemoteVision.REMOTE_PROC_MODE, (byte) (processing.size())};
		}
		if(newSelection){
			newSelection = false;
			return new byte[]{RemoteVision.REMOTE_SELECT_MODE, (byte) currentProcessing};
		}
		
		byte[] data = getAnalysis().transmit();
		byte[] send = new byte[data.length+1];
		send[0] = RemoteVision.REMOTE_ANALYSIS_MODE;
		System.arraycopy(data, 0, send, 1, data.length);
		return send;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasChanged() {
		return hasNewAnalysis() || newSelection || newProcessing;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onConnection() {
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onConnectionLost() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setNewAnalysisTimeout(int timeout) {
		recTimeout = timeout;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getNewAnalysisTimeout() {
		return recTimeout;
	}
	
	/**
	 * Analyzes available images and returns the resulting {@link Analysis} object.
	 * @return a new analysis
	 */
	protected abstract Analysis analyse();
}
