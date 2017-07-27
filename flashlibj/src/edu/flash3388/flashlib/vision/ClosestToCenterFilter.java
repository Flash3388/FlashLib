package edu.flash3388.flashlib.vision;

import java.util.Map;

/**
 * Filers out contours by their proximity to the center of the frame. 
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 * @see VisionSource#closestToCenterFrame(int)
 */
public class ClosestToCenterFilter extends ProcessingFilter{
	private byte amount = 0;
	
	public ClosestToCenterFilter(){}
	public ClosestToCenterFilter(int amount){
		this.amount = (byte) amount;
	}
	
	public int getAmount(){
		return amount;
	}
	public void setAmount(int amount){
		this.amount = (byte) amount;
	}
	
	@Override
	public void process(VisionSource source) {
		source.closestToCenterFrame(amount);
	}
	@Override
	public void parseParameters(Map<String, VisionParam> parameters) {
		amount = (byte) VisionParam.getIntValue(parameters.get("amount"));
	}
	@Override
	public VisionParam[] getParameters() {
		return new VisionParam[]{
				new VisionParam.IntParam("amount", amount)
		};
	}
}
