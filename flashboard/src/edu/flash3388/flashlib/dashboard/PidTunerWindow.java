package edu.flash3388.flashlib.dashboard;

import edu.flash3388.flashlib.dashboard.controls.DashboardPidTuner;
import edu.flash3388.flashlib.gui.FlashFxUtils;
import edu.flash3388.flashlib.math.Mathf;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Button;
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
	
	private static enum PidTunningMode{
		P, PI, PD, ClassicPID, PessenIntegralRule, SomeOvershoot, NoOvershoot
	}
	private static abstract class PidTunningParam{
		double kp, ki, kd;
		
		abstract void calc(double ku, double tu);
	}
	
	
	private static final double DEFAULT_X_RANGE = 5.0;
	private static final double DEFAULT_Y_RANGE = 100.0;
	
	private static PidTunerWindow instance;
	private static PidTunningParam[] tunningParams;
	
	private DashboardPidTuner tuner;
	private ComboBox<String> keysBox;
	
	private LineChart<Number, Number> chart;
	private NumberAxis axisx, axisy;
	private Slider pslider, islider, dslider, fslider;
	private Label valLbl, pLbl, iLbl, dLbl, fLbl, timeLbl;
	private TextField spField;
	
	private SimpleStringProperty binderPropSp;
	private SimpleDoubleProperty binderValue;
	private SimpleDoubleProperty binderKp;
	private SimpleDoubleProperty binderKi;
	private SimpleDoubleProperty binderKd;
	private SimpleDoubleProperty binderKf;
	private SimpleDoubleProperty binderTime;
	private SimpleDoubleProperty binderPeriod;
	
	private boolean localKUpdate = false;
	
	private double lastX = 0.0, lastY = 0.0;
	private double yrange = DEFAULT_Y_RANGE, xrange = DEFAULT_X_RANGE;
	private double kuValue;
	
	private PidTunerWindow(){
		setTitle("FLASHboard - PID Tuner");
		initStyle(StageStyle.DECORATED);
        initModality(Modality.NONE);
        setResizable(false);
        setScene(loadScene());
        setOnCloseRequest((e)->{
        	if(tuner != null)
        		tuner.setUpdate(false);
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
		pSlider.setMinorTickCount(99);
		pSlider.setBlockIncrement(0.1);
		
		return pSlider;
	}
	private void setSlider(Slider slider, double max, int ticks){
		slider.setMax(max);
		slider.setMajorTickUnit(max);
		slider.setMinorTickCount(ticks - 1);
		slider.setBlockIncrement(max / ticks);
	}
	
	private void attachTuner(){
		binderPropSp.bind(tuner.setPointBindProperty());
		binderKd.bind(tuner.kdProperty());
		binderKi.bind(tuner.kiProperty());
		binderKp.bind(tuner.kpProperty());
		binderKf.bind(tuner.kfProperty());
		binderValue.bind(tuner.valueBindProperty());
		binderTime.bind(tuner.timeBindProperty());
		binderPeriod.bind(tuner.periodBindProperty());
		
		setSlider(pslider, tuner.getSliderMaxValue(), tuner.getSliderTicks());
		setSlider(islider, tuner.getSliderMaxValue(), tuner.getSliderTicks());
		setSlider(dslider, tuner.getSliderMaxValue(), tuner.getSliderTicks());
		setSlider(fslider, tuner.getSliderMaxValue(), tuner.getSliderTicks());
		
		chart.getData().add(tuner.getSeries());
	}
	private void disableControls(boolean disable){
		pslider.setDisable(disable);
		islider.setDisable(disable);
		dslider.setDisable(disable);
		fslider.setDisable(disable);
		spField.setDisable(disable);
	}
	private void resetBinds(){
		binderTime.unbind();
		binderPropSp.unbind();
		binderKd.unbind();
		binderKi.unbind();
		binderKp.unbind();
		binderKf.unbind();
		binderValue.unbind();
		binderPeriod.unbind();
		chart.getData().clear();
		
		
		valLbl.setText("Value: 0.0");
		timeLbl.setText("Time: 0.0 sec");
		
		pslider.setValue(0.0);
		islider.setValue(0.0);
		dslider.setValue(0.0);
		fslider.setValue(0.0);
		
		spField.setText("0.0");
		
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
			System.out.println("Increasing range");
		}
		
		if(val > axisy.getUpperBound() || val < axisy.getLowerBound()){
			//double top = Mathf.roundToMultiplier(val, yrange, true);
			axisy.setUpperBound(val + yrange);
			axisy.setLowerBound(val - yrange);
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
		chart.setAnimated(false);
		
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
		
		Label periodLbl = new Label("Period: 0.0 sec");
		binderPeriod = new SimpleDoubleProperty();
		binderPeriod.addListener((obs, oldVal, newVal)->{
			if(tuner != null){
				periodLbl.setText("Period: "+Mathf.roundDecimal(newVal.doubleValue(), 3)+" sec");
			}
		});
		
		spField = new TextField("0.0");
		spField.setOnKeyPressed((e)->{
			if(e.getCode() == KeyCode.ENTER){
				if(tuner != null){
					Double val = validateVal(spField.getText());
					if(val == null)
						FlashFxUtils.showErrorDialog(this, "Error", "SetPoint value is not a number");
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
		
		fslider = createSlider();
		fslider.valueProperty().addListener((obs, oldVal, newVal)->{
			if(tuner != null){
				if(!localKUpdate){
					localKUpdate = true;
					tuner.kfProperty().set(newVal.doubleValue());
					localKUpdate = false;
				}
				fLbl.setText("Kf: "+Mathf.roundDecimal(newVal.doubleValue(), 4));
			}
		});
		fLbl = new Label("Kf: 0.0");
		binderKf = new SimpleDoubleProperty();
		binderKf.addListener((obs, oldVal, newVal)->{
			if(!localKUpdate){
				localKUpdate = true;
				fslider.setValue(newVal.doubleValue());
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
		databox.getChildren().addAll(valLbl, timeLbl, periodLbl);
		VBox center = new VBox();
		center.setSpacing(5);
		center.setAlignment(Pos.CENTER);
		center.getChildren().addAll(databox, chart);
		root.setCenter(center);
		
		VBox tunningControls = new VBox();
		root.setLeft(tunningControls);
		tunningControls.setSpacing(10);
		tunningControls.setPadding(new Insets(0, 10, 0, 10));
		tunningControls.setAlignment(Pos.CENTER);
		
		Label kuLbl = new Label("Ku");
		TextField kuField = new TextField();
		kuField.setOnKeyPressed((e)->{
			if(e.getCode() == KeyCode.ENTER){
				Double val = validateVal(kuField.getText());
				if(val == null)
					FlashFxUtils.showErrorDialog(this, "Error", "Ku value is not a number");
				else 
					kuValue = val;
			}
		});		
		Button computeKu = new Button("Compute Ku");
		computeKu.setOnAction((e)->{
			
		});
		computeKu.setDisable(true);//UNTIL IT IS FUNCTIONAL
		VBox kufieldbox = new VBox();
		kufieldbox.getChildren().addAll(kuLbl, kuField, computeKu);
		
		if(tunningParams == null)
			createTunningParams();
		ComboBox<PidTunningMode> tuneModeBox = new ComboBox<PidTunningMode>();
		tuneModeBox.getItems().addAll(PidTunningMode.values());
		tuneModeBox.getSelectionModel().select(0);
	
		Button tunepid = new Button("Tune PID");
		tunepid.setOnAction((e)->{
			if(tuner != null){
				Double val = validateVal(kuField.getText());
				if(val == null){
					FlashFxUtils.showErrorDialog(this, "Error", "Ku value is not a number");
					return;
				}
				kuValue = val;
				
				int index = tuneModeBox.getSelectionModel().getSelectedIndex();
				PidTunningParam param = tunningParams[index];
				param.calc(kuValue, binderPeriod.get());
				
				pslider.setValue(param.kp);
				islider.setValue(param.ki);
				dslider.setValue(param.kd);
			}
		});
		tunningControls.getChildren().addAll(kufieldbox, tuneModeBox, tunepid);
		tunningControls.setMaxWidth(150);
		
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
		VBox kfbox = new VBox();
		kfbox.getChildren().addAll(fLbl, fslider);
		kbottom.getChildren().addAll(kpbox, kibox, kdbox, kfbox);
		
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
	
	private static void createTunningParams(){
		PidTunningMode[] modes = PidTunningMode.values();
		tunningParams = new PidTunningParam[modes.length];
		for (int i = 0; i < modes.length; i++) {
			tunningParams[i] = createTunningParam(modes[i]);
		}
	}
	private static PidTunningParam createTunningParam(PidTunningMode mode){
		switch(mode){
			case ClassicPID: return new PidTunningParam(){
				@Override
				public void calc(double ku, double tu) {
					this.kp = 0.6 * ku;
					this.ki = 2 / tu * kp;
					this.kd = tu / 8 * kp;
				}
			};
			case NoOvershoot: return new PidTunningParam(){
				@Override
				public void calc(double ku, double tu) {
					this.kp = 0.2 * ku;
					this.ki = 2 / tu * kp;
					this.kd = tu / 3 * kp;
				}
			};
			case P: return new PidTunningParam(){
				@Override
				public void calc(double ku, double tu) {
					this.kp = 0.5 * ku;
					this.ki = 0;
					this.kd = 0;
				}
			};
			case PD: return new PidTunningParam(){
				@Override
				public void calc(double ku, double tu) {
					this.kp = 0.8 * ku;
					this.ki = 0.0;
					this.kd = tu / 8 * kp;
				}
			};
			case PI: return new PidTunningParam(){
				@Override
				public void calc(double ku, double tu) {
					this.kp = 0.45 * ku;
					this.ki = 1.2 / tu * kp;
					this.kd = 0.0;
				}
			};
			case PessenIntegralRule:  return new PidTunningParam(){
				@Override
				public void calc(double ku, double tu) {
					this.kp = 0.7 * ku;
					this.ki = 2.5 / tu * kp;
					this.kd = 3 * tu / 20 * kp;
				}
			};
			case SomeOvershoot: return new PidTunningParam(){
				@Override
				public void calc(double ku, double tu) {
					this.kp = 0.33 * ku;
					this.ki = 2 / tu * kp;
					this.kd = tu / 3 * kp;
				}
			};
		}
		return null;
	}
	public static void showTuner(){
		if(instance != null){
			FlashFxUtils.showErrorDialog(Dashboard.getPrimary(), "Error", "Tuner is already open!");
			return;
		}
		instance = new PidTunerWindow();
		instance.show();
	}
	public static void reset(){
		if(instance != null){
			if(instance.tuner != null){
				instance.tuner.setUpdate(false);
				instance.tuner = null;
			}
			instance.close();
			instance = null;
		}
	}
}
