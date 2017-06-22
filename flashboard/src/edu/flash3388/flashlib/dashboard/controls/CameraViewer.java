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
import edu.flash3388.flashlib.dashboard.Displayble;
import edu.flash3388.flashlib.gui.FlashFxUtils;
import edu.flash3388.flashlib.communications.DataListener;
import edu.flash3388.flashlib.vision.CvPipeline;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;

public class CameraViewer extends Displayble implements DataListener, CvPipeline{
	
	public static enum DisplayMode{
		Normal, PostProcess, Threshold
	}
	
	private ImageView view;
	private Image image;
	private Runnable updater;
	private DisplayMode mode = DisplayMode.Normal;
	
	public CameraViewer(String name, int id) {
		super(name, id, (byte)-1);
		view = new ImageView();
		view.setFitHeight(420);
		view.setFitWidth(640);
		image = new WritableImage(640, 420);
		view.setImage(image);
		
		updater = new Runnable(){
			@Override
			public void run() {
				if(image != null)
					view.setImage(image);
			}
		};
	}

	public void setImage(BufferedImage bf){
		if(mode != DisplayMode.Normal)
			return;
		
		/*WritableImage wr = null;
        if (bf != null) {
            wr = new WritableImage(bf.getWidth(), bf.getHeight());
            PixelWriter pw = wr.getPixelWriter();
            for (int x = 0; x < bf.getWidth(); x++) {
                for (int y = 0; y < bf.getHeight(); y++) 
                    pw.setArgb(x, y, bf.getRGB(x, y));
            }
        }*/
        image = FlashFxUtils.bufferedImage2FxImage(bf);
	}
	public void setMatImage(Mat mat){
		image = FlashFxUtils.cvMat2FxImage(mat);
	}
	@Override
	public void newData(byte[] bytes) {
		Dashboard.setForVision(bytes);
		
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
	@Override
	protected Node getNode(){return view;}
	@Override
	public Runnable updateDisplay() {
		return updater;
	}
	@Override
	public DisplayType getDisplayType(){
		return DisplayType.Cam;
	}
	
	public void setDisplayMode(DisplayMode m){
		mode = m;
	}
	@Override
	public void newImage(Mat mat, byte type) {
		if((mode == DisplayMode.Threshold && type == CvPipeline.TYPE_THRESHOLD) ||
				(mode == DisplayMode.PostProcess && type == CvPipeline.TYPE_POST_PROCESS))
			setMatImage(mat);
	}
}
