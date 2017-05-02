package edu.flash3388.flashlib.dashboard.controls;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;

import edu.flash3388.flashlib.dashboard.Dashboard;
import edu.flash3388.flashlib.dashboard.Displayble;
import edu.flash3388.flashlib.communications.DataListener;
import edu.flash3388.flashlib.flashboard.FlashboardSendableType;
import edu.flash3388.flashlib.util.FlashUtil;
import edu.flash3388.flashlib.vision.ImagePipeline;
import edu.flash3388.flashlib.gui.FlashFxUtils;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;

public class CameraViewer extends Displayble implements DataListener, ImagePipeline{
	
	public static enum DisplayMode{
		Normal, PostProcess, Threshold
	}
	
	private ImageView view;
	private Image image;
	private Image timeoutImage;
	private Runnable updater;
	private DisplayMode mode = DisplayMode.Normal;
	private long lastUpdate = -1;
	private boolean timeoutSet = false;
	
	public CameraViewer(String name, int id) {
		super(name, id, FlashboardSendableType.CAM);
		view = new ImageView();
		view.setFitHeight(420);
		view.setFitWidth(640);
		image = new WritableImage(640, 420);
		try {
			timeoutImage = FlashFxUtils.bufferedImage2FxImage(ImageIO.read(new File("data/res/Donald-Trump.jpg")));
		} catch (IOException e) {
			e.printStackTrace();
		}
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
		MatOfByte buffer = new MatOfByte();
		Imgcodecs.imencode(".png", mat, buffer);
		image = new Image(new ByteArrayInputStream(buffer.toArray()));
	}
	@Override
	public void newData(byte[] bytes) {
		ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        Iterator<?> readers = ImageIO.getImageReadersByFormatName("jpeg");
 
        ImageReader reader = (ImageReader) readers.next();
        Object source = bis; 
       
		try {
			ImageInputStream iis = ImageIO.createImageInputStream(source); 
		    reader.setInput(iis, true);
		    ImageReadParam param = reader.getDefaultReadParam();
		    BufferedImage image = reader.read(0, param); 
		    Dashboard.setForVision(image);
		    timeoutSet = false;
		    setImage(image);
		    lastUpdate = FlashUtil.millis();
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

	public boolean updateTimeout(){
		return (lastUpdate > 0 || !Dashboard.communicationsConnected()) && FlashUtil.millis() - lastUpdate > 2000 && !timeoutSet;
	}
	public void setTimeoutDisplay(){
		if(timeoutImage != null){
			image = null;
			view.setImage(timeoutImage);
			FlashUtil.getLog().log("Set timeout image");
			timeoutSet = true;
		}
	}
	public void setDisplayMode(DisplayMode m){
		mode = m;
	}
	@Override
	public void newImage(Mat mat, int type) {
		if((mode == DisplayMode.Threshold && type == ImagePipeline.TYPE_THRESHOLD) ||
				(mode == DisplayMode.PostProcess && type == ImagePipeline.TYPE_POST_PROCESS))
			setMatImage(mat);
	}
}
