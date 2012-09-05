package XBee.Configurator;

import java.io.Serializable;
import java.util.LinkedList;

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


	public void associateActuatorToSensor(String addrActuator, String addrSensor) {
		
		/*
		 * 
		 * 
		 */
		byte[] addSen=null;
		byte[] addAct=null;
		int posS=0;
		int posA=0;
		
		for (int i = 0; i < xbee.size(); i++) {
			if (xbee.get(i).getAddress().equals(addrSensor)) {
				
//				addActuator(addrActuator, i);
				addSen=xbee.get(i).getAddressByte();
				posS=i;
				
			} else if (xbee.get(i).getAddress().equals(addrActuator)) {
				
//				setSensor(addrSensor, i);
				addAct=xbee.get(i).getAddressByte();
				posA=i;
				
				
			}
		}
		
		if(addSen!= null && addAct!=null){
			this.addActuator(addAct, posS);
			this.setSensor(addSen, posA);
		}
	}
	
	public void associateActuatorToSensor(byte[] addrActuator, byte[] addrSensor) {

		for (int i = 0; i < xbee.size(); i++) {
			if (xbee.get(i).getAddress().equals(addrSensor)) {
				addActuator(addrActuator, i);
			} else if (xbee.get(i).getAddress().equals(addrActuator)) {
				setSensor(addrSensor, i);
			}
		}
	}
	
	/*
	 * Method to add an actuator
	 */
	private void addActuator(String addrActuator, int pos){
		/*
		 * Code to prevent duplicated entries
		 * 1- If the list is empty then adds an actuator without a problem
		 * 2- If it's not empty, it'll check if the address received 
		 *    exists already in the list
		 */
		if (xbee.get(pos).getMyActuators().size() == 0)
			xbee.get(pos).setActuator(addrActuator);
		else {
			int repetitions = 0;
			for (int j = 0; j < xbee.get(pos).getMyActuators().size(); j++) {
				if (xbee.get(pos).getMyActuators().get(j)
						.equals(addrActuator))
					repetitions++;
			}
			if (repetitions == 0)
				xbee.get(pos).setActuator(addrActuator);
		}
	}
	
	private void addActuator(byte[] addrActuator, int pos){
		/*
		 * Code to prevent duplicated entries
		 * 1- If the list is empty then adds an actuator without a problem
		 * 1.1- Adds an actuator in a byte's LinkedList and in a String LinkedList
		 * 2- If it's not empty, it'll check if the address received 
		 *    exists already in the list
		 */
		String s = new AuxiliarMethods().convertByteToString(addrActuator);
		if (xbee.get(pos).getMyActuatorsByte().size() == 0){
			xbee.get(pos).setActuator(addrActuator);
			xbee.get(pos).setActuator(s);
		
		}
		else {
			int repetitions = 0;
			for (int j = 0; j < xbee.get(pos).getMyActuatorsByte().size(); j++) {
				if (xbee.get(pos).getMyActuators().get(j).equals(s))
					repetitions++;
			}
			if (repetitions == 0){
				xbee.get(pos).setActuator(s);
				xbee.get(pos).setActuator(addrActuator);
			}
		}
	}
	
	/*
	 * Method to set a sensor
	 */
//	private void setSensor(String addrSensor, int pos){
//		/*
//		 * Adds a sensor as a string (addrSensor) and then
//		 * searches for a match in the full device list
//		 * it theres a match then retrieves the address in byte
//		 * and then add that address at the same node as the string 
//		 */
//		xbee.get(pos).setSensor(addrSensor);
//		for(int i=0; i<xbee.size(); i++)
//			if(xbee.get(i).equals(addrSensor))
//				xbee.get(pos).setSensorByte(xbee.get(i).getAddressByte());
//	}
	
	private void setSensor(byte[] addrSensor, int pos){
		/*
		 * Adds a sensor as a string (addrSensor) and then
		 * searches for a match in the full device list
		 * it theres a match then retrieves the address in byte
		 * and then add that address at the same node as the string 
		 */
		xbee.get(pos).setSensorByte(addrSensor);
		xbee.get(pos).setSensor(new AuxiliarMethods().convertByteToString(addrSensor));
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
	
	public String getMySensor(int position) {
		return xbee.get(position).getMySensor();
	}
	
	public byte[] getMySensorByte(int position){
		return xbee.get(position).getMySensorByte();
	}
	
	public LinkedList<String> getMyActuators(int position) {
		return xbee.get(position).getMyActuators();
	}
	
	public LinkedList<byte[]> getMyActuatorsByte(int position){
		return xbee.get(position).getMyActuatorsByte();
	}

	/*###############################
	 * 
	 * OTHER METHODS 
	 * 
	 ##############################*/

	public void removeActuatorFromSensor(String addrActuator, String addrSensor) {

		for (int i = 0; i < xbee.size(); i++) {
			if (xbee.get(i).getAddress().equals(addrSensor)
					&& xbee.get(i).getMyActuators().size() > 0) {
				xbee.get(i).removeActuator(addrActuator);
				break;
			}
		}
	}
	
	public void clearList() {
		xbee.clear();
	}

}
