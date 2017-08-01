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

	private ValueSource<Object>[] imgs;
	private IntegerProperty scaleFactor = new SimpleIntegerProperty();
	private IntegerProperty method = new SimpleIntegerProperty();
	private Property<String> imgDirPath = new SimpleProperty<String>();
	
	public TemplateFilter(){}
	public TemplateFilter(String imgDirPath, int method, int scaleFactor){
		this.imgDirPath.setValue(imgDirPath);
		this.scaleFactor.set(scaleFactor);
		this.method.set(method);
	}
	
	public IntegerProperty scaleFactorProperty(){
		return scaleFactor;
	}
	public IntegerProperty methodProperty(){
		return method;
	}
	public Property<String> imageDirectoryPathProperty(){
		return imgDirPath;
	}
	
	@Override
	public void process(VisionSource source) {
		if(imgs == null){
			
		}
		//source.matchTemplate(method.get(), scaleFactor.get(), imgs);
	}
}
