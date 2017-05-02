package edu.flash3388.gui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Dialog extends Stage {
	
	private final int HEIGHT = 100;
	private final int WIDTH = 350;
	
	public static final int CANCEL = 1;
	public static final int APPLY = 2;
	
	private Dialog(Stage owner, String title, String message, boolean button){
		setTitle(title);
        initStyle(StageStyle.UTILITY);
        initModality(Modality.APPLICATION_MODAL);
        initOwner(owner);
        setResizable(false);
        setScene(createScene(message, button));
	}
	
	private Scene createScene(String message, boolean useBtn){
		BorderPane root = new BorderPane();
		Label messageLbl = new Label(message);
		messageLbl.fontProperty().setValue(Font.font(Font.getDefault().getFamily(), FontWeight.BOLD, 13));
		root.setCenter(messageLbl);
		
		if(useBtn){
			Button ok = new Button("OK");
		
			ok.setPrefWidth(60);
			ok.setPadding(new Insets(5,0,5,0));
			ok.setStyle("-fx-border-insets:5px");
			ok.setOnAction(new EventHandler<ActionEvent>(){
				@Override
				public void handle(ActionEvent event) {
					close();
				}
			});
		
			HBox bottomLayout = new HBox();
			bottomLayout.spacingProperty().set(5);
			bottomLayout.alignmentProperty().set(Pos.CENTER);
			bottomLayout.getChildren().add(ok);
			root.setBottom(bottomLayout);
		}
		return new Scene(root, WIDTH, HEIGHT);
	}
	
	public static void show(Stage owner, String title, String message){
		Dialog d = new Dialog(owner, title, message, true);
		d.showAndWait();
	}
	public static Dialog create(Stage owner, String title, String message){
		return new Dialog(owner, title, message, false);
	}
}
