package edu.flash3388.flashlib.vision;

import edu.flash3388.flashlib.util.FlashUtil;

public class DefaultFilterCreator implements FilterCreator{

	@Override
	public ProcessingFilter create(int id) {
		switch (id) {
			case 3: return new HighestFilter();
			case 4: return new LowestFilter();
			case 5: return new LargestFilter();
			case 6: return new ShapeFilter();
			case 7: return new RatioFilter();
			case 8: return new ClosestToLeftFilter();
			case 9: return new ClosestToRightFilter();
			case 10: return new ClosestToCenterFilter();
			case 11: return new CoordinateFilter();
			case 12: return new ColorFilter();
		}
		return null;
	}

	@Override
	public byte getSaveId(ProcessingFilter filter) {
		if(FlashUtil.instanceOf(filter, HighestFilter.class))
			return 3;
		if(FlashUtil.instanceOf(filter, LowestFilter.class))
			return 4;
		if(FlashUtil.instanceOf(filter, LargestFilter.class))
			return 5;
		if(FlashUtil.instanceOf(filter, ShapeFilter.class))
			return 6;
		if(FlashUtil.instanceOf(filter, RatioFilter.class))
			return 7;
		if(FlashUtil.instanceOf(filter, ClosestToLeftFilter.class))
			return 8;
		if(FlashUtil.instanceOf(filter, ClosestToRightFilter.class))
			return 9;
		if(FlashUtil.instanceOf(filter, ClosestToCenterFilter.class))
			return 10;
		if(FlashUtil.instanceOf(filter, CoordinateFilter.class))
			return 11;
		if(FlashUtil.instanceOf(filter, ColorFilter.class))
			return 12;
		return 0;
	}

}
