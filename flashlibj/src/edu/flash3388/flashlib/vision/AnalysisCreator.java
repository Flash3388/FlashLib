package edu.flash3388.flashlib.vision;

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
	 * @return an analysis from the performed vision.
	 */
	Analysis createAnalysis(VisionSource source);
	
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
		
		//setting parameters
		VisionParam.setParameters(creator, parameters);
		
		return creator;
	}
}
