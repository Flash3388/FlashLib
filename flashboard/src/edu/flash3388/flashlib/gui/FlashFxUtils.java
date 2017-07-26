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
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class FlashFxUtils {
	private FlashFxUtils(){}
	
	public static void onFxThread(Runnable r){
		Platform.runLater(r);
	}
	
	public static void showErrorDialog(Stage owner, String title, String error){
		Alert alert = new Alert(AlertType.ERROR);
		alert.setContentText(error);
		alert.setTitle(title);
		alert.initOwner(owner);
		alert.showAndWait();
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
