package edu.flash3388.flashlib.dashboard.controls;

import java.util.Enumeration;
import java.util.Vector;

import edu.flash3388.flashlib.dashboard.Displayable;
import edu.flash3388.flashlib.flashboard.FlashboardXYChart;
import edu.flash3388.flashlib.flashboard.FlashboardSendableType;
import edu.flash3388.flashlib.math.Mathf;
import edu.flash3388.flashlib.util.FlashUtil;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class AreaChartControl extends Displayable{

	public static final double WIDTH = 60.0;
	public static final double HEIGHT = 50.0;
	
	private VBox root;
	
	private AreaChart<Number,Number> chart;
	private AreaChart.Series<Number,Number> chartSeries;
	private NumberAxis axisX;
	private NumberAxis axisY;
	
	private Vector<Data<Number, Number>> dataCollection = new Vector<Data<Number, Number>>();
	
	private Object configMutex = new Object();
	private double minY = 0.0, maxY = 1.0, minX = 0.0, maxX = 1.0;
	private boolean configUpdate = false;
	
	private double rangeY = 1.0, rangeX = 1.0;
	
	public AreaChartControl(String name) {
		super(name, FlashboardSendableType.AREACHART);
		
		axisX = new NumberAxis();
		axisX.setForceZeroInRange(false);
		axisX.setAutoRanging(false);
		axisX.setUpperBound(maxX);
		axisX.setLowerBound(minX);
		axisY = new NumberAxis();
		axisY.setForceZeroInRange(false);
		axisY.setAutoRanging(false);
		axisY.setUpperBound(maxY);
		axisY.setLowerBound(minY);
		
		chart = new AreaChart<Number, Number>(axisX, axisY);
		chart.setLegendVisible(false);
		chart.setAnimated(false);
		
		chartSeries = new AreaChart.Series<Number, Number>();
		chart.setTitle(name);
		chart.getData().add(chartSeries);
		
		root = new VBox();
		root.setAlignment(Pos.CENTER);
		root.setMaxSize(WIDTH, HEIGHT);
		root.getChildren().add(chart);
	}

	@Override
	protected Node getNode() {
		return root;
	}
	@Override
	protected DisplayType getDisplayType() {
		return DisplayType.GraphicData;
	}
	@Override
	protected void update() {
		synchronized (configMutex) {
			if(configUpdate){
				configUpdate = false;	
				
				axisX.setLowerBound(maxX);
				axisX.setUpperBound(minX);
				rangeX = maxX - minX;
				
				axisY.setUpperBound(maxY);
				axisY.setLowerBound(minY);
				rangeY = maxY - minY;
			}
		}
		synchronized (dataCollection) {
			if(!dataCollection.isEmpty()){
				Enumeration<Data<Number, Number>> denum = dataCollection.elements();
				while(denum.hasMoreElements()){
					Data<Number, Number> data = denum.nextElement();
					
					double x = data.getXValue().doubleValue();
					if(x > axisX.getUpperBound() || x < axisX.getLowerBound()){
						double top = Mathf.roundToMultiplier(x, rangeX, true);		
						
						axisX.setLowerBound(top - rangeX);
						axisX.setUpperBound(top);
					}
					double y = data.getYValue().doubleValue();
					if(y > axisY.getUpperBound() || y < axisY.getLowerBound()){
						double top = Mathf.roundToMultiplier(y, rangeY, true);		
						
						axisY.setLowerBound(top - rangeY);
						axisY.setUpperBound(top);
					}
					
					//chart.setTitle(getName()+": "+Mathf.roundDecimal(y));
					chart.setTitle(getName()+": ("+Mathf.roundDecimal(y)+", "+Mathf.roundDecimal(x)+")");
					chartSeries.getData().add(data);
					((StackPane)data.getNode()).setVisible(false);
				}
				dataCollection.clear();
			}
		}
	}
	
	@Override
	public void newData(byte[] data) {
		if(data[0] == FlashboardXYChart.VALUE_UPDATE){
			synchronized (dataCollection) {
				double x = FlashUtil.toDouble(data, 1);
				double y = FlashUtil.toDouble(data, 9);
				
				dataCollection.add(new Data<Number, Number>(x, y));
			}
		}
		if(data[0] == FlashboardXYChart.CONFIG_UPDATE){
			synchronized (configMutex) {
				minX = FlashUtil.toDouble(data, 1);
				maxX = FlashUtil.toDouble(data, 9);
				minY = FlashUtil.toDouble(data, 17);
				maxY = FlashUtil.toDouble(data, 25);
				configUpdate = true;
			}
		}
	}

	@Override
	public byte[] dataForTransmition() {
		return null;
	}
	@Override
	public boolean hasChanged() {
		return false;
	}
	@Override
	public void onConnection() {
	}
	@Override
	public void onConnectionLost() {
	}
}
