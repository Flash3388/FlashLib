package edu.flash3388.flashlib.vision;

public class Range {
	public int end;
	public int start;
	
	public Range(int min, int max){
		this.end = max;
		this.start = min;
	}
	
	@Override
	public String toString(){
		return String.valueOf(start) + " -> " + String.valueOf(end);
	}
	public boolean equals(Range r){
		return start == r.start && end == r.end;
	}
}
