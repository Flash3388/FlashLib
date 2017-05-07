package edu.flash3388.flashlib.dashboard;

import java.io.IOException;

import edu.flash3388.flashlib.dashboard.controls.Tester;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class TesterWindow extends Stage{

	private static TesterWindow instance;
	
	private TesterWindowController controller;
	
	private TesterWindow(){
		setTitle("FLASHboard - Tester");
		initStyle(StageStyle.DECORATED);
        initModality(Modality.NONE);
        setResizable(false);
        setScene(loadScene());
        setOnCloseRequest((e)->Tester.getInstance().enable(false));
	}
	
	private Scene loadScene(){
		VBox root = new VBox();
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("TesterWindow.fxml"));
			loader.setRoot(root);
			root = loader.load();
			controller = loader.getController();
			controller.setWindow(this);
			return new Scene(root, 462, 245);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static void showTester(){
		if(instance == null)
			instance = new TesterWindow();
		
		if(!instance.isShowing()){
			instance.show();
			Tester.getInstance().enable(true);
		}
	}
	public static TesterWindow getInstance(){
		return instance;
	}
}
