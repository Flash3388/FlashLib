package edu.flash3388.flashlib.vision;

import java.util.List;
import java.util.Map;

import edu.flash3388.flashlib.util.FlashUtil;

/**
 * Provides a base for creating {@link Analysis} for vision. Used to provide additional and
 * more sophisticated analysis of vision rather than the default {@link VisionSource#getResult()}.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.1
 */
public interface AnalysisCreator {

	/**
	 * Creates an {@link Analysis} object to represent data about the searched destination. 
	 * 
	 * @param source the object performing the vision.
	 * @param contours list of contours in the image that were not filtered out.
	 * @return an analysis from the performed vision.
	 */
	Analysis createAnalysis(VisionSource source, List<Contour> contours);
	
	/**
	 * Loads parameters for the creator. Used when loading the creator from a file or a byte stream.
	 * @param parameters a map of parameters where the key is the name.
	 */
	void parseParameters(Map<String, VisionParam> parameters);
	/**
	 * Gets the parameters of the creator. Used mostly when saving the creator into a file or a byte stream.
	 * @return an array of parameters
	 */
	VisionParam[] getParameters();
	
	/**
	 * Creates a new AnalysisCreator. Creation is done by attempting to instantiate a class with the given name.
	 * @param name the name of the creator class
	 * @param parameters parameters for the creator
	 * @return a new creator, or null if unable to create.
	 */
	public static AnalysisCreator create(String name, Map<String, VisionParam> parameters){
		Object obj = FlashUtil.createInstance(name);
		if(obj == null || !(obj instanceof AnalysisCreator))
			return null;
		AnalysisCreator creator = (AnalysisCreator)obj;
		creator.parseParameters(parameters);
		return creator;
	}
}
