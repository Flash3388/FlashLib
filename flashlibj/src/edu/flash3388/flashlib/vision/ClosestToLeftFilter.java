package edu.flash3388.flashlib.vision;

import java.util.Map;

/**
 * Filers out contours by their proximity to the left side of the frame. 
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 * @see VisionSource#closestToLeft(int)
 */
public class ClosestToLeftFilter extends ProcessingFilter{
	private byte amount;

	public ClosestToLeftFilter(){}
	public ClosestToLeftFilter(int amount){
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
		if(amount <= 0)
			amount = 1;
		source.closestToLeft(amount);
	}
	@Override
	public void parseParameters(Map<String, FilterParam> parameters) {
		amount = (byte) FilterParam.getIntValue(parameters.get("amount"));
	}
	@Override
	public FilterParam[] getParameters() {
		return new FilterParam[]{
				new FilterParam.IntParam("amount", amount)
		};
	}
}
