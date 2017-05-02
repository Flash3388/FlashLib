package edu.flash3388.flashlib.vision;

import java.awt.Color;

public class RGB {
	public final int red;
	public final int green;
	public final int blue;
	
	public RGB(Color color){
		this(color.getRed(), color.getGreen(), color.getBlue());
	}
	
	public RGB(int red, int green, int blue){
		this.red = red;
		this.green = green;
		this.blue = blue;
	}
	
	public HSV toHSV(){
		float min, max, delta, h, s, v, r, g, b;
		
		r = (float) (red/255.0);
		g = (float) (green/255.0);
		b = (float) (blue/255.0);
		
		min = Math.min(Math.min(r, g), b);
		max = Math.max(Math.max(r, g), b);
		delta = max - min;
		
		// Value
		v = max; 
		
		//Saturation
		if (max != 0) s = delta / max;
		else s = 0;
		
		//Hue
		if(delta == 0) h = 0;
		else if(r == max) h = ((g - b) / delta) % 6;// between yellow & magenta
		else if(g == max) h = 2 + (b - r) / delta;// between cyan & yellow
		else h = 4 + (r - g) / delta;// between magenta & cyan
		
		h *= 60;// degrees
		
		h %= 360;
		if(h < 0)
			h += 360;
		
		return new HSV((int)h , (int)s * 100, v * 100);
	}
	
	@Override
	public String toString(){
		return "R: "+red+" G: "+green+" B: "+blue;
	}
}
