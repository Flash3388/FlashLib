package edu.flash3388.flashlib.vision;

/**
 * An object capable of receiving images and handling them
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
@FunctionalInterface
public interface ImagePipeline {
	
	public static final byte TYPE_NONE = 0;
	public static final byte TYPE_THRESHOLD = 1;
	public static final byte TYPE_POST_PROCESS = 2;
	public static final byte TYPE_PRE_PROCESS = 3;
	
	/**
	 * Transfer a new image with a given handling type. The handling type would indicate
	 * the object receiving the image what to do with it. If there is no special case for the image,
	 * pas {@value #TYPE_NONE}.
	 * 
	 * @param frame the frame object
	 * @param type the handling type
	 */
	void newImage(Object frame, byte type);
	
	/**
	 * Transfer a new image.
	 * <p>
	 * The default implementation calls {@link #newImage(Object, byte)} and passes 
	 * the {@value #TYPE_NONE} handling type.
	 * 
	 * @param frame the frame object
	 */
	default void newImage(Object frame){
		newImage(frame, TYPE_NONE);
	}
}
