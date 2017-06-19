package edu.flash3388.flashlib.vision;

import edu.flash3388.flashlib.util.FlashUtil;

/**
 * Represents a post vision analysis. Contains data about the located contour.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public class Analysis {
	
	/**
	 * The horizontal distance between the located contour and the center of the frame.
	 */
	public int horizontalDistance;
	/**
	 * The vertical distance between the located contour and the center of the frame.
	 */
	public int verticalDistance;
	/**
	 * The x coordinate of the center of the contour.
	 */
	public double centerPointX;
	/**
	 * The y coordinate of the center of the contour.
	 */
	public double centerPointY;
	/**
	 * The distance of the target.
	 */
	public double targetDistance;
	/**
	 * The offset horizontal angle between the center of the frame and the center of the contour.
	 */
	public double offsetAngle;
	/**
	 * Ratio between pixels and cm in the frame.
	 */
	public double pixelsToCmRatio;
	
	/**
	 * Prints data about this analysis to {@link java.lang.System#out}
	 */
	public void print(){
		String all = "H_dis: "+horizontalDistance+" V_dis: "+verticalDistance+" Dis: "+targetDistance+" Angle:"+offsetAngle;
		System.out.println(all);
	}
	
	/**
	 * Creates a byte array and saves this analysis data into it.
	 * @return a byte array with data about this analysis
	 */
	public byte[] transmit(){
		byte[] bytes = new byte[32], x = FlashUtil.toByteArray((int)centerPointX), 
				y = FlashUtil.toByteArray((int)centerPointY),
				pixelsh = FlashUtil.toByteArray(horizontalDistance),
				pixelsv = FlashUtil.toByteArray(verticalDistance),
				dis = FlashUtil.toByteArray(targetDistance),
				off = FlashUtil.toByteArray(offsetAngle);
		int pos = 0;
		System.arraycopy(x, 0, bytes, pos, 4);
		pos += 4;
		System.arraycopy(y, 0, bytes, pos, 4);
		pos += 4;
		System.arraycopy(pixelsh, 0, bytes, pos, 4);
		pos += 4;
		System.arraycopy(pixelsv, 0, bytes, pos, 4);
		pos += 4;
		System.arraycopy(dis, 0, bytes, pos, 8);
		pos += 8;
		System.arraycopy(off, 0, bytes, pos, 8);
		return bytes;
	}
	
	/**
	 * Creates an analysis from a byte array. 
	 * @param bytes byte array with analysis data.
	 * @return a new analysis object
	 */
	public static Analysis fromBytes(byte[] bytes){
		if(bytes.length < 32) return null;
		Analysis an = new Analysis();
		int pos = 0;
		an.centerPointX = FlashUtil.toInt(bytes, pos); pos+=4;
		an.centerPointY = FlashUtil.toInt(bytes, pos); pos+=4;
		an.horizontalDistance = FlashUtil.toInt(bytes, pos); pos+=4;
		an.verticalDistance = FlashUtil.toInt(bytes, pos); pos+=4;
		an.targetDistance = FlashUtil.toDouble(bytes, pos); pos+=8;
		an.offsetAngle = FlashUtil.toDouble(bytes, pos);
		
		return an;
	}
}
