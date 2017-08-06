package edu.flash3388.flashlib.vision;

import java.io.File;
import java.util.ArrayList;

import edu.flash3388.flashlib.util.beans.DoubleProperty;
import edu.flash3388.flashlib.util.beans.IntegerProperty;
import edu.flash3388.flashlib.util.beans.Property;
import edu.flash3388.flashlib.util.beans.SimpleDoubleProperty;
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
	private DoubleProperty scaleFactor = new SimpleDoubleProperty();
	private IntegerProperty method = new SimpleIntegerProperty();
	private Property<String> imgDirPath = new SimpleProperty<String>();
	
	public TemplateFilter(){}
	public TemplateFilter(String imgDirPath, int method, double scaleFactor){
		this.imgDirPath.setValue(imgDirPath);
		this.scaleFactor.set(scaleFactor);
		this.method.set(method);
	}
	
	public DoubleProperty scaleFactorProperty(){
		return scaleFactor;
	}
	public IntegerProperty methodProperty(){
		return method;
	}
	public Property<String> imageDirectoryPathProperty(){
		return imgDirPath;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void process(VisionSource source) {
		if(imgs == null){
			File dir = new File(imgDirPath.getValue());
			if(!dir.exists() || !dir.isDirectory())
				return;
			File[] files = dir.listFiles();
			ArrayList<ValueSource<Object>> imgList = new ArrayList<ValueSource<Object>>();
			for (int i = 0; i < files.length; i++) {
				ValueSource<Object> img = source.loadImage(files[i].getAbsolutePath());
				if(img != null && img.getValue() != null)
					imgList.add(img);
			}
			imgs = new ValueSource[imgList.size()];
			imgList.toArray(imgs);
		}
		source.matchTemplate(imgs, method.get(), scaleFactor.get());
	}
}
