package edu.flash3388.flashlib.vision;

import edu.flash3388.flashlib.util.FlashUtil;

public class Analysis {
	
	public int horizontalDistance, verticalDistance;
	public double centerPointX, centerPointY;
	public double targetDistance;
	public double offsetAngle;
	public double pixelsToCmRatio;
	
	public void print(){
		String all = "H_dis: "+horizontalDistance+" V_dis: "+verticalDistance+" Dis: "+targetDistance+" Angle:"+offsetAngle;
		FlashUtil.getLog().log(all);
	}
	public byte[] transmit(){
		byte[] bytes = new byte[8 * 4], x = FlashUtil.toByteArray((int)centerPointX), 
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
