package edu.flash3388.flashlib.dashboard.controls;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import org.opencv.core.Mat;

import edu.flash3388.flashlib.dashboard.Dashboard;
import edu.flash3388.flashlib.dashboard.Displayable;
import edu.flash3388.flashlib.gui.FlashFXUtils;
import edu.flash3388.flashlib.communications.DataListener;
import edu.flash3388.flashlib.vision.ImagePipeline;
import edu.flash3388.flashlib.vision.cv.CvProcessing;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;

public class CameraViewer extends Displayable implements DataListener, ImagePipeline{
	
	public static enum DisplayMode{
		Normal, PostProcess, Threshold
	}
	
	private ImageView view;
	private Image image;
	private DisplayMode mode = DisplayMode.Normal;
	
	public CameraViewer(String name) {
		super(name, (byte)-1);
		view = new ImageView();
		view.setFitHeight(420);
		view.setFitWidth(640);
		image = new WritableImage(640, 420);
		view.setImage(image);
	}
	
	@Override
	protected void update() {
		if(image != null)
			view.setImage(image);
	}
	@Override
	protected Node getNode(){
		return view;
	}
	@Override
	protected DisplayType getDisplayType(){
		return DisplayType.Cam;
	}

	public void setImage(BufferedImage bf){
		if(mode != DisplayMode.Normal)
			return;
        image = FlashFXUtils.bufferedImage2FxImage(bf);
	}
	public void setMatImage(Mat mat){
		image = FlashFXUtils.cvMat2FxImage(mat);
	}
	
	@Override
	public void newData(byte[] bytes) {
		if(Dashboard.visionInitialized()){
			Mat m = CvProcessing.byteArray2Mat(bytes);
			Dashboard.setForVision(m);
		}
		if(mode == DisplayMode.Normal){
			ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
	        Iterator<?> readers = ImageIO.getImageReadersByFormatName("jpeg");
	 
	        ImageReader reader = (ImageReader) readers.next();
	        Object source = bis; 
	       
			try {
				ImageInputStream iis = ImageIO.createImageInputStream(source); 
			    reader.setInput(iis, true);
			    ImageReadParam param = reader.getDefaultReadParam();
			    BufferedImage image = reader.read(0, param); 
			    setImage(image);
			} catch (IOException e) {
			}
		}

	}
	@Override
	public byte[] dataForTransmition() {return null;}
	@Override
	public boolean hasChanged() {
		return false;
	}
	@Override
	public void onConnection() {}
	@Override
	public void onConnectionLost() {}

	
	public void setDisplayMode(DisplayMode m){
		mode = m;
	}
	@Override
	public void newImage(Object frame, byte type) {
		if((mode == DisplayMode.Threshold && type == ImagePipeline.TYPE_THRESHOLD) ||
				(mode == DisplayMode.PostProcess && type == ImagePipeline.TYPE_POST_PROCESS))
			setMatImage((Mat)frame);
	}
}
