package edu.flash3388.flashlib.dashboard.controls;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import edu.flash3388.flashlib.communications.Sendable;
import edu.flash3388.flashlib.flashboard.FlashboardSendableType;
import edu.flash3388.flashlib.flashboard.FlashboardMotorTester;

public class FlashboardTester extends Sendable{

	private static HashMap<String, List<TesterMotorControl>> unallocatedMotors = 
			new HashMap<String, List<TesterMotorControl>>();
	private static HashMap<String, FlashboardTester> testers = new HashMap<String, FlashboardTester>();
	
	private Vector<TesterMotorControl> motors = new Vector<TesterMotorControl>();
	private boolean updateEnable = false, enable = false;
	
	public FlashboardTester(String name) {
		super(name, FlashboardSendableType.TESTER);
		
		testers.put(name, this);
		List<TesterMotorControl> motors = unallocatedMotors.get(name);
		if(motors != null){
			unallocatedMotors.remove(name);
			for (int i = 0; i < motors.size(); i++)
				addMotor(motors.get(i));
		}
	}

	public void enable(boolean enable){
		this.enable = enable;
		updateEnable = true;
	}
	public Enumeration<TesterMotorControl> getMotors(){
		return motors.elements();
	}
	private void addMotor(TesterMotorControl motor){
		motors.addElement(motor);
	}
	
	@Override
	public void newData(byte[] data) {
	}
	@Override
	public byte[] dataForTransmition() {
		updateEnable = false;
		return new byte[]{enable? FlashboardMotorTester.START : FlashboardMotorTester.STOP};
	}
	@Override
	public boolean hasChanged() {
		return updateEnable;
	}
	@Override
	public void onConnection() {
	}
	@Override
	public void onConnectionLost() {
	}
	
	public static void allocateTesterMotor(TesterMotorControl motor){
		String name = motor.getTesterName();
		if(name == null || name.equals("")){
			return;
		}else{
			FlashboardTester tester = testers.get(name);
			if(tester != null)
				tester.addMotor(motor);
			else{
				List<TesterMotorControl> motors = unallocatedMotors.get(name);
				if(motors == null){
					motors = new ArrayList<TesterMotorControl>();
					unallocatedMotors.put(name, motors);
				}
				motors.add(motor);
			}
		}
	}
	public static FlashboardTester getTester(String name){
		return testers.get(name);
	}
	public static String[] getTestersNames(){
		return testers.keySet().toArray(new String[testers.keySet().size()]);
	}
	public static void resetTesters(){
		String[] testers = getTestersNames();
		for (int i = 0; i < testers.length; i++){
			FlashboardTester tester = getTester(testers[i]);
			tester.enable(false);
			tester.motors.clear();
		}
		FlashboardTester.testers.clear();
		unallocatedMotors.clear();
	}
}
