package edu.flash3388.flashlib.dashboard.controls;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import edu.flash3388.flashlib.communications.Sendable;
import edu.flash3388.flashlib.flashboard.FlashboardSendableType;
import edu.flash3388.flashlib.flashboard.Tester;

public class FlashboardTester extends Sendable{

	private static HashMap<String, List<FlashboardTesterMotor>> unallocatedMotors = 
			new HashMap<String, List<FlashboardTesterMotor>>();
	private static HashMap<String, FlashboardTester> testers = new HashMap<String, FlashboardTester>();
	
	private Vector<FlashboardTesterMotor> motors = new Vector<FlashboardTesterMotor>();
	private boolean updateEnable = false, enable = false;
	
	public FlashboardTester(String name, int id) {
		super(name, id, FlashboardSendableType.TESTER);
		
		testers.put(name, this);
		List<FlashboardTesterMotor> motors = unallocatedMotors.get(name);
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
	public Enumeration<FlashboardTesterMotor> getMotors(){
		return motors.elements();
	}
	private void addMotor(FlashboardTesterMotor motor){
		motors.addElement(motor);
	}
	
	@Override
	public void newData(byte[] data) {
	}
	@Override
	public byte[] dataForTransmition() {
		updateEnable = false;
		return new byte[]{enable? Tester.START : Tester.STOP};
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
	
	public static void allocateTesterMotor(FlashboardTesterMotor motor){
		String name = motor.getTesterName();
		if(name == null || name.equals("")){
			return;
		}else{
			FlashboardTester tester = testers.get(name);
			if(tester != null)
				tester.addMotor(motor);
			else{
				List<FlashboardTesterMotor> motors = unallocatedMotors.get(name);
				if(motors == null){
					motors = new ArrayList<FlashboardTesterMotor>();
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
