package edu.flash3388.flashlib.vision;

public class HsvFilter extends ProcessingFilter{

	private short minH, maxH, minS, maxS, minV, maxV;

	public HsvFilter(){}
	public HsvFilter(int minH, int maxH, int minS, int maxS, int minV, int maxV){
		this.minH = (short) minH;
		this.maxH = (short) maxH;
		this.maxS = (short) maxS;
		this.minS = (short) minS;
		this.maxV = (short) maxV;
		this.minV = (short) minV;
	}
	
	public void set(int minH, int maxH, int minS, int maxS, int minV, int maxV){
		this.minH = (short) minH;
		this.maxH = (short) maxH;
		this.maxS = (short) maxS;
		this.minS = (short) minS;
		this.maxV = (short) maxV;
		this.minV = (short) minV;
	}
	
	public int getMinH(){
		return minH;
	}
	public void setMinH(int minH){
		this.minH = (short) minH;
	}
	public int getMaxH(){
		return maxH;
	}
	public void setMaxH(int maxH){
		this.maxH = (short) maxH;
	}
	
	public int getMinS(){
		return minS;
	}
	public void setMinS(int minS){
		this.minS = (short) minS;
	}
	public int getMaxS(){
		return maxS;
	}
	public void setMaxS(int maxS){
		this.maxS = (short) maxS;
	}
	
	public int getMinV(){
		return minV;
	}
	public void setMinV(int minV){
		this.minV = (short) minV;
	}
	public int getMaxV(){
		return maxV;
	}
	public void setMaxV(int maxV){
		this.maxV = (short) maxV;
	}
	
	@Override
	public void process(VisionSource source) {
		source.filterHsv(minH, minS, minV, maxH, maxS, maxV);
	}
	@Override
	public void parseParameters(double[] parameters) {
		if(parameters.length != 6)
			throw new IllegalArgumentException("Arguments invalid! Require 6");
		
		minH = (short) parameters[0];
		maxH = (short) parameters[1];
		minS = (short) parameters[2];
		maxS = (short) parameters[3];
		minV = (short) parameters[4];
		maxV = (short) parameters[5];
	}
	@Override
	public double[] getParameters() {
		return new double[] {minH, maxH, minS, maxS, minV, maxV};
	}
}
