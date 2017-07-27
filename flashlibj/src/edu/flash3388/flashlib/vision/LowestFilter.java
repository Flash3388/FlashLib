package edu.flash3388.flashlib.vision;

import java.util.Map;


/**
 * Filers out contours by their proximity to a the lower edge of the frame.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 * @see VisionSource#lowestContours(int)
 */
public class LowestFilter extends ProcessingFilter{
	private byte amount;

	public LowestFilter(){}
	public LowestFilter(int amount){
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
		source.lowestContours(amount);
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
