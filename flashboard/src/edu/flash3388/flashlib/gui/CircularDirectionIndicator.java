package edu.flash3388.flashlib.gui;

import edu.flash3388.flashlib.math.Mathf;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.TextAlignment;

public class CircularDirectionIndicator{
	
	private Circle outerCircle;
	//private Circle innerCircle;
	
	private Label valLbl;
	private String name;
	
	private VBox root;
	private Canvas lineBox;
	private double radius;
	
	public CircularDirectionIndicator(String name, double radius){
		this.name = name;
		this.radius = radius;
		
		root = new VBox();
		
		StackPane graphicalData = new StackPane();
		graphicalData.setMaxSize(radius * 2, radius * 2);
		lineBox = new Canvas();
		
		outerCircle = new Circle(graphicalData.getWidth() * 0.5, graphicalData.getHeight() * 0.5, radius);
		outerCircle.setFill(Color.TRANSPARENT);
		outerCircle.setStroke(Color.BLACK);
		/*innerCircle = new Circle(graphicalData.getWidth() * 0.5, graphicalData.getHeight() * 0.5, radius * 0.5);
		innerCircle.setFill(Color.TRANSPARENT);
		innerCircle.setStroke(Color.BLACK);*/
		
		lineBox.setWidth(radius * 2);
		lineBox.setHeight(radius * 2);
		
		graphicalData.getChildren().addAll(outerCircle, lineBox);
		
		valLbl = new Label(name+": 0.0");
		valLbl.setMinWidth(radius * 2);
		valLbl.setTextAlignment(TextAlignment.CENTER);
		
		HBox labalBox = new HBox();
		labalBox.getChildren().add(valLbl);
		labalBox.setAlignment(Pos.CENTER);
		
		root.setAlignment(Pos.CENTER);
		root.setSpacing(2.0);
		root.getChildren().addAll(labalBox, graphicalData);
		
		setValue(0);
	}
	
	public Node getRoot(){
		return root;
	}
	public void setValue(double val){
		
		val = Mathf.roundDecimal(Mathf.translateAngle(val));
		
		valLbl.setText(name+": "+String.valueOf(val));
		
		val -= 90.0;
		val = Mathf.translateAngle(val);
		val = Math.toRadians(val);
		lineBox.getGraphicsContext2D().clearRect(0, 0, lineBox.getWidth(), lineBox.getHeight());
		lineBox.getGraphicsContext2D().strokeLine(lineBox.getWidth() * 0.5, lineBox.getHeight() * 0.5,
				lineBox.getWidth() * 0.5 + radius * Math.cos(val), 
				lineBox.getHeight() * 0.5 + radius * Math.sin(val));
	}
}
