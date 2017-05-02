package edu.flash3388.flashlib.vision;

public class HSV {
	public final int hue;
	public final int saturation;
	public final float value;
	
	public HSV(int hue, int saturation, float value){
		this.hue = hue;
		this.saturation = saturation;
		this.value = value;
	}
	
	public HSV toByteForm(){
		return new HSV(hue/360*255, saturation/100*255, (int)(value/100*255));
	}
	
	public RGB toRGB(){
		float v, s, c, x, m = 0, r = 0, g = 0, b = 0;
		
		v = value / 100; 
		s = saturation / 100;
		
		c = v * s;
		x = (float) (c * (1 - Math.abs((hue / 60.0) % 2 - 1)));
		
		switch((int)(hue / 60)){
			case 0:
				r = c; g = x; b = 0;
				break;
			case 1:
				r = x; g = c; b = 0;
				break;
			case 2:
				r = 0; g = c; b = x;
				break;
			case 3:
				r = 0; g = x; b = c;
				break;
			case 4:
				r = x; g = 0; b = c;
				break;
			case 5:
				r = c; g = 0; b = x;
				break;
		}
		
		return new RGB((int)((r+m)*255), (int)((g+m)*255), (int)((b+m)*255));
	}
	
	@Override
	public String toString(){
		return "H: "+hue+" S: "+saturation+" V: "+value;
	}
}
