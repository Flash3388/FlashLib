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
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class LineChartControl extends Displayable{

	public static final double WIDTH = 60.0;
	public static final double HEIGHT = 50.0;
	
	private VBox root;
	
	private LineChart<Number,Number> chart;
	private LineChart.Series<Number,Number> chartSeries;
	private NumberAxis axisX;
	private NumberAxis axisY;
	
	private Vector<Data<Number, Number>> dataCollection = new Vector<Data<Number, Number>>();
	
	private Object configMutex = new Object();
	private double minY = 0.0, maxY = 1.0, xRange = 10.0;
	private boolean configUpdate = false;
	
	public LineChartControl(String name) {
		super(name, FlashboardSendableType.LINECHART);
		
		axisX = new NumberAxis();
		axisX.setForceZeroInRange(false);
		axisX.setAutoRanging(false);
		axisX.setUpperBound(xRange);
		axisX.setLowerBound(0.0);
		axisY = new NumberAxis();
		axisY.setForceZeroInRange(false);
		axisY.setAutoRanging(false);
		axisY.setUpperBound(maxY);
		axisY.setLowerBound(minY);
		
		chart = new LineChart<Number, Number>(axisX, axisY);
		chart.setLegendVisible(false);
		chart.setAnimated(false);
		
		chartSeries = new LineChart.Series<Number, Number>();
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
				
				double top = Mathf.roundToMultiplier(axisX.getLowerBound(), xRange, true);		
				
				axisX.setLowerBound(top - xRange);
				axisX.setUpperBound(top);
				
				axisY.setUpperBound(maxY);
				axisY.setLowerBound(minY);
			}
		}
		synchronized (dataCollection) {
			if(!dataCollection.isEmpty()){
				Enumeration<Data<Number, Number>> denum = dataCollection.elements();
				while(denum.hasMoreElements()){
					Data<Number, Number> data = denum.nextElement();
					
					double x = data.getXValue().doubleValue();
					if(x > axisX.getUpperBound() || x < axisX.getLowerBound()){
						double top = Mathf.roundToMultiplier(x, xRange, true);		
						
						axisX.setLowerBound(top - xRange);
						axisX.setUpperBound(top);
					}
					
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
				xRange = FlashUtil.toDouble(data, 1);
				minY = FlashUtil.toDouble(data, 9);
				maxY = FlashUtil.toDouble(data, 17);
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
