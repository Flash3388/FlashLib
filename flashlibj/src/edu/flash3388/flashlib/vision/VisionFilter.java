package edu.flash3388.flashlib.vision;

import java.io.Serializable;

/**
 * A base for filters used for vision processing. Each filter is used to filter data out of an image during processing. 
 * <p>
 * Most filters contain parameters which define certain aspects of the filter. 
 * </p>
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 * @see VisionProcessing
 * @see FilterCreator
 * @see VisionParam
 */
public interface VisionFilter extends Serializable {
	
	/**
	 * Processes data in an image from the vision source and filters out non matching data.
	 * @param source the source of the vision
	 */
	void process(VisionSource source);
}
