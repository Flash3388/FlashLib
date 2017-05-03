package edu.flash3388.flashlib.vision;

import java.io.IOException;

import edu.flash3388.flashlib.io.FileReader;
import edu.flash3388.flashlib.io.FileWriter;
import edu.flash3388.flashlib.util.FlashUtil;

public class ProcessingParam {
	public final static int MIN_SCORE = 75;
	
	public static enum DetectingMode{
		Highest(0), Largest(1), Both(2), Rect(3), Ratio(4);
		
		public final int value;
		private DetectingMode(int val){
			value = val;
		}
	}
	
	public boolean hsv, morphOps, targetBoiler;
	public Range hue_red;
	public Range sat_green;
	public Range val_blue;
	public double objHeight;
	public double objWidth;
	public double minScore = 30;
	public int blur;
	public DetectingMode mode;
	
	public ProcessingParam(boolean hsv, boolean morph, int blur, Range r1, Range r2, Range r3, double h, double w, double minScore,DetectingMode M){
		this.hue_red = r1;
		this.sat_green = r2;
		this.val_blue = r3;
		this.hsv = hsv;
		this.morphOps = morph;
		this.objHeight = h;
		this.objWidth = w;
		this.minScore = minScore;
		this.mode = M;
		this.blur = blur;
	}
	
	public ProcessingParam(boolean hsv, boolean morph, Range r1, Range r2, Range r3, double h, double w, DetectingMode M){
		this(hsv, morph, 7, r1, r2, r3, h, w, MIN_SCORE,M);
	}
	
	public ProcessingParam(Range r1, Range r2, Range r3, double h, double w, DetectingMode M){
		this(false, false, r1, r2, r3, h, w,M);
	}
	public ProcessingParam(int min1, int max1, int min2, int max2, int min3, int max3, double h, double w, DetectingMode M){
		this(false, false, new Range(min1, max1), new Range(min2, max2), new Range(min3, max3), h, w,M);
	}
	public ProcessingParam(){}
	
	public ProcessingParam copy(){
		ProcessingParam p = new ProcessingParam();
		p.hsv = hsv;
		p.minScore = minScore;
		p.morphOps = morphOps;
		p.mode = mode;
		p.hue_red = new Range(hue_red.start, hue_red.end);
		p.sat_green = new Range(sat_green.start, sat_green.end);
		p.val_blue = new Range(val_blue.start, val_blue.end);
		p.objHeight = objHeight;
		p.objWidth = objWidth;
		p.blur = blur;
		return p;
	}
	public boolean equals(ProcessingParam p){
		return p != null && blur == p.blur && hsv == p.hsv && morphOps == p.morphOps && minScore == p.minScore && objHeight == p.objHeight && objWidth == p.objWidth
				&& mode.value == p.mode.value && hue_red.equals(p.hue_red) && sat_green.equals(p.sat_green) && val_blue.equals(p.val_blue);
	}
	public byte[] toBytes(){
		byte[] bytes = new byte[59];
		
		bytes[0] = (byte) (hsv? 1 : 0);
		bytes[1] = (byte) (morphOps? 1 : 0);
		bytes[2] = (byte) (targetBoiler? 1 : 0);
		
		int pos = 3; Range[] ranges = {hue_red, sat_green, val_blue};
		for(int i = 0; i < 3; i++){
			System.arraycopy(FlashUtil.toByteArray(ranges[i].start), 0, bytes, pos, 4);
			pos += 4;
			System.arraycopy(FlashUtil.toByteArray(ranges[i].end), 0, bytes, pos, 4);
			pos += 4;
		}
		
		System.arraycopy(FlashUtil.toByteArray(objHeight), 0, bytes, pos, 8);
		pos += 8;
		System.arraycopy(FlashUtil.toByteArray(objWidth), 0, bytes, pos, 8);
		pos += 8;
		System.arraycopy(FlashUtil.toByteArray(minScore), 0, bytes, pos, 8);
		pos += 8;
		System.arraycopy(FlashUtil.toByteArray(mode.value), 0, bytes, pos, 4);
		pos += 4;
		System.arraycopy(FlashUtil.toByteArray(blur), 0, bytes, pos, 4);
		
		return bytes;
	}
	public void saveFile(String filepath){
		FileWriter writer = new FileWriter(filepath);
		String r1 = hue_red.start + "," + hue_red.end,
			   r2 = sat_green.start + "," + sat_green.end,
			   r3 = val_blue.start + "," + val_blue.end,
			   hsv = String.valueOf(this.hsv? 1 : 0),
			   targetBoiler = String.valueOf(this.targetBoiler? 1 : 0),
			   morph = String.valueOf(morphOps? 1 : 0),
			   width = String.valueOf(this.objWidth),
			   height = String.valueOf(this.objHeight),
			   mode = String.valueOf(this.mode.value);
		writer.writeConstant("r1", r1);
		writer.writeConstant("r2", r2);
		writer.writeConstant("r3", r3);
		writer.writeConstant("boiler", targetBoiler);
		writer.writeConstant("hsv", hsv);
		writer.writeConstant("morphOps", morph);
		writer.writeConstant("width", width);
		writer.writeConstant("height", height);
		writer.writeConstant("mode", mode);
	}
	public static ProcessingParam loadFromFile(String filepath){
		FileReader reader = null;
		try {
			reader = new FileReader(filepath);
			ProcessingParam param = new ProcessingParam();
			String r1 = reader.getConstant("r1"), 
				   r2 = reader.getConstant("r2"),
				   r3 = reader.getConstant("r3"),
				   boiler = reader.getConstant("boiler"),
				   hsv = reader.getConstant("hsv"),
				   morphOps = reader.getConstant("morphOps"),
				   objHeight = reader.getConstant("height"),
				   objWidth = reader.getConstant("width"),
				   mode = reader.getConstant("mode");
			
			String[] splits = r1.split(",");
			param.hue_red = new Range(Integer.parseInt(splits[0]), Integer.parseInt(splits[1]));
			splits = r2.split(",");
			param.sat_green = new Range(Integer.parseInt(splits[0]), Integer.parseInt(splits[1]));
			splits = r3.split(",");
			param.val_blue = new Range(Integer.parseInt(splits[0]), Integer.parseInt(splits[1]));
			
			param.targetBoiler = Integer.parseInt(boiler) == 1;
			param.hsv = Integer.parseInt(hsv) == 1;
			param.morphOps = Integer.parseInt(morphOps) == 1;
			
			param.objHeight = Double.parseDouble(objHeight);
			param.objWidth = Double.parseDouble(objWidth);
			
			param.mode = DetectingMode.values()[Integer.parseInt(mode)];
			
			return param;
		} catch (NullPointerException | IOException | NumberFormatException | IndexOutOfBoundsException e) {
			return null;
		} 
	}
	public static ProcessingParam fromBytes(byte[] bytes, int start, int length){
		if(length - start < 59) return null;
		
		ProcessingParam p = new ProcessingParam();
		
		p.hsv = bytes[start] == 1;
		p.morphOps = bytes[start + 1] == 1;
		p.targetBoiler = bytes[start + 2] == 1;
		
		int pos = start + 3; int[] ranges = new int[6];
		for(int i = 0; i < 6; i++){
			ranges[i] = FlashUtil.toInt(bytes, pos);
			pos += 4;
		}
		p.hue_red = new Range(ranges[0], ranges[1]);
		p.sat_green = new Range(ranges[2], ranges[3]);
		p.val_blue = new Range(ranges[4], ranges[5]);
		
		p.objHeight = FlashUtil.toDouble(bytes, pos);
		pos += 8;
		p.objWidth = FlashUtil.toDouble(bytes, pos);
		pos += 8;
		p.minScore = FlashUtil.toDouble(bytes, pos);
		pos += 8;
		p.mode = DetectingMode.values()[FlashUtil.toInt(bytes, pos)];
		pos += 4;
		p.blur = FlashUtil.toInt(bytes, pos);
		
		return p;
	}
}
