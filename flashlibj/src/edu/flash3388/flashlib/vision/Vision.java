package edu.flash3388.flashlib.vision;

/**
 * 
 * Vision provides an interface for vision processing control. Classes implementing this interface will be
 * required to allow users to control:
 * <ul>
 * 	<li>What {@link VisionProcessing} object is to be used for vision</li>
 * 	<li>Whether to run vision or not</li>
 * 	<li>How much time has to pass for an {@link Analysis} object to be considered out-of-data</li>
 * </ul>
 * <p>
 * Users can also retrieve the latest {@link Analysis} object, add new {@link VisionProcessing} objects, etc.
 * </p>
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 * @see RemoteVision
 * @see VisionRunner
 */
public interface Vision {
	/**
	 * Gets whether or not a new analysis is available. 
	 * <p>
	 * This is usually determined by checking if the classes {@link Analysis} object is not null, and
	 * the time since that object was received does not exceed the analysis timeout defined by {@link #setNewAnalysisTimeout(int)}.
	 * </p>
	 * @return true if a new analysis is available
	 */
	boolean hasNewAnalysis();
	/**
	 * Gets whether or not an analysis is available. Unlike {@link #hasNewAnalysis()},
	 * this method does not care how long has passed since the analysis was received.
	 * @return true if an analysis is available
	 */
	boolean hasAnalysis();
	/**
	 * Gets the latest vision analysis. 
	 * <p>
	 * It is usually recommended to make sure the analysis is up to date by calling {@link #hasNewAnalysis()}.
	 * As a safety precaution, if the analysis object is not up-to-date it should not be used.
	 * </p>
	 * @return the latest vision analysis, or null if non exist
	 * @see Analysis
	 */
	Analysis getAnalysis();
	/**
	 * Sets the timeout for vision analysis to be considered new. If the timeout has passed, the analysis is
	 * not up-to-date and should not be used. One second is usually the recommended timeout for analysis.
	 * @param timeout the timeout for new vision analysis
	 * @see #hasNewAnalysis()
	 */
	void setNewAnalysisTimeout(int timeout);
	/**
	 * Gets the timeout for vision analysis to be considered new.If the timeout has passed, the analysis is
	 * not up-to-date and should not be used. One second is usually the recommended timeout for analysis.
	 * @return the timeout for new vision analysis
	 * @see #hasNewAnalysis()
	 */
	int getNewAnalysisTimeout();
	
	/**
	 * Gets whether or not the vision process is running. If not, than no new analysis will be received.
	 * 
	 * @return true if the vision is running, false otherwise
	 * @see #start()
	 * @see #stop()
	 */
	boolean isRunning();
	/**
	 * Starts the vision process. New analysis will be received for use. Uses the selected {@link VisionProcessing}
	 * to analyze images.
	 * @see #stop()
	 */
	void start();
	/**
	 * Stops the vision process. No new analysis will be received for use.
	 * @see #start()
	 */
	void stop();
	
	/**
	 * Adds a new {@link VisionProcessing} object to the list of possible processing objects to be used
	 * for vision analysis. If no other processing object is available, this one will be selected for use automatically.
	 * 
	 * @param proc a new processing object to add
	 * @see VisionProcessing
	 */
	void addProcessing(VisionProcessing proc);
	/**
	 * Selects the {@link VisionProcessing} object to be used by index. The index is determined by order of addition
	 * to this instance, from 0.
	 * 
	 * @param index the index of the processing object to select
	 * @see VisionProcessing
	 */
	void selectProcessing(int index);
	/**
	 * Gets the amount of processing objects available for selection.
	 * @return the amount of processing objects available.
	 */
	int getProcessingCount();
	/**
	 * Gets the currently selected index of  processing object to use for vision analysis.
	 * @return the selected index
	 * @see #selectProcessing(int)
	 */
	int getSelectedProcessingIndex();
	/**
	 * Gets the processing object at the given index. If the index is bigger than the amount of objects stored, null
	 * will be returned.
	 * @param index the index of the processing object
	 * @return the processing object stored at this index, null if none exist
	 */
	VisionProcessing getProcessing(int index);
	/**
	 * Gets the currently selected processing object. 
	 * @return the currently selected processing object, or null if none are selected
	 * @see #selectProcessing(int)
	 */
	VisionProcessing getProcessing();
}
