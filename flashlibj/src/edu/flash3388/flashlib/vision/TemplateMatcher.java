package edu.flash3388.flashlib.vision;

/**
 * An interface for class providing algorithms for template matching.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.1
 */
public interface TemplateMatcher {
	
	/**
	 * Performs the template matching algorithm between stored templates and the given frame using a given image scale
	 * factor. Implementation differs between libraries.
	 * 
	 * @param frame the frame to match against
	 * @param scaleFactor the frame scaling factor
	 * @return template matching result
	 */
	MatchResult match(Object frame, double scaleFactor);
	/**
	 * Performs the template matching algorithm between the given templates and the given frame using a given image scale
	 * factor. Implementation differs between libraries.
	 * 
	 * @param frame the frame to match against
	 * @param method the template matching method
	 * @param scaleFactor the frame scaling factor
	 * @param templates the templates
	 * @return template matching result
	 */
	MatchResult match(Object frame, int method, double scaleFactor, Object... templates);
}
