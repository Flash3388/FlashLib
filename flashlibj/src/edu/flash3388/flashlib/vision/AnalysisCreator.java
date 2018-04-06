package edu.flash3388.flashlib.vision;

import java.io.Serializable;

/**
 * Provides a base for creating {@link Analysis} for vision. Used to provide additional and
 * more sophisticated analysis of vision rather than the default {@link VisionSource#getResult()}.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.1
 */
public interface AnalysisCreator extends Serializable {

	/**
	 * Creates an {@link Analysis} object to represent data about the searched destination. 
	 * 
	 * @param source the object performing the vision.
	 * @return an analysis from the performed vision.
	 */
	Analysis createAnalysis(VisionSource source);
}
