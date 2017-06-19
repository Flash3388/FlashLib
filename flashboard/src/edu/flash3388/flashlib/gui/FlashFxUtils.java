package edu.flash3388.flashlib.gui;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;

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
	public static Image cvMat2FxImage(Mat mat){
		MatOfByte buffer = new MatOfByte();
		Imgcodecs.imencode(".png", mat, buffer);
		return new Image(new ByteArrayInputStream(buffer.toArray()));
	}
}
