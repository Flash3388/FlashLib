package edu.flash3388.flashlib.cams;


public interface Camera {
	public static final int DEFAULT_WIDTH = 320;
	public static final int DEFAULT_HEIGHT = 240;
	public static final int DEFAULT_QUALITY = 30;
	
	byte[] getData();
	int getQuality();
	int getFPS();
	void setFPS(int fps);
	void setQuality(int quality);
}
