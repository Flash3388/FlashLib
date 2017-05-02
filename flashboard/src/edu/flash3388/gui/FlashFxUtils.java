package edu.flash3388.gui;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.opencv.core.CvType;
import org.opencv.core.Mat;

import javafx.application.Platform;
import javafx.scene.image.Image;

public class FlashFxUtils {
	private FlashFxUtils(){}
	
	public static void onFxThread(Runnable r){
		Platform.runLater(r);
	}
	public static Image bufferedImage2FxImage(BufferedImage img){
		ByteArrayOutputStream out = new ByteArrayOutputStream();
	    try {
	        ImageIO.write((RenderedImage) img, "jpg", out);
	        out.flush();
        } catch (IOException ex) {
        }
	    ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
	    return new Image(in);
	}
	public static Mat bufferedImage2Mat(BufferedImage image){
		byte[] data = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();  		  
		Mat mat = new Mat(image.getHeight(), image.getWidth(), CvType.CV_8UC3);  		  
		mat.put(0, 0, data);  		  		  
		return mat;  
	}
}
