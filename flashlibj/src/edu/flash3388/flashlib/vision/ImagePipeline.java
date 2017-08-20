package edu.flash3388.flashlib.vision;

/**
 * Used to transfer image object 
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
@FunctionalInterface
public interface ImagePipeline {
	
	public static final byte TYPE_THRESHOLD = 1, TYPE_POST_PROCESS = 2, TYPE_PRE_PROCESS = 3;
	
	void newImage(Object frame, byte type);
}
