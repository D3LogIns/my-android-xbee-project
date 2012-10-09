package XBee.Configurator;

import java.io.Serializable;
import java.util.LinkedList;

import android.os.Bundle;
import android.util.Log;

public class AuxiliarXBee implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6963040436481928378L;

	LinkedList<XBeeDevice> xbee = new LinkedList<XBeeDevice>();

	// Constructors
	public AuxiliarXBee(LinkedList<XBeeDevice> xbee) {
		this.xbee = xbee;
	}

	public AuxiliarXBee() {

	}

	/*#############################
	 * 
	 * SETTERS
	 * 
	 ############################*/
	
	public void setList(LinkedList<XBeeDevice> xbee) {
		this.xbee = xbee;
	}


	public void setMyActuators(int position, LinkedList<String> actuators) {
		xbee.get(position).setActuators(actuators);
	}
	
	
	/*################################
	 * 
	 * GETTERS
	 * 
	 ###############################*/
	
	public int getListSize() {
		return xbee.size();
	}

	public String getAddress(int pos) {
		return xbee.get(pos).getAddress();
	}

	public byte[] getAddressByte(int position) {
		return xbee.get(position).getAddressByte();
	}

	public String getType(int pos) {
		return xbee.get(pos).getType();
	}
	


	public String getSignalStrength(int pos) {
		return xbee.get(pos).getSignalStrength();
	}

	public LinkedList<XBeeDevice> getList() {
		return xbee;
	}

	public int getNumberOfXBeeType(String s) {
		int count = 0;

		for (int i = 0; i != xbee.size(); i++)
			if (this.getType(i).equals(s))
				count++;

		return count;
	}
	
	
	public LinkedList<String> getMySensor(int position) {
		return xbee.get(position).getMySensor();
	}
	
	public LinkedList<byte[]> getMySensorByte(int position){
		return xbee.get(position).getMySensorByte();
	}
	
	public LinkedList<String> getMyActuators(int position) {
		return xbee.get(position).getMyActuators();
	}
	
	public LinkedList<byte[]> getMyActuatorsByte(int position){
		return xbee.get(position).getMyActuatorsByte();
	}
	
	public LinkedList<String> getMyActuatorsType(int position) {
		return xbee.get(position).getMyActuatorsType();
	}
	
	public XBeeDevice getDevice(int pos){
		return xbee.get(pos);
	}

	/*###############################
	 * 
	 * OTHER METHODS 
	 * 
	 ##############################*/

	public void removeActuatorFromSensor(String addrActuator, String addrSensor) {

		byte[] addAct=null;
		
		int posS=-1;

		
		for (int i = 0; i < xbee.size(); i++) {
			if (xbee.get(i).getAddress().equals(addrSensor) && xbee.get(i).getMyActuators().size() > 0) {
				posS=i;
			}
		}
		
		try {
			addAct=new AuxiliarMethods().convertStringAddressToByte(addrActuator);
			
			xbee.get(posS).removeActuator(addAct, addrActuator);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	
public void associateActuatorToSensor(String addrActuator, String addrSensor, String type) {

		byte[] addAct=null;
		int posS=0;
		
		
		for (int i = 0; i < xbee.size(); i++) {
			if (xbee.get(i).getAddress().equals(addrSensor)) {
				posS=i;	
			}}
		
		try {
			addAct=new AuxiliarMethods().convertStringAddressToByte(addrActuator);
			
			this.addActuator(addAct, posS, type);
		} catch (Exception e) {

			e.printStackTrace();
		}
	}

private void addActuator(byte[] addrActuator, int pos, String type){
		/*
		 * Code to prevent duplicated entries
		 * 1- If the list is empty then adds an actuator without a problem
		 * 1.1- Adds an actuator in a byte's LinkedList and in a String LinkedList
		 * 2- If it's not empty, it'll check if the address received 
		 *    exists already in the list
		 */
		String s = new AuxiliarMethods().convertByteToString(addrActuator);
		
		//if it hasn't any devices associated
		if (xbee.get(pos).getMyActuatorsByte().size() == 0){
			xbee.get(pos).setActuator(addrActuator);
			xbee.get(pos).setActuator(s);
			xbee.get(pos).setActuatorType(type);
			
		}
		//if it has devices associated, searches for repetitions
		else {
			
			int repetitions = 0;
			for (int j = 0; j < xbee.get(pos).getMyActuatorsByte().size(); j++) {
				if (xbee.get(pos).getMyActuators().get(j).equals(s))
					repetitions++;
			}
			if (repetitions == 0){
				xbee.get(pos).setActuator(s);
				xbee.get(pos).setActuator(addrActuator);
				xbee.get(pos).setActuatorType(type);
			}else{
				
			}
			
		}
	}
	

	
	public void clearList() {
		xbee.clear();
	}


}
