package edu.flash3388.flashlib.vision;

import edu.flash3388.flashlib.util.beans.IntegerProperty;
import edu.flash3388.flashlib.util.beans.Property;
import edu.flash3388.flashlib.util.beans.ValueSource;
import edu.flash3388.flashlib.util.beans.SimpleIntegerProperty;
import edu.flash3388.flashlib.util.beans.SimpleProperty;

/**
 * Filters for a part of the image which matches a given template.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.1
 */
public class TemplateFilter extends VisionFilter{

	private ValueSource<Object> img;
	private IntegerProperty scaleFactor = new SimpleIntegerProperty();
	private IntegerProperty method = new SimpleIntegerProperty();
	private Property<String> imgPath = new SimpleProperty<String>();
	
	public TemplateFilter(){}
	public TemplateFilter(String imgPath, int method, int scaleFactor){
		this.imgPath.setValue(imgPath);
		this.scaleFactor.set(scaleFactor);
		this.method.set(method);
	}
	
	public IntegerProperty scaleFactorProperty(){
		return scaleFactor;
	}
	public IntegerProperty methodProperty(){
		return method;
	}
	public Property<String> imagePathProperty(){
		return imgPath;
	}
	
	@Override
	public void process(VisionSource source) {
		if(img == null)
			img = source.loadImage(imgPath.getValue());
		source.matchTemplate(img, method.get(), scaleFactor.get());
	}
}
