package edu.flash3388.flashlib.vision;

import edu.flash3388.flashlib.util.FlashUtil;

public class DefaultFilterCreator implements FilterCreator{

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
		}
		return null;
	}

	@Override
	public String getSaveName(ProcessingFilter filter) {
		if(FlashUtil.instanceOf(filter, HighestFilter.class))
			return "highest";
		if(FlashUtil.instanceOf(filter, LowestFilter.class))
			return "lowest";
		if(FlashUtil.instanceOf(filter, LargestFilter.class))
			return "largest";
		if(FlashUtil.instanceOf(filter, ShapeFilter.class))
			return "shape";
		if(FlashUtil.instanceOf(filter, RatioFilter.class))
			return "ratio";
		if(FlashUtil.instanceOf(filter, ClosestToLeftFilter.class))
			return "closest-left";
		if(FlashUtil.instanceOf(filter, ClosestToRightFilter.class))
			return "closest-right";
		if(FlashUtil.instanceOf(filter, ClosestToCenterFilter.class))
			return "closest-center";
		if(FlashUtil.instanceOf(filter, CoordinateFilter.class))
			return "coordinate";
		if(FlashUtil.instanceOf(filter, ColorFilter.class))
			return "color";
		return null;
	}

}
