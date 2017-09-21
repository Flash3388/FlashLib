package edu.flash3388.flashlib.vision;

import java.util.ArrayList;
import java.util.Arrays;

import edu.flash3388.flashlib.communications.Sendable;
import edu.flash3388.flashlib.flashboard.FlashboardSendableType;
import edu.flash3388.flashlib.util.FlashUtil;
import edu.flash3388.flashlib.util.beans.observable.ObservableProperty;

/**
 * Provides a base for running vision processing. The processing is executed in a separate thread which receives new analysis
 * and stores them for use. It is possible to control this runner through a {@link RemoteVision} object.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 * @see RemoteVision
 * @see Vision
 */
public abstract class VisionRunner extends Sendable implements Vision{
	
	private boolean newSelection = false, newProcessing = false;
	
	private ArrayList<VisionProcessing> processing = new ArrayList<VisionProcessing>();
	private VisionSource visionSource;
	
	private int currentProcessing = -1;
	private int recTimeout = 1000, lastRec;
	private boolean running = false, considerNew = true;
	
	/**
	 * Creates a base for running vision. 
	 * Uses {@link FlashboardSendableType#VISION} as a sendable type.
	 * 
	 * @param name the name of the runner
	 */
	public VisionRunner(String name) {
		super(name, FlashboardSendableType.VISION);
		
		
	}
	/**
	 * Creates a base for running vision. When using the runner for local vision and not remote, this constructor is
	 * usable. For remote vision, see {@link #VisionRunner(String)}. The name of the runner is "VisionRunner".
	 */
	public VisionRunner(){
		this("VisionRunner");
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasNewAnalysis() {
		return hasAnalysis() && considerNew && FlashUtil.millisInt() - lastRec < recTimeout;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setNewAnalysisAsOld(){
		considerNew = false;
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
	 */
	@Override
	public void start() {
		if(isRunning()) return;
		running = true;
		lastRec = FlashUtil.millisInt();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void stop() {
		running = false;
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
			selectProcessing(data[1]);
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
		
		//considerNew = true;
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
	 * Sets the {@link VisionSource} to be used by this runner to perform vision operations
	 * @param source the vision source
	 */
	public void setVisionSource(VisionSource source){
		this.visionSource = source;
	}
	/**
	 * Gets the {@link VisionSource} used by this runner to perform vision
	 * operation.
	 * @return the vision object, or null if one does not exist
	 */
	public VisionSource getVisionSource(){
		return visionSource;
	}
	
	/**
	 * Analyzes available images and saves the resulting {@link Analysis} object.
	 * 
	 * @return true if an analysis was saved, false otherwise
	 */
	public boolean analyze(){
		if(!isRunning() || getProcessing() == null || visionSource == null)
			return false;
		
		Object frame = getNextFrame();
		
		if(frame != null){
			visionSource.setFrame(frame);
			
			Analysis an = getProcessing().processAndGet(visionSource);
			if(an != null){
				ImagePipeline pipe = visionSource.getImagePipeline();
				if(pipe != null){
					visionSource.drawAnalysisResult(frame, an);
					pipe.newImage(frame, ImagePipeline.TYPE_POST_PROCESS);
				}
				
				newAnalysis(an);
				considerNew = true;
				lastRec = FlashUtil.millisInt();
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Sets the value of {@link #frameProperty()}.
	 * 
	 * @param frame the frame
	 */
	public void setFrame(Object frame){
		frameProperty().setValue(frame);
	}
	/**
	 * The property used to hold the newest frame to be received.
	 * The frame type depends on the vision library used.
	 * This is an {@link ObservableProperty} and can be bound.
	 * 
	 * @return the frame property
	 */
	public abstract ObservableProperty<Object> frameProperty();
	
	protected abstract Object getNextFrame();
	protected abstract void newAnalysis(Analysis an);
}
