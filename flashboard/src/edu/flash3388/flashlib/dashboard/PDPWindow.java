package edu.flash3388.flashlib.dashboard;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class PDPWindow extends Stage{
	
	private static PDPWindow instance = null;
	private PDPWindowController controller;
	
	private PDPWindow(){
		setTitle("FLASHBoard - PDP");
		initStyle(StageStyle.DECORATED);
        initModality(Modality.NONE);
        setResizable(false);
        setScene(loadScene());
        setOnCloseRequest((v)->{
        	controller.deselect();
        });
	}
	
	private Scene loadScene(){
		BorderPane root = new BorderPane();
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("PDPWindow.fxml"));
			loader.setRoot(root);
			root = loader.load();
			controller = loader.getController();
			return new Scene(root, 450, 300);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public PDPWindowController getController(){
		return controller;
	}
	public static void showPDP(){
		if(instance == null)
			instance = new PDPWindow();
		
		if(!instance.isShowing())
			instance.show();
	}
	public static PDPWindow getInstance(){
		return instance;
	}
	public static boolean onScreen(){
		return instance != null && instance.isShowing();
	}
	public static void reset(){
		if(instance != null)
			instance.controller.resetTotal();
	}
}
