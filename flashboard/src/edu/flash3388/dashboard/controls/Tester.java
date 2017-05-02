package edu.flash3388.dashboard.controls;

import java.util.Enumeration;
import java.util.Vector;

import edu.flash3388.dashboard.Displayble;
import edu.flash3388.flashlib.communications.Sendable;
import edu.flash3388.flashlib.flashboard.FlashboardSendableType;
import edu.flash3388.flashlib.math.Mathd;
import edu.flash3388.flashlib.util.FlashUtil;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class Tester extends Sendable{
	
	public static class TesterMotor extends Displayble{

		private final SimpleBooleanProperty enabled = new SimpleBooleanProperty(false);
		private final SimpleBooleanProperty brakeMode = new SimpleBooleanProperty(false);
		private final SimpleBooleanProperty cantalon = new SimpleBooleanProperty(false);
		private final SimpleIntegerProperty channel = new SimpleIntegerProperty(-1); 
		private final SimpleDoubleProperty speed = new SimpleDoubleProperty(0.0);
		private final SimpleDoubleProperty current = new SimpleDoubleProperty(0.0);
		private final SimpleDoubleProperty voltage = new SimpleDoubleProperty(0.0);
		
		private byte[][] data = new byte[2][31];
		private int dataIndex = 0;
		private double setSpeed = 0.0;
		private boolean setMode = false;
		private boolean setEnabled = false;
		boolean channelSet = false;
		private boolean modeChanged = false, speedChanged = false, enabledChanged = false, localModeChange = false, updated = false;
		
		public TesterMotor(int id) {
			super("", id, FlashboardSendableType.MOTOR);
		}

		public void setEnable(boolean en){
			enabledChanged = true;
			setEnabled = en;
		}
		public void setMode(int index){
			if(localModeChange) return;
			modeChanged = true;
			setMode = index == 1;
		}
		public void setSpeed(double n){
			if(n > 1) n = 1;
			if(n < -1) n = -1;
			n = Mathd.roundDecimal(n);
			speedChanged = true;
			setSpeed = n;
		}
		public double getSpeed(){
			return speed.get();
		}
		public double getCurrent(){
			return current.get();
		}
		public double getVoltage(){
			return voltage.get();
		}
		public int getChannel(){
			return channel.get();
		}
		public boolean isEnabled(){
			return enabled.get();
		}
		public boolean isBrakeMode(){
			return brakeMode.get();
		}
		
		public SimpleDoubleProperty speedProperty(){
			return speed;
		}
		public SimpleDoubleProperty currentProperty(){
			return current;
		}
		public SimpleDoubleProperty voltageProperty(){
			return voltage;
		}
		public SimpleIntegerProperty channelProperty(){
			return channel;
		}
		public SimpleBooleanProperty enabledProperty(){
			return enabled;
		}
		public SimpleBooleanProperty brakeModeProperty(){
			return brakeMode;
		}
		
		private void update(byte[] bytes){
			if(bytes.length < 31) return;
			
			if(channel.get() == -1)
				channel.set(FlashUtil.toInt(bytes));
			
			boolean enabled = bytes[4] == 1;
			if(this.enabled.get() != enabled) 
				this.enabled.set(enabled);
			
			boolean cantalon = bytes[5] == 1;
			if(this.cantalon.get() != cantalon) 
				this.cantalon.set(cantalon);
			
			boolean brakeMode = bytes[6] == 1;
			if(this.brakeMode.get() != brakeMode) 
				this.brakeMode.set(brakeMode);
			
			double speed = FlashUtil.toDouble(bytes, 7);
			if(this.speed.get() != speed) 
				this.speed.set(speed);
			
			double current = FlashUtil.toDouble(bytes, 15);
			if(this.current.get() != current) 
				this.current.set(current);
			
			double voltage = FlashUtil.toDouble(bytes, 23);
			if(this.voltage.get() != voltage) 
				this.voltage.set(voltage);
			
			if(!speedChanged) setSpeed = speed;
			if(!modeChanged) setMode = brakeMode;
			if(!enabledChanged) setEnabled = enabled;
		}
		
		@Override
		public void newData(byte[] bytes) {
			if(bytes.length < data[1-dataIndex].length) return;
			System.arraycopy(bytes, 0, data[1-dataIndex], 0, data[1-dataIndex].length);
			
			if(!updated){
				dataIndex ^= 1;
				updated = true;
			}
		}
		@Override
		public void update() {
			update(data[dataIndex]);
			updated = false;
		}
		@Override
		public byte[] dataForTransmition() {
			boolean chSpeed = speedChanged && setSpeed != speed.get();
			byte[] bytes = new byte[((modeChanged)? 1 : 0) + ((enabledChanged)? 1 : 0) + ((chSpeed)? 8 : 0)];
			int pos = 0;
			if(enabledChanged)
				bytes[pos++] = (byte) (setEnabled? 1 : 0);
			if(modeChanged) 
				bytes[pos++] = (byte) (setMode? 1 : 0);
			if(chSpeed)
				FlashUtil.fillByteArray(setSpeed, pos, bytes);
			speedChanged = false; modeChanged = false; enabledChanged = false;
			return bytes;
		}
		@Override
		public boolean hasChanged() {
			return modeChanged || enabledChanged || speedChanged;
		}
		@Override
		public void onConnection() {}
		@Override
		public void onConnectionLost() {}
	}
	
	private static Tester instance;
	private Vector<TesterMotor> motors = new Vector<TesterMotor>();
	private byte[] confirm = {0};
	
	public Tester(int id) {
		super("", id, FlashboardSendableType.MOTOR);
	}

	@Override
	public void newData(byte[] bytes) {
	}
	@Override
	public byte[] dataForTransmition() {
		return confirm;
	}
	@Override
	public boolean hasChanged() {
		return false;
	}
	@Override
	public void onConnection() {}
	@Override
	public void onConnectionLost() {}
	
	public void enable(boolean en){
		FlashUtil.getLog().log("Enabled "+en);
		confirm[0] = (byte) (en? 1 : 0);
	}
	public Sendable addMotor(int id){
		TesterMotor m = new TesterMotor(id);
		motors.addElement(m);
		return m;
	}
	public Enumeration<TesterMotor> getMotors(){
		return motors.elements();
	}
	
	public static void init(int id){
		if(instance == null)
			instance = new Tester(id);
	}
	public static Tester getInstance(){
		return instance;
	}
}
/*
public static class Test{
		public static interface TestAction{
			void initialize();
			void execute();
			boolean isFinished();
			ActionResult end();
		}
		public static class TesterMotorResult{
			private TesterMotor motor;
			private double avgVoltage = 0, avgCurrent = 0, avgSpeed = 0;
			private int feeds = 0;
			
			public TesterMotorResult(TesterMotor motor){
				this.motor = motor;
			}
			
			public void feed(){
				avgVoltage += motor.getVoltage();
				avgCurrent += motor.getCurrent();
				avgSpeed += motor.getSpeed();
				feeds++;
			}
			public double getAvgSpeed(){
				return feeds > 0 ? avgSpeed / feeds : 0;
			}
			public double getAvgCurrent(){
				return feeds > 0 ? avgCurrent / feeds : 0;
			}
			public double getAvgVoltage(){
				return feeds > 0 ? avgVoltage / feeds : 0;
			}
		}
		public static class ActionResult{
			private TesterMotorResult motorsResult[];
			private double avgVoltage, avgCurrent;
			
			public ActionResult(TesterMotorResult... results){
				motorsResult = results;
			}
			public ActionResult(TesterMotor...motors){
				motorsResult = new TesterMotorResult[motors.length];
				for(int i = 0; i < motors.length; i++)
					motorsResult[i] = new TesterMotorResult(motors[i]);
			}
			
			public void feed(){
				for(TesterMotorResult r : motorsResult)
					r.feed();
			}
			
			public int motors(){
				return motorsResult.length;
			}
			public TesterMotorResult getMotor(int i){
				return motorsResult[i];
			}
			public double getAvgVoltage(){
				return avgVoltage;
			}
			public double getAvgCurrent(){
				return avgCurrent;
			}
		}
		private static class Entry{
			TestAction action;
			boolean sequential, initialized, running;
		}
		
		public static final Test CURRENT_DRAW = null;
		public static final Test FUNCTIONALITY = null;
		public static final Test BATTERY = null;
		
		private Vector<Entry> actions = new Vector<Entry>();
		private int index = -1;
		private boolean init = false;
		private Vector<Entry> current = new Vector<Entry>();
		
		public Test(){}
		
		public Test addSequential(TestAction action){
			Entry entry = new Entry();
			entry.action = action;
			entry.sequential = true;
			this.actions.add(entry);
			return this;
		}
		public Test addSequential(TestAction... actions){
			for(TestAction action : actions)
				addSequential(action);
			return this;
		}
		public Test addParallel(TestAction action){
			Entry entry = new Entry();
			entry.action = action;
			entry.sequential = false;
			this.actions.add(entry);
			return this;
		}
		public Test addParallel(TestAction... actions){
			for(TestAction action : actions)
				addParallel(action);
			return this;
		}
		
		public void stop(){
			init = false;
			index = -1;
			for(Entry e : actions){
				e.initialized = false;
				e.running = false;
			}
		}
		private boolean run(Entry entry){
			if(!entry.initialized){
				entry.running = true;
				entry.initialized = true;
				entry.action.initialize();
			}
			entry.action.execute();
			return !entry.action.isFinished();
		}
		public void run(){
			if(!init){
				init = true;
				index = 0;
				Entry c = actions.elementAt(index);
				current.addElement(c);
				return;
			}
			
			for(Enumeration<Entry> en = current.elements(); en.hasMoreElements();){
				 Entry entry = en.nextElement();
				 if(!run(entry)){
					 current.remove(entry);
					 entry.running = false;
				 }
			}
			
			Entry c = actions.elementAt(index), toRun = null; boolean next = false;
			if(!c.running){
				current.removeElement(c);
				index++;
				next = true;
			}
			else if(!c.sequential){
				index++;
				next = true;
			}
			
			if(next && index + 1 < actions.size())
				toRun = actions.elementAt(index);
			
			if(toRun != null)
				current.add(toRun);
		}
		public boolean finished(){
			return index >= actions.size();
		}
	}
*/