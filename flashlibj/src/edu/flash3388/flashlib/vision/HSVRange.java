package edu.flash3388.flashlib.vision;

public class HSVRange {
	public final HSV min;
	public final HSV max;
	
	public HSVRange(HSV min, HSV max){
		this.min = min;
		this.max = max;
	}
	public HSVRange(RGB min, RGB max){
		this(min.toHSV().toByteForm(), max.toHSV().toByteForm());
	}
	public HSVRange(Range h, Range s, Range v){
		this(new HSV(h.start, s.start, v.start), new HSV(h.end, s.end, v.end));
	}
	
	@Override
	public String toString(){
		return min.toString() + " -> " + max.toString();
	}
}
