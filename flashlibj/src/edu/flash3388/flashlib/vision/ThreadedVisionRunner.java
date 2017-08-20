package edu.flash3388.flashlib.vision;

import edu.flash3388.flashlib.util.FlashUtil;
import edu.flash3388.flashlib.util.beans.observable.ObservableProperty;
import edu.flash3388.flashlib.util.beans.observable.ObservablePropertyBase;

/**
 * Represents a thread-based implementations of {@link VisionRunner}, where the vision
 * is executed in a separate thread. The thread is automatically started upon a call to
 * {@link #start()}, and stopped only when {@link #close()} is called. While {@link #isRunning()}
 * returns false, the thread does nothing.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.1
 */
public class ThreadedVisionRunner extends VisionRunner{

	private static class VisionRunnerTask implements Runnable{
		private boolean stop = false;
		private VisionRunner runner;
		
		public VisionRunnerTask(VisionRunner v){
			this.runner = v;
		}
		
		@Override
		public void run() {
			while(!stop){
				if(!runner.isRunning()){
					FlashUtil.delay(100);
					continue;
				}
				runner.analyze();
				FlashUtil.delay(10);
			}
		}
		public void stop(){
			stop = true;
		}
	}
	private static class ArrayIndexObservableProperty extends ObservablePropertyBase<Object>{

		private Object[] array;
		private int index;
		
		public ArrayIndexObservableProperty(Object[] arr){
			this.array = arr;
			index = 0;
		}
		
		public void setIndex(int index){
			this.index = index;
		}
		
		@Override
		protected void setInternal(Object val) {
			array[index] = val;
		}
		@Override
		protected Object getInternal() {
			return array[index];
		}
	}
	
	private Analysis[] lastAnalysis = new Analysis[2];
	private Object[] frames = new Object[2];
	
	private int analysisIndex = 0;
	private int frameIndex = 0;
	
	private ArrayIndexObservableProperty newFrame = new ArrayIndexObservableProperty(frames);
	
	private Thread visionThread;
	private VisionRunnerTask runTask;
	
	public ThreadedVisionRunner(String name, int id) {
		super(name, id);
		
		runTask = new VisionRunnerTask(this);
		visionThread = new Thread(runTask, name);
	}
	public ThreadedVisionRunner(String name){
		this(name, -10);
	}
	public ThreadedVisionRunner() {
		this("ThreadedVisionRunner");
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * Starts the vision thread if it is not running.
	 * </p>
	 */
	@Override
	public void start() {
		if(!isRunning()){
			if(!visionThread.isAlive())
				visionThread.start();
			super.start();
		}
	}
	/**
	 * Closes the vision thread. Call to this method disables this runner object from further use.
	 */
	public void close(){
		stop();
		runTask.stop();
		
		try {
			visionThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
			Thread.currentThread().interrupt();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasAnalysis() {
		return lastAnalysis[analysisIndex] != null;
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
	public ObservableProperty<Object> frameProperty(){
		return newFrame;
	}
	
	@Override
	protected Object getNextFrame() {
		Object frame = frames[1 - frameIndex];
		frames[1 - frameIndex] = null;
		frameIndex ^= 1;
		newFrame.setIndex(frameIndex);
		return frame;
	}
	@Override
	protected void newAnalysis(Analysis an) {
		lastAnalysis[1 - analysisIndex] = an;
	}
}
