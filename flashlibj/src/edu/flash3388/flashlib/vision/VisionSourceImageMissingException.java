package edu.flash3388.flashlib.vision;

public class VisionSourceImageMissingException extends RuntimeException{

	private static final long serialVersionUID = 1L;
	
	public VisionSourceImageMissingException(){
		super("Vision source requires an image to process for results");
	}
}
