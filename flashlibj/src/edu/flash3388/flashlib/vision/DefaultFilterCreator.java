package edu.flash3388.flashlib.vision;

/**
 * The default creator object for processing filters. Can load and create all basic filters provided with 
 * FlashLib.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 * @see FilterCreator
 */
public class DefaultFilterCreator implements FilterCreator{

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProcessingFilter create(String name) {
		switch (name) {
			case "highest": return new HighestFilter();
			case "lowest": return new LowestFilter();
			case "largest": return new LargestFilter();
			case "shape": return new ShapeFilter();
			case "ratio": return new RatioFilter();
			case "closest-left": return new ClosestToLeftFilter();
			case "closest-right": return new ClosestToRightFilter();
			case "closest-center": return new ClosestToCenterFilter();
			case "coordinate": return new CoordinateFilter();
			case "color": return new ColorFilter();
			case "gray": return new GrayFilter();
			case "circle": return new CircleFilter();
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getSaveName(ProcessingFilter filter) {
		if(filter instanceof HighestFilter)
			return "highest";
		if(filter instanceof LowestFilter)
			return "lowest";
		if(filter instanceof LargestFilter)
			return "largest";
		if(filter instanceof ShapeFilter)
			return "shape";
		if(filter instanceof RatioFilter)
			return "ratio";
		if(filter instanceof ClosestToLeftFilter)
			return "closest-left";
		if(filter instanceof ClosestToRightFilter)
			return "closest-right";
		if(filter instanceof ClosestToCenterFilter)
			return "closest-center";
		if(filter instanceof CoordinateFilter)
			return "coordinate";
		if(filter instanceof ColorFilter)
			return "color";
		if(filter instanceof GrayFilter)
			return "gray";
		if(filter instanceof CircleFilter)
			return "circle";
		return null;
	}

}
