package edu.flash3388.flashlib.dashboard;

import edu.flash3388.flashlib.dashboard.controls.DashboardPidTuner;
import edu.flash3388.flashlib.gui.Dialog;
import edu.flash3388.flashlib.math.Mathf;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class PidTunerWindow extends Stage{
	
	private static final double DEFAULT_X_RANGE = 10.0;
	private static final double DEFAULT_Y_RANGE = 100.0;
	
	private static PidTunerWindow instance;
	
	private DashboardPidTuner tuner;
	private ComboBox<String> keysBox;
	
	private LineChart<Number, Number> chart;
	private NumberAxis axisx, axisy;
	private Slider pslider, islider, dslider;
	private Label valLbl, pLbl, iLbl, dLbl, timeLbl;
	private TextField spField;
	
	private SimpleStringProperty binderPropSp;
	private SimpleDoubleProperty binderValue;
	private SimpleDoubleProperty binderKp;
	private SimpleDoubleProperty binderKi;
	private SimpleDoubleProperty binderKd;
	private SimpleDoubleProperty binderTime;
	
	private boolean localKUpdate = false;
	
	private double lastX = 0.0, lastY = 0.0;
	private double yrange = DEFAULT_Y_RANGE, xrange = DEFAULT_X_RANGE;
	
	private PidTunerWindow(){
		setTitle("FLASHboard - PID Tuner");
		initStyle(StageStyle.DECORATED);
        initModality(Modality.NONE);
        setResizable(false);
        setScene(loadScene());
        setOnCloseRequest((e)->{
        	instance = null;
        });
	}
	
	private Slider createSlider(){
		Slider pSlider = new Slider();
		pSlider.setShowTickLabels(true);
		pSlider.setShowTickMarks(true);
		pSlider.setSnapToTicks(true);
		pSlider.setMin(0);
		pSlider.setMax(10.0);
		pSlider.setMajorTickUnit(10.0);
		pSlider.setMinorTickCount(100);
		pSlider.setBlockIncrement(0.1);
		
		return pSlider;
	}
	private void attachTuner(){
		binderPropSp.bind(tuner.setPointBindProperty());
		binderKd.bind(tuner.kdProperty());
		binderKi.bind(tuner.kiProperty());
		binderKp.bind(tuner.kpProperty());
		binderValue.bind(tuner.valueBindProperty());
		binderTime.bind(tuner.timeBindProperty());
		chart.getData().add(tuner.getSeries());
	}
	private void disableControls(boolean disable){
		pslider.setDisable(disable);
		islider.setDisable(disable);
		dslider.setDisable(disable);
		spField.setDisable(disable);
	}
	private void resetBinds(){
		binderTime.unbind();
		binderPropSp.unbind();
		binderKd.unbind();
		binderKi.unbind();
		binderKp.unbind();
		binderValue.unbind();
		chart.getData().clear();
		
		
		valLbl.setText("Value: 0.0");
		timeLbl.setText("Time: 0.0 sec");
		
		pslider.setValue(0.0);
		islider.setValue(0.0);
		dslider.setValue(0.0);
		
		xrange = DEFAULT_X_RANGE;
		yrange = DEFAULT_Y_RANGE;
		
		axisx.setUpperBound(xrange);
		axisx.setLowerBound(0.0);
		axisy.setUpperBound(yrange);
		axisy.setLowerBound(0.0);
		
		lastX = 0.0;
		lastY = 0.0;
	}
	private Double validateVal(String val){
		try {
			return Double.parseDouble(val);
		} catch (NumberFormatException e) { return null;}
	}
	private void checkRange(double val){
		if(val - lastY > yrange){
			yrange = val - lastY;
		}
		
		if(val > axisy.getUpperBound() || val < axisy.getLowerBound()){
			double top = Mathf.roundToMultiplier(val, yrange, true);
			axisy.setUpperBound(top);
			axisy.setLowerBound(top - yrange);
		}
		
		lastY = val;
	}
	private void checkRangeTime(double val){
		if(val - lastX > xrange){
			xrange = val - lastX;
		}
		
		if(val > axisx.getUpperBound() || val < axisx.getLowerBound()){
			double top = Mathf.roundToMultiplier(val, xrange, true);
			axisx.setUpperBound(top);
			axisx.setLowerBound(top - xrange);
		}
		
		lastX = val;
	}
	
	private Scene loadScene(){
		axisx = new NumberAxis();
		axisx.setAutoRanging(false);
		axisx.setForceZeroInRange(false);
		//axisx.setLowerBound(0);
		axisy = new NumberAxis();
		axisy.setAutoRanging(false);
		axisy.setForceZeroInRange(false);
		//axisy.setLowerBound(0);
		
		chart = new LineChart<Number, Number>(axisx, axisy);
		chart.setLegendVisible(false);
		
		valLbl = new Label("Value: 0.0");
		binderValue = new SimpleDoubleProperty();
		binderValue.addListener((obs, oldVal, newVal)->{
			if(tuner != null){
				double val = newVal.doubleValue();
				checkRange(val);
				valLbl.setText("Value: "+val);
			}
		});
		
		timeLbl = new Label("Time: 0.0 sec");
		binderTime = new SimpleDoubleProperty();
		binderTime.addListener((obs, oldVal, newVal)->{
			if(tuner != null){
				double val = newVal.doubleValue();
				checkRangeTime(val);
				timeLbl.setText("Time: "+val+" sec");
			}
		});
		
		spField = new TextField("0.0");
		spField.setOnKeyPressed((e)->{
			if(e.getCode() == KeyCode.ENTER){
				if(tuner != null){
					Double val = validateVal(spField.getText());
					if(val == null)
						Dialog.show(this, "Error", "SetPoint value is not a number");
					else 
						tuner.setpointProperty().set(val);
				}
			}
		});
		binderPropSp = new SimpleStringProperty();
		binderPropSp.addListener((obs, oldVal, newVal)->{
			spField.setText(newVal);
		});
		Label spLbl = new Label("Set Point");
		
		pslider = createSlider();
		pslider.valueProperty().addListener((obs, oldVal, newVal)->{
			if(tuner != null){
				if(!localKUpdate){
					localKUpdate = true;
					tuner.kpProperty().set(newVal.doubleValue());
					localKUpdate = false;
				}
				pLbl.setText("Kp: "+Mathf.roundDecimal(newVal.doubleValue(), 4));
			}
		});
		pLbl = new Label("Kp: 0.0");
		binderKp = new SimpleDoubleProperty();
		binderKp.addListener((obs, oldVal, newVal)->{
			if(!localKUpdate){
				localKUpdate = true;
				pslider.setValue(newVal.doubleValue());
				localKUpdate = false;
			}
		});
		
		islider = createSlider();
		islider.valueProperty().addListener((obs, oldVal, newVal)->{
			if(tuner != null){
				if(!localKUpdate){
					localKUpdate = true;
					tuner.kiProperty().set(newVal.doubleValue());
					localKUpdate = false;
				}
				iLbl.setText("Ki: "+Mathf.roundDecimal(newVal.doubleValue(), 4));
			}
		});
		iLbl = new Label("Ki: 0.0");
		binderKi = new SimpleDoubleProperty();
		binderKi.addListener((obs, oldVal, newVal)->{
			if(!localKUpdate){
				localKUpdate = true;
				islider.setValue(newVal.doubleValue());
				localKUpdate = false;
			}
		});
		
		dslider = createSlider();
		dslider.valueProperty().addListener((obs, oldVal, newVal)->{
			if(tuner != null){
				if(!localKUpdate){
					localKUpdate = true;
					tuner.kdProperty().set(newVal.doubleValue());
					localKUpdate = false;
				}
				dLbl.setText("Kd: "+Mathf.roundDecimal(newVal.doubleValue(), 4));
			}
		});
		dLbl = new Label("Kd: 0.0");
		binderKd = new SimpleDoubleProperty();
		binderKd.addListener((obs, oldVal, newVal)->{
			if(!localKUpdate){
				localKUpdate = true;
				dslider.setValue(newVal.doubleValue());
				localKUpdate = false;
			}
		});
		
		keysBox = new ComboBox<String>();
		keysBox.getItems().add("-- Choose Tuner --");
		keysBox.getSelectionModel().select(0);
		keysBox.getItems().addAll(DashboardPidTuner.getTunerNames());
		keysBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue)->{
			disableControls(true);
			resetBinds();
			if(tuner != null){
				tuner.setUpdate(false);
				tuner = null;
			}
			if(keysBox.getSelectionModel().selectedIndexProperty().get() == 0){
				return;
			}
			
			tuner = DashboardPidTuner.getTuner(newValue);
			if(tuner == null){
				keysBox.getSelectionModel().select(0);
				return;
			}
			attachTuner();
			disableControls(false);
			tuner.setUpdate(true);
		});
		
		BorderPane root = new BorderPane();
		
		HBox top = new HBox();
		top.setAlignment(Pos.CENTER);
		top.getChildren().add(keysBox);
		top.setPadding(new Insets(5, 0, 5, 0));
		root.setTop(top);
		
		
		HBox databox = new HBox();
		databox.setSpacing(10);
		databox.setAlignment(Pos.CENTER);
		databox.getChildren().addAll(valLbl, timeLbl);
		VBox center = new VBox();
		center.setSpacing(5);
		center.setAlignment(Pos.CENTER);
		center.getChildren().addAll(databox, chart);
		root.setCenter(center);
		
		BorderPane bottom = new BorderPane();
		root.setBottom(bottom);
		
		HBox kbottom = new HBox();
		kbottom.setSpacing(10);
		kbottom.setPadding(new Insets(0, 5, 0, 5));
		kbottom.setAlignment(Pos.CENTER_LEFT);
		VBox kpbox = new VBox();
		kpbox.getChildren().addAll(pLbl, pslider);
		VBox kibox = new VBox();
		kibox.getChildren().addAll(iLbl, islider);
		VBox kdbox = new VBox();
		kdbox.getChildren().addAll(dLbl, dslider);
		kbottom.getChildren().addAll(kpbox, kibox, kdbox);
		
		HBox spbottom = new HBox();
		spbottom.setSpacing(10);
		spbottom.setPadding(new Insets(0, 5, 0, 5));
		spbottom.setAlignment(Pos.CENTER_RIGHT);
		VBox spbox = new VBox();
		spbox.getChildren().addAll(spLbl, spField);
		spbottom.getChildren().add(spbox);
		
		bottom.setLeft(kbottom);
		bottom.setRight(spbottom);
		
		disableControls(true);
		resetBinds();
		
		return new Scene(root, 800, 500);
	}
	
	public static void showTuner(){
		if(instance != null){
			Dialog.show(Dashboard.getPrimary(), "Error", "Tuner is already open!");
			return;
		}
		instance = new PidTunerWindow();
		instance.show();
	}
	public static void reset(){
		if(instance != null){
			instance.keysBox.getItems().clear();
			instance.keysBox.getItems().add("-- Choose Tuner --");
			instance.keysBox.getSelectionModel().select(0);
			if(instance.tuner != null){
				instance.tuner.setUpdate(false);
				instance.tuner = null;
			}
		}
	}
}
